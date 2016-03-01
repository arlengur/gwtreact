/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.action;

import java.util.Map;

import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;

/**
 * 
 * Отвечает за выполнение {@link MPolicyAction}
 * 
 * @author abondin
 * 
 */
public interface ActionHandler {
	public static final String OUTPUT_PARAMETER_ALERT_SEVERITY = "ALERT_SEVERITY";

	public static final String OUTPUT_PARAMETER_ALERT_SETTINGS = "ALERT_SETTINGS";
	/**
	 * 
	 * @param policy
	 * @param outputParamaters
	 */
	void doAction(MPolicy policy, Map<String, Object> outputParameters);
}
