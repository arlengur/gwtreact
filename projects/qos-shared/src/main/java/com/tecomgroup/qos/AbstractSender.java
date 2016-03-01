/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.util.Collection;
import java.util.Map;

/**
 * @author ivlev.e
 * 
 */
public interface AbstractSender {

	/**
	 * Resulting status of message (sms|email) sending operation.
	 * 
	 * @author sviyazov.a
	 * 
	 */
	public static enum Status {
		/**
		 * Message was successfully sent.
		 */
		SUCCESS,
		/**
		 * Some error (e.g. network connection loss, configuration problems)
		 * occurred, that can be solved, either through deferred sending retry
		 * or SNMP parameters reconfiguration.
		 */
		SOLVABLE_PROBLEM_OCCURED,
		/**
		 * Indicates, that it's impossible to send provided message.
		 */
		INCORRECT_INPUT
	}

	public static final String UNICODE_TEXT_PARAMETER_NAME = "UNICODE_TEXT";

	/**
	 * Processes templates by resolving placeholders using passed parameters and
	 * sends processed message.
	 */
	Status sendTemplatedMessage(Collection<String> contacts, String subject,
			String body, Map<String, Object> templateParameters);

}
