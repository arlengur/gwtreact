/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class PlayerUtils {

	public static void setUpLiveVideoPlayer(final QoSPlayer player,
			final MLiveStreamWrapper streamWrapper) {
		if (streamWrapper.getStream() != null) {
			final String streamUrl = streamWrapper.getStream().getUrl();
			if (SimpleUtils.isNotNullAndNotEmpty(streamUrl)) {
				final int ulrDelimiter = streamUrl
						.lastIndexOf(SimpleUtils.SLASH);
				final String baseUrl = streamUrl.substring(0, ulrDelimiter);
				final String urlPostfix = streamUrl.substring(ulrDelimiter + 1,
						streamUrl.length());

				player.setLiveStreamParameters(baseUrl, urlPostfix);
				player.initialize();
			}
		}
	}
}
