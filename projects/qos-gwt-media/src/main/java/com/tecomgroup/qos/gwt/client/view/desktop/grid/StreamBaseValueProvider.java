/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamBasicCell;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author ivlev.e
 * 
 */
public class StreamBaseValueProvider
		implements
			ValueProvider<StreamClientWrapper<?>, Map<String, String>> {

	protected final QoSMessages messages;

	protected final Map<String, String> localeMap;

	public StreamBaseValueProvider(final QoSMessages messages) {
		this.messages = messages;
		localeMap = new HashMap<String, String>();
		localeMap.put("fps", messages.fps());
		localeMap.put("codec", messages.codec());
		localeMap.put("size", messages.size());
		localeMap.put("videobitrate", messages.videobitrate());
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public Map<String, String> getValue(
			final StreamClientWrapper<?> clientStreamWrapper) {
		final Map<String, String> properties = new LinkedHashMap<String, String>();
		final MStream stream = clientStreamWrapper.getWrapper().getStream();
		for (final MProperty prop : stream.getProperties()) {
			final String propertyDisplayName = localeMap.get(prop.getName());
			if (propertyDisplayName != null) {
				properties.put(propertyDisplayName, prop.getValue());
			}
		}
		properties.put(LiveStreamBasicCell.PROGRAM_NAME_PROPERTY,
				stream.getDisplayName());
		return properties;
	}

	@Override
	public void setValue(final StreamClientWrapper<?> object,
			final Map<String, String> value) {
	}

}
