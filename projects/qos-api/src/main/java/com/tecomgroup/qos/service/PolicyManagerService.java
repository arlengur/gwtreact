/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.communication.handler.ResultHandler;
import com.tecomgroup.qos.communication.message.PolicyManagerConfiguration;
import com.tecomgroup.qos.communication.pm.PMConfiguration;

/**
 * 
 * Сервис для обработки полисей
 * 
 * @author abondin
 * 
 */
public interface PolicyManagerService extends Service, ResultHandler {
	public static final String OUTPUT_PARAMETER_CURRENT_VALUE = "CURRENT_VALUE";
	public static final String OUTPUT_PARAMETER_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
	public static final String OUTPUT_PARAMETER_DISPLAY_NAME = "PARAMETER_DISPLAY_NAME";
	public static final String OUTPUT_PARAMETER_TASK_DISPLAY_NAME = "TASK_DISPLAY_NAME";
	public static final String OUTPUT_PARAMETER_AGENT_DISPLAY_NAME = "AGENT_DISPLAY_NAME";
	public static final String OUTPUT_PARAMETER_POLICY_DISPLAY_NAME = "POLICY_DISPLAY_NAME";
	public static final String OUTPUT_PARAMETER_CURRENT_TASK_KEY = "CURRENT_TASK_KEY";
	public static final String OUTPUT_PARAMETER_THRESHOLD = "PARAMETER_THRESHOLD";

	/**
	 * @param configuration
	 */
	void applyConfiguration(PolicyManagerConfiguration configuration);

	/**
	 * 
	 * @return
	 */
	PolicyManagerConfiguration getConfiguration();

	/**
	 * 
	 */
	void loadLocalConfiguration();

	/**
	 * 
	 * @param configuration
	 */
	void removePMConfiguration(PMConfiguration configuration);

	/**
	 * 
	 * @param configuration
	 */
	void updatePMConfiguration(PMConfiguration configuration);
}
