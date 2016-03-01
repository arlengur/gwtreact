/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author ivlev.e
 * 
 */
public class StreamValueProvider extends StreamBaseValueProvider {

	public StreamValueProvider(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public Map<String, String> getValue(final StreamClientWrapper<?> object) {
		final Map<String, String> parentProperties = super.getValue(object);
		final Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put(messages.probeShort(), object.getWrapper()
				.getAgentDisplayName());
		properties.put(messages.timezone(), object.getWrapper().getTimeZone());
		properties.putAll(parentProperties);
		return properties;
	}

	@Override
	public void setValue(final StreamClientWrapper<?> object,
			final Map<String, String> value) {
	}

}
