/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * @author kunilov.p
 * 
 */
public interface ResultConfigurationSettings {

	String CONFIGURATION_TYPE = "GAUGE";

	Long MIN_HEARTBEAT = 60L;

	Double MAX_VALUE = Double.NaN;

	Double MIN_VALUE = Double.NaN;

	String RRD_EXTENSION = ".rrd";

	Integer AGGREGATION_MULTIPLIED_FACTOR = 2;
}
