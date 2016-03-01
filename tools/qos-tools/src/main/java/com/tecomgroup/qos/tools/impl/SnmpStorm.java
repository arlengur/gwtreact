/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.tools.ToolsUtil;

/**
 * @author sviyazov.a
 * 
 */
@Component
public class SnmpStorm implements QoSTool {

	private static class SnmpRequestScenario {
		/*
		 * Should be only "GET" or "GETNEXT"
		 */
		public String requestType;
		public int timesToRepeat;
		public String startOID;
		public String endOID;
	}

	private Snmp snmp;

	private final static Logger LOGGER = Logger.getLogger(SnmpStorm.class);

	@Value("${snmp.request.timeout.millisec}")
	private int requestTimeout;

	@Value("${snmp.agent.address}")
	private String address;

	@Value("${snmp.agent.port}")
	private String port;

	@Value("${snmp.agent.community}")
	private String community;

	@Value("${snmp.requests.config.path}")
	private String configPath;

	private long totalRequestsSent;

	@Override
	public void execute() {
		final Address targetAddress = GenericAddress
				.parse(address + "/" + port);
		final CommunityTarget target = new CommunityTarget();

		target.setCommunity(new OctetString(community));
		target.setAddress(targetAddress);
		target.setTimeout(requestTimeout);

		try {
			snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();

			final List<SnmpRequestScenario> scenarios = parseScenariosFromFile(configPath);

			totalRequestsSent = 0;
			final long executionStartTime = System.currentTimeMillis();
			for (final SnmpRequestScenario scenario : scenarios) {
				executeScenario(scenario, target);
			}
			final long elapsedTime = System.currentTimeMillis()
					- executionStartTime;
			LOGGER.info("Total execution time " + (float) elapsedTime / 1000
					+ " seconds. Total number of requests " + totalRequestsSent);
		} catch (final IOException e) {
			LOGGER.error(e);
		}

	}

	private void executeScenario(final SnmpRequestScenario scenario,
			final CommunityTarget target) throws IOException {

		int requestType;
		OID startOID, endOID;

		if ("GET".equals(scenario.requestType)) {
			requestType = PDU.GET;
		} else if ("GETNEXT".equals(scenario.requestType)) {
			requestType = PDU.GETNEXT;
		} else {
			LOGGER.error("Unsupported request type: " + scenario.requestType);
			return;
		}

		try {
			startOID = new OID(scenario.startOID);
			endOID = new OID(scenario.endOID);
		} catch (final RuntimeException e) {
			LOGGER.error("Error parsing OID from scenario config file", e);
			return;
		}

		for (int iteration = 0; iteration < scenario.timesToRepeat; iteration++) {
			OID currentOID = new OID(startOID);

			while (currentOID.compareTo(endOID) <= 0) {
				final PDU pdu = new PDU();
				pdu.add(new VariableBinding(currentOID));
				pdu.setType(requestType);

				final ResponseEvent response = snmp.send(pdu, target);
				totalRequestsSent++;
				if (response.getResponse() != null) {

					LOGGER.info("Response received: "
							+ response.getResponse().toString());

					if (requestType == PDU.GETNEXT) {
						currentOID = response.getResponse().get(0).getOid();
					} else {// PDU.GET
						// increment
						currentOID.append(currentOID.removeLast() + 1);
					}
				} else {
					LOGGER.error("No response received for request: "
							+ pdu.toString());
					return;
				}

			}
		}
	}

	@Override
	public String getDescription() {
		return "Sends snmp requests to provided address"
				+ "\nSupported VM arguments:"
				+ "\n\tsnmp.agent.address - snmp agent address"
				+ "\n\tsnmp.agent.port - snmp agent port"
				+ "\n\tsnmp.agent.address - snmp agent address"
				+ "\n\tsnmp.request.timeout.millisec -  snmp request timeout in milliseconds"
				+ "\n\tsnmp.requests.config.path -  path to request scenarios config file"
				+ "\n\t Scenarios config should be JSON formatted array of scenario objects. Each scenario has following parameters: "
				+ "\n\t\t timesToRepeat - (number) times that scenario will run "
				+ "\n\t\t startOID - (string) first OID of some range"
				+ "\n\t\t endOID - (string) last OID of some range "
				+ "\n\t\t requestType - SNMP request type. Should be either 'GET' or 'GETNEXT'"
				+ "\n\t Example of scenario config: [{ \"timesToRepeat\": 1, \"startOID\": "
				+ "\"1.3.6.1.2.1.47.1.1.1.1.3.1\", \"endOID\": \"1.3.6.1.2.1.47.1.1.1.1.3.999\", \"requestType\": \"GETNEXT\" }]";
	}

	private List<SnmpRequestScenario> parseScenariosFromFile(
			final String configPath) {
		List<SnmpRequestScenario> requests = new ArrayList<SnmpRequestScenario>();

		try {
			final File inputFile = new File(configPath);
			if (inputFile.exists()) {
				requests = ToolsUtil.parseListFromJsonFile(new FileInputStream(
						inputFile), SnmpRequestScenario.class);
			}
		} catch (final Exception e) {
			LOGGER.error("Error reading scenario config file", e);
		}

		return requests;
	}
}
