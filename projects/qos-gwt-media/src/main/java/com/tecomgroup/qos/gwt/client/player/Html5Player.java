/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import java.util.Date;

import com.google.gwt.media.client.Video;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.listener.AfterPlayerSetUpListener;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class Html5Player extends AbstractQoSPlayer {

	private Video player;

	public Html5Player(final String playerId, final QoSMessages messages) {
		super(playerId, messages);
	}

	public Html5Player(final String playerId, final String baseUrl,
			final String urlPostfix, final QoSMessages messages) {
		super(playerId, baseUrl, urlPostfix, messages);
	}

	public Html5Player(final String playerId, final String baseUrl,
			final String downloadUrl, final String recordedStreamIdentifier,
			final Date startDateTime, final Date endDateTime,
			final QoSMessages messages) {
		super(playerId, baseUrl, downloadUrl, recordedStreamIdentifier,
				startDateTime, endDateTime, messages);
	}

	@Override
	protected void afterInitialization() {
		player.setSrc(getUrl());
		notifyListeners(AfterPlayerSetUpListener.class);
	}

	@Override
	public void close() {
		checkInitializationState();
		player.pause();
	}

	@Override
	public long getCurrentTime() {
		checkInitializationState();
		final long currentTimeInMilliseconds = (long) (player.getCurrentTime() * TimeConstants.MILLISECONDS_PER_SECOND);
		return currentTimeInMilliseconds;
	}

	private String getUrl() {
		String url;
		if (baseUrl.endsWith(SimpleUtils.SLASH)) {
			url = baseUrl + urlPostfix;
		} else {
			url = baseUrl + SimpleUtils.SLASH + urlPostfix;
		}
		return url;
	}

	@Override
	public double getVolume() {
		checkInitializationState();
		return player.getVolume();
	}

	@Override
	protected void initPlayer() {
		player = Video.createIfSupported();
		if (player != null) {
			player.setControls(true);
			player.setPixelSize(playerWidget.getOffsetWidth(),
					playerWidget.getOffsetHeight());
			playerWidget.setWidget(player);
		} else {
			throw new UnsupportedOperationException(
					"HTML5 is unsupported by your browser");
		}
	}

	@Override
	public boolean isPaused() {
		checkInitializationState();
		return player.isPaused();
	}

	@Override
	public void mute() {
		checkInitializationState();
		player.setMuted(true);
	}

	@Override
	public void pause() {
		checkInitializationState();
		player.pause();
	}

	@Override
	public void play() {
		checkInitializationState();
		player.play();
	}

	@Override
	public void resume() {
		checkInitializationState();
		player.play();
	}

	@Override
	public void setCurrentTime(final long currentTimeInMilliseconds) {
		checkInitializationState();
		final long currentTimeInSeconds = currentTimeInMilliseconds
				/ TimeConstants.MILLISECONDS_PER_SECOND;
		player.setCurrentTime(currentTimeInSeconds);
	}

	@Override
	public void setVolume(final double volume) {
		checkInitializationState();
		player.setVolume(volume);
	}

	@Override
	public void togglePause() {
		throw new UnsupportedOperationException(
				"TogglePause is unsupported. Use play/pause instead.");
	}

	@Override
	public void unmute() {
		checkInitializationState();
		player.setMuted(false);
	}
}
