/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.PlaylistItem;

/**
 * @author meleshin.o
 * 
 */
public class PlaylistItemDurationValueProvider
		extends
			DurationValueProvider<PlaylistItem> {
	public PlaylistItemDurationValueProvider(final QoSMessages messages) {
		super(messages, "duration");

	}

	@Override
	public Long getDuration(final PlaylistItem item) {
		return item.getEndTime() - item.getStartTime();
	}
}
