/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.Session;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;

/**
 * Wrapper for {@link Session} replacing synchronous send(final PDU pdu, final
 * Target target) method with asynchronous one.
 * 
 * @author novohatskiy.r
 * 
 */
public class AsyncSession implements Session {

	private final static Logger LOGGER = Logger.getLogger(AsyncSession.class);

	private final Session session;

	public AsyncSession(final Session session) {
		this.session = session;
	}

	@Override
	public void cancel(final PDU request, final ResponseListener listener) {
		session.cancel(request, listener);
	}

	@Override
	public void close() throws IOException {
		session.close();
	}

	@Override
	public ResponseEvent send(final PDU pdu, final Target target)
			throws IOException {
		send(pdu, target, target, new ResponseListener() {
			@Override
			public void onResponse(final ResponseEvent event) {
				final PDU requestPdu = event.getRequest();
				((Snmp) session).cancel(requestPdu, this);
				final PDU responsePdu = event.getResponse();
				final OID notificationID = (OID) requestPdu
						.getVariable(SnmpConstants.snmpTrapOID);
				if (responsePdu != null
						&& responsePdu.getErrorStatus() == PDU.noError) {
					LOGGER.info("INFORM with OID=" + notificationID
							+ " was successfully delivered to "
							+ target.getAddress());
				} else {
					LOGGER.warn("Failed to deliver INFORM with OID="
							+ notificationID + " to " + target.getAddress());
				}
			}
		});
		return null;
	}

	@Override
	public void send(final PDU pdu, final Target target,
			final Object userHandle, final ResponseListener listener)
			throws IOException {
		session.send(pdu, target, userHandle, listener);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ResponseEvent send(final PDU pdu, final Target target,
			final TransportMapping transport) throws IOException {
		return session.send(pdu, target, transport);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void send(final PDU pdu, final Target target,
			final TransportMapping transport, final Object userHandle,
			final ResponseListener listener) throws IOException {
		session.send(pdu, target, transport, userHandle, listener);
	}

}
