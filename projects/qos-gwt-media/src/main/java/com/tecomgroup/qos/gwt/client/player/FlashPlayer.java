/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import java.io.Serializable;
import java.util.Date;

import com.google.gwt.user.client.Timer;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.listener.PlayerListenerCallbacks;
import com.tecomgroup.qos.gwt.client.player.listener.StartPlayingListener;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
public class FlashPlayer extends AbstractQoSPlayer implements Serializable {

	static {
		PlayerListenerCallbacks.createPlayerCallbacks();
	}

	public FlashPlayer(final String playerId, final QoSMessages messages) {
		super(playerId, messages);

	}

	public FlashPlayer(final String playerId, final String baseUrl,
			final String urlPostfix, final QoSMessages messages) {
		super(playerId, baseUrl, urlPostfix, messages);
	}

	//
	//
	// /**
	// * Gets total video length in milliseconds.
	// */
	// public native long getVideoLength() /*-{
	// $wnd.qligentPlayerModule.getVideoLength(playerId);
	// }-*/;
	//

	public FlashPlayer(final String playerId, final String baseUrl,
			final String downloadUrl, final String recordedStreamIdentifier,
			final Date startDateTime, final Date endDateTime,
			final QoSMessages messages) {
		super(playerId, baseUrl, downloadUrl, recordedStreamIdentifier,
				startDateTime, endDateTime, messages);
	}

	@Override
	protected void afterInitialization() {
		// do nothing
	}

	@Override
	public void close() {
		if (isInitialized()) {
			if (isLoaded()) {
				close(playerId);
			}
			removePlayer(playerId, getWrapperId(), playerWidgetHtmlElement);
		}
		super.close();
	}

	private native void close(final String playerId) /*-{
		$wnd.qligentPlayerModule.close(playerId);
	}-*/;

	private native void createPlayer(final String playerId,
			final String playerType) /*-{
		$wnd.qligentPlayerModule.createPlayer(playerId, playerType);
	}-*/;

	@Override
	public long getCurrentTime() {
		checkInitializationState();
		return (long) getCurrentTime(playerId);
	}

	private native double getCurrentTime(final String playerId) /*-{
		return $wnd.qligentPlayerModule.getCurrentTime(playerId);
	}-*/;

	@Override
	public double getVolume() {
		checkInitializationState();
		return getVolume(playerId);
	}

	private native double getVolume(final String playerId) /*-{
		return $wnd.qligentPlayerModule.getVolume(playerId);
	}-*/;

	@Override
	protected void initPlayer() {
		createPlayer(playerId, streamType.toString());
	}

	@Override
	public boolean isPaused() {
		checkInitializationState();
		return isPaused(playerId);
	}

	private native boolean isPaused(final String playerId) /*-{
		return $wnd.qligentPlayerModule.isPaused(playerId);
	}-*/;

	@Override
	public void loadState() {
		checkInitializationState();
		if (hasState()) {
			setCurrentTime(state.getCurrentTime());
			if (state.isMuted()) {
				mute();
			} else {
				setVolume(state.getVolume());
			}
			if (state.isPaused()) {
				addPlayerListener(StartPlayingListener.class,
						new StartPlayingListener() {

							@Override
							public void handle(final QoSPlayer player) {
								removePlayerListener(
										StartPlayingListener.class, this);
								/*
								 * The player must go to new frame and pause on
								 * it so timer is need
								 */
								new Timer() {

									@Override
									public void run() {
										player.pause();
									}
								}.schedule(TimeConstants.MILLISECONDS_PER_SECOND);
							}
						});
			}
			setPlayerPanelText(messages.liveVideo());
			play();
		}
	}

	@Override
	public void mute() {
		checkInitializationState();
		mute(playerId);
	}

	private native void mute(final String playerId) /*-{
		$wnd.qligentPlayerModule.mute(playerId);
	}-*/;

	@Override
	public void pause() {
		checkInitializationState();
		pause(playerId);
	}

	private native void pause(final String playerId) /*-{
		$wnd.qligentPlayerModule.pause(playerId);
	}-*/;

	@Override
	public void play() {
		checkInitializationState();
		if (StreamType.LIVE.equals(streamType)) {
			playLiveStream(playerId, baseUrl, urlPostfix);
		} else if (StreamType.RECORDED.equals(streamType)) {
			playRecordedStream(playerId, baseUrl, recordedStreamIdentifier,
					startDateTime.getTime(), endDateTime.getTime());
		}
	}

	private native void playLiveStream(final String playerId,
			final String baseUrl, final String urlPostfix) /*-{
		$wnd.qligentPlayerModule.playLiveStream(playerId, baseUrl, urlPostfix);
	}-*/;

	private native void playRecordedStream(final String playerId,
			final String baseUrl, final String channelIdentifier,
			final double startTimestamp, final double endTimestamp) /*-{
		$wnd.qligentPlayerModule.playRecordedStream(playerId, baseUrl,
				channelIdentifier, startTimestamp, endTimestamp);
	}-*/;

	private native void removePlayer(final String playerId, String wrapperId,
			String originalHtmlElement) /*-{
		$wnd.qligentPlayerModule.removePlayer(playerId, wrapperId,
				originalHtmlElement);
	}-*/;

	@Override
	public void resume() {
		checkInitializationState();
		resume(playerId);
	}

	private native void resume(final String playerId) /*-{
		$wnd.qligentPlayerModule.resume(playerId);
	}-*/;

	@Override
	public void setCurrentTime(final long currentTimeInMilliseconds) {
		checkInitializationState();
		if (StreamType.RECORDED.equals(streamType)) {
			setCurrentTime(playerId, currentTimeInMilliseconds);
		}
	}

	/**
	 * Set player cursor to specified position in milliseconds.
	 * 
	 * @param positionInMilliseconds
	 */
	private native void setCurrentTime(final String playerId,
			final double currentTimeInMilliseconds) /*-{
		$wnd.qligentPlayerModule.setCurrentTime(playerId,
				currentTimeInMilliseconds);
	}-*/;
	//
	// /**
	// * Set buffer time to hide transition between files in channel mode in
	// * seconds.
	// *
	// * Current value is 1 second.
	// *
	// * @param bufferTime
	// */
	// public native void setBufferTime(final float bufferTime) /*-{
	// $wnd.qligentPlayerModule.setBufferTime(playerId, bufferTime);
	// }-*/;

	private void setPlayerPanelText(final String text) {
		if (StreamType.LIVE.equals(streamType)) {
			setPlayerPanelText(playerId, text);
		}
	}

	private native void setPlayerPanelText(final String playerId,
			final String text) /*-{
		$wnd.qligentPlayerModule.setPlayerPanelText(playerId, text);
	}-*/;

	/**
	 * Sets volume from 0 to 1.
	 * 
	 * @param volume
	 *            - float value from 0 to 1.
	 */
	@Override
	public void setVolume(final double volume) {
		checkInitializationState();
		setVolume(playerId, volume);
	}

	private native void setVolume(final String playerId, final double volume) /*-{
		$wnd.qligentPlayerModule.setVolume(playerId, volume);
	}-*/;

	@Override
	public void togglePause() {
		checkInitializationState();
		togglePause(playerId);
	}

	private native void togglePause(final String playerId) /*-{
		$wnd.qligentPlayerModule.togglePause(playerId);
	}-*/;

	@Override
	public void unmute() {
		checkInitializationState();
		unmute(playerId);
	}

	private native void unmute(final String playerId) /*-{
		$wnd.qligentPlayerModule.unmute(playerId);
	}-*/;
}
