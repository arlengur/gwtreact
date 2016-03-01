/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Date;

import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * Dialog for recorded video.
 * 
 * Use {@link RecordedPlayerWidget#show(MRecordedStream, Date, Date)} to show
 * player dialog with preloaded video.
 * 
 * @author kunilov.p
 * 
 */
public class RecordedPlayerDialog extends PlayerDialog {

	public RecordedPlayerDialog(final PlayerFactory playerFactory,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(playerFactory, appearanceFactory, messages);
	}

	/**
	 * Shows player dialog with preloaded video represented by provided stream. <br />
	 * 
	 * @param stream
	 * @param startDateTime
	 * @param endDateTime
	 */
	public void show(final MRecordedStream stream, final Date startDateTime,
			final Date endDateTime) {
		show();
		playerWidget.show(stream, startDateTime, endDateTime);
	}
}
