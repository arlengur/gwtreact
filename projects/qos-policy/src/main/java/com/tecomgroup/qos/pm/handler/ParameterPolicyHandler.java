/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.handler;

import java.util.Date;
import java.util.Map;

import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * Класс для обработки резульатов параметров
 * 
 * @author abondin
 * 
 */
public interface ParameterPolicyHandler {
	/**
	 * 
	 * @return
	 */
	ParameterIdentifier getParameterIdentifier();

	/**
	 * 
	 * @return
	 */
	MPolicy getPolicy();

	/**
	 * @param timestamp
	 * @param value
	 * @return
	 */
	Map<String, Object> handleResult(Date timestamp, Double value);
}
