/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Date;

import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * Provides recorded player functionality.
 * 
 * Use {@link RecordedPlayerWidget#show(MRecordedStream, Date, Date)} to show
 * player with preloaded video.
 * 
 * @author kunilov.p
 * 
 */
public class RecordedPlayerWidget extends PlayerWidget {

	public RecordedPlayerWidget(final String playerId,
			final PlayerFactory playerFactory,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(playerId, playerFactory, appearanceFactory, messages);
	}

	/**
	 * Shows player with preloaded video represented by provided stream.
	 * 
	 * @param stream
	 * @param startDateTime
	 * @param endDateTime
	 */
	public void show(final MRecordedStream stream, final Date startDateTime,
			final Date endDateTime) {
		if (stream == null) {
			destroyPlayer();
		} else {
			final String baseUrl = stream.getStreamUrl();
			final String recordedStreamIdentifier = stream.getProperty(
					MStream.RECORDED_FILE_PREFIX).getValue();

			final QoSPlayer player = getPlayer(stream.getDisplayName());

			player.setRecordedStreamParameters(baseUrl,
					stream.getDownloadUrl(), recordedStreamIdentifier,
					startDateTime, endDateTime);
			player.initialize();
			getVideoPanel().setTaskKey(stream.getSource().getKey());
			getVideoPanel().setTaskDisplayName(stream.getTaskDisplayName());
		}
	}
}
