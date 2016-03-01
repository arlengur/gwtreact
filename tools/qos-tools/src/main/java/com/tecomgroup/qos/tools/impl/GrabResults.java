/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import com.tecomgroup.qos.communication.message.ResultMessage;
import com.tecomgroup.qos.communication.message.ResultMessage.ResultType;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.tools.QoSTool;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Forward all QoS 2.3 messages to Qligent Vision Broker
 * 
 * @author abondin
 * 
 */
@Component
public class GrabResults implements QoSTool {

	private class DVBTErrControlModule extends ModuleDescription {
	}

	private class ModuleDescription {
		public ResultType getResultType() {
			return ResultType.SINGLE_VALUE_RESULT;
		}
		protected void handleAttribute(final String name, final String value,
				final Map<String, Double> parameters,
				final Map<String, String> properties) {
			parameters.put(name, Double.valueOf(value));
		}

		public Collection<Result> parse(final Map<String, String> attributes) {
			final List<Result> results = new ArrayList<Result>();
			final Map<String, Double> parameters = new HashMap<String, Double>();
			final Map<String, String> properties = new HashMap<String, String>();
			String resultDateTime = null;
			for (final Map.Entry<String, String> entry : attributes.entrySet()) {
				if (entry.getKey().equals("time")) {
					resultDateTime = entry.getValue();
				} else {
					handleAttribute(entry.getKey(), entry.getValue(),
							parameters, properties);
				}
			}
			if (resultDateTime == null || parameters.isEmpty()) {
				LOGGER.warn("Result Time is not set or no parameters found: "
						+ attributes);
			} else {
				results.add(new Result(resultDateTime, parameters, properties));
			}
			return results;
		}
	}

	private class MpegTSStatisticsIPTVControlModule extends ModuleDescription {
		@Override
		protected void handleAttribute(final String name, final String value,
				final Map<String, Double> parameters,
				final Map<String, String> properties) {
			if (value == null || value.isEmpty()) {
				return;
			}
			if (name.equals("program")) {
				properties.put("programId", value);
			} else if (name.equals("programName")) {
				properties.put("programName", value);
			} else {
				String visionName = name;
				if (name.equals("videobitrate")) {
					visionName = "videoBitrate";
				} else if (name.equals("audiobitrate")) {
					visionName = "audioBitrate";
				} else if (name.equals("databitrate")) {
					visionName = "dataBitrate";
				} else if (name.equals("bitrate")) {
					visionName = "totalBitrate";
				}
				super.handleAttribute(visionName, value, parameters, properties);
			}
		}
	}

	private class T2ControlModule extends ModuleDescription {
		@Override
		public ResultType getResultType() {
			return ResultType.INTERVAL_RESULT;
		}
		@Override
		protected void handleAttribute(final String name, final String value,
				final Map<String, Double> parameters,
				final Map<String, String> properties) {
			if (name.equals("freeze_type")) {
				switch (value) {
					case "unknown" :
						parameters.put("silence", 0.0);
						parameters.put("black_screen", 0.0);
						parameters.put("no_signal", 0.0);
						break;
					case "silence" :
						parameters.put("silence", 1.0);
						parameters.put("black_screen", 0.0);
						parameters.put("no_signal", 0.0);
						break;
					case "black" :
						parameters.put("silence", 0.0);
						parameters.put("black_screen", 1.0);
						parameters.put("no_signal", 0.0);
						break;
					case "no_signal" :
						parameters.put("silence", 0.0);
						parameters.put("black_screen", 0.0);
						parameters.put("no_signal", 1.0);
						break;
					default :
						break;
				}
			}
		}

		@Override
		public Collection<Result> parse(final Map<String, String> attributes) {
			final List<Result> results = new ArrayList<Result>();
			if (attributes.containsKey("freeze_type")) {
				final Map<String, Double> parameters = new HashMap<String, Double>();
				final Map<String, String> properties = new HashMap<String, String>();
				final String startDateTime = attributes.get("start_time");
				final String endDateTime = attributes.get("end_time");
				handleAttribute("freeze_type", attributes.get("freeze_type"),
						parameters, properties);
				results.add(new Result(startDateTime, parameters, properties));
				results.add(new Result(endDateTime, parameters, properties));
			}
			return results;
		}
	}

	private class TR101290IPTVControlModule extends ModuleDescription {
	}

	private class TR101290p2IPTVControlModule extends ModuleDescription {
	}

	@Autowired
	private AmqpTemplate amqpTemplate;

	private Date startDateTime;

	private Date endDateTime;

	@Autowired
	private ConnectionFactory rabbitConnectionFactory;

	@Value("${grab.xml.results}")
	private File xmlResults;

	@Value("${grab.agent.name}")
	private String agentName;

	private final static Logger LOGGER = Logger.getLogger(GrabResults.class);

	private final static Pattern XML_FILE_NAME = Pattern
			.compile("RES(\\d+).XML");

	@Override
	public void execute() {
		Assert.isTrue(agentName != null && !agentName.isEmpty(),
				"No agent specified (grab.agent.name)");
		Assert.isTrue(xmlResults != null && xmlResults.isDirectory(),
				"No xml folder specified (grab.xml.results)");
		for (final File xml : xmlResults.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				boolean accept = true;
				try {
					final Matcher matcher = XML_FILE_NAME.matcher(name);
					if (matcher.find()) {
						final Date fileDateTime = Result.parseDateTime(matcher
								.group(1));
						if (startDateTime != null) {
							accept &= fileDateTime.after(startDateTime);
						}
						if (endDateTime != null) {
							accept &= fileDateTime.before(endDateTime);
						}
					} else {
						accept = false;
					}
				} catch (final Exception e) {
					accept = false;
					LOGGER.warn("Cannot parse filename", e);
				}
				return accept;
			}
		})) {
			try {
				parse(xml);
			} catch (final Exception e) {
				LOGGER.error(
						"Cannot parse or send result from " + xml.getName(), e);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Grab results from Rabbit Broker and save it to JSON files"
				+ "\nSupported VM arguments:"
				+ "\n  amqp.host - broker address"
				+ "\n  grab.xml.results - path to XML results"
				+ "\n  grab.agent.name - agent name"
				+ "\n  grab.start.time - (YYYYMMDDHHmmss in UTC) parse results with timestamp in the file name > that given value."
				+ "\n  grab.end.time - (YYYYMMDDHHmmss in UTC) parse results with timestamp in the file name < that given value.";

	}

	protected void parse(final File xmlResult) throws Exception {
		LOGGER.info("Parsing " + xmlResult.getName() + "...");
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
				.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final Document doc = dBuilder.parse(xmlResult);

		final NodeList modules = doc.getElementsByTagName("module");
		for (int moduleIndex = 0; moduleIndex < modules.getLength(); moduleIndex++) {
			final Element module = (Element) modules.item(moduleIndex);
			parseModule(module);
		}
	}

	/**
	 * @param module
	 */
	private void parseModule(final Element module) {
		final ModuleDescription moduleDescription;
		switch (module.getAttribute("name")) {
			case "DVBTErrControlModule" :
				moduleDescription = new DVBTErrControlModule();
				break;
			case "MpegTSStatisticsIPTVControlModule" :
				moduleDescription = new MpegTSStatisticsIPTVControlModule();
				break;
			case "TR101290IPTVControlModule" :
				moduleDescription = new TR101290IPTVControlModule();
				break;
			case "TR101290p2IPTVControlModule" :
				moduleDescription = new TR101290p2IPTVControlModule();
				break;
			case "T2ControlModule" :
				moduleDescription = new T2ControlModule();
				break;
			default :
				LOGGER.warn("Unsupported module " + module.getAttribute("name"));
				moduleDescription = null;
				break;
		}
		if (moduleDescription != null) {
			final String moduleName = module.getAttribute("name");
			final NodeList tasks = ((Element) (module
					.getElementsByTagName("result").item(0)))
					.getElementsByTagName("Task");
			for (int taskIndex = 0; taskIndex < tasks.getLength(); taskIndex++) {
				final Element task = (Element) tasks.item(taskIndex);
				parseTaskAndSendResult(moduleDescription, moduleName, task);
			}
		}
	}

	/**
	 * @param moduleDescription
	 * @param resultElement
	 * @return
	 */
	private Collection<Result> parseResult(
			final ModuleDescription moduleDescription,
			final Element resultElement) {
		final NamedNodeMap attributes = resultElement.getAttributes();
		final Map<String, String> attributeMap = new HashMap<String, String>();
		for (int index = 0; index < attributes.getLength(); index++) {
			final Attr attribute = (Attr) attributes.item(index);
			attributeMap.put(attribute.getName(), attribute.getValue());
		}
		return moduleDescription.parse(attributeMap);
	}

	/**
	 * @param moduleDescription
	 * @param moduleName
	 * @param task
	 */
	private void parseTaskAndSendResult(
			final ModuleDescription moduleDescription, final String moduleName,
			final Element task) {
		final ResultMessage resultMessage = new ResultMessage();
		resultMessage.setOriginName(agentName);
		resultMessage.setTaskKey(agentName + "." + moduleName + "."
				+ task.getElementsByTagName("id").item(0).getTextContent());
		resultMessage.setResultType(moduleDescription.getResultType());
		final List<Result> results = new ArrayList<Result>();
		resultMessage.setResults(results);
		final NodeList resultsNode = task.getElementsByTagName("record");
		for (int index = 0; index < resultsNode.getLength(); index++) {
			final Element resultElement = (Element) resultsNode.item(index);
			results.addAll(parseResult(moduleDescription, resultElement));
		}
		if (!resultMessage.getResults().isEmpty()) {
			amqpTemplate.convertAndSend("qos.result", "agent-" + agentName,
					resultMessage);
		}
	}

	@Value("${grab.end.time}")
	public void setEndDateTime(final String endDateTime) {
		this.endDateTime = endDateTime == null || endDateTime.trim().isEmpty()
				? null
				: Result.parseDateTime(endDateTime);
	}

	@Value("${grab.start.time}")
	public void setStartDateTime(final String startDateTime) {
		this.startDateTime = startDateTime == null
				|| startDateTime.trim().isEmpty() ? null : Result
				.parseDateTime(startDateTime);
	}
}
