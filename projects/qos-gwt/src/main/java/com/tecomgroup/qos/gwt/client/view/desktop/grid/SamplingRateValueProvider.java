/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.domain.MAgentTask;

/**
 * @author kunilov.p
 * 
 */
public class SamplingRateValueProvider
		extends
			ValueProviderWithPath<MAgentTask, Long> {

	public SamplingRateValueProvider(final String path) {
		super(path);
	}

	@Override
	public Long getValue(final MAgentTask task) {
		Long samplingRate = null;
		if (task.getResultConfiguration() != null) {
			samplingRate = task.getResultConfiguration().getSamplingRate();
		}
		return samplingRate;
	}
}
