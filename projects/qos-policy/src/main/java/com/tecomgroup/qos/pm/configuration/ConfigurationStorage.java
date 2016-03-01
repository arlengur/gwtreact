/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.configuration;

import com.tecomgroup.qos.communication.message.PolicyManagerConfiguration;

/**
 * 
 * @author abondin
 * 
 */
public interface ConfigurationStorage {
	/**
	 * 
	 * @return
	 */
	PolicyManagerConfiguration loadLocal();

	/**
	 * 
	 * @param configuration
	 */
	void updateConfiguration(PolicyManagerConfiguration configuration);
}
