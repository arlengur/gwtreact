/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.List;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MProperty;

/**
 * @author ivlev.e
 * 
 */
public class TaskPropertiesValueProvider
		extends
			ValueProviderWithPath<MAgentTask, String> {

	private static final String PROPERTY_SEPARATOR = ", ";
	private static final String PROPERTY_NAME_VALUE_SEPARATOR = ":";

	public TaskPropertiesValueProvider(final String path) {
		super(path);
	}

	@Override
	public String getValue(final MAgentTask task) {
		String result = null;
		final List<MProperty> taskProperties = task.getProperties();
		if (taskProperties != null) {
			for (final MProperty property : taskProperties) {
				result = (result == null) ? "" : result + PROPERTY_SEPARATOR;
				result += property.getName() + PROPERTY_NAME_VALUE_SEPARATOR
						+ property.getValue();
			}
		}
		return result;
	}
}
