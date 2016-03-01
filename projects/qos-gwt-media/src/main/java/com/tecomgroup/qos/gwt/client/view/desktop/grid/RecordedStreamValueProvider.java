/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedStreamCell;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author novohatskiy.r
 * 
 */
public class RecordedStreamValueProvider extends StreamValueProvider {

	/**
	 * @param messages
	 */
	public RecordedStreamValueProvider(final QoSMessages messages) {
		super(messages);
	}

	@Override
	public Map<String, String> getValue(final StreamClientWrapper<?> object) {
		final MRecordedStreamWrapper wrapper = (MRecordedStreamWrapper) object
				.getWrapper();
		final Map<String, String> parentProperties = super.getValue(object);
		final Map<String, String> properties = new LinkedHashMap<String, String>();
		String videoTimezone = wrapper.getVideoTimeZone();
		properties.put(messages.recordedVideoFrom(),
				DateUtils.DATE_TIME_FORMATTER.format(wrapper.getStartDateTime()));
		properties.put(messages.recordedVideoTo(),
				DateUtils.DATE_TIME_FORMATTER.format(wrapper.getEndDateTime()));
		if (parentProperties.get(messages.timezone()).equals(videoTimezone)) {
			videoTimezone = messages.timeAgent();
		}
		properties.put(RecordedStreamCell.VIDEOS_TIMEZONE, videoTimezone);
		properties.putAll(parentProperties);
		return properties;
	}

}
