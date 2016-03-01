/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import java.util.Date;

import com.google.gwt.user.client.ui.IsWidget;
import com.tecomgroup.qos.Statefull;
import com.tecomgroup.qos.gwt.client.player.listener.AfterPlayerSetUpListener;
import com.tecomgroup.qos.gwt.client.player.listener.PlayerListener;

/**
 * Interface represents wrapper for player.
 * 
 * @author kunilov.p
 * 
 */
public interface QoSPlayer extends IsWidget, Statefull {
	/**
	 * Adds listener which will be called when player is completely initialized.
	 * 
	 * @param listener
	 */
	void addAfterPlayerSetUpListener(AfterPlayerSetUpListener listener);

	/**
	 * Clears all listeners.
	 */
	void clearListeners();

	/**
	 * Closes player.
	 */
	void close();

	/**
	 * Returns the current time within the source media stream.
	 * 
	 * @return time, in milliseconds.
	 */
	long getCurrentTime();

	/**
	 * Gets unique player id.
	 * 
	 * @return
	 */
	String getPlayerId();

	/**
	 * Creates url for downloading playlist of current recorded video files
	 */
	String getPlaylistDownloadUrl();

	/**
	 * Gets volume.
	 * 
	 * @return double from 0 to 1.
	 */
	double getVolume();

	/**
	 * Initializes player.
	 * 
	 * IMPORTANT: should be called first after creation.
	 */
	void initialize();

	/**
	 * @return true if player is paused, otherwise false.
	 */
	boolean isPaused();

	/**
	 * Mutes player.
	 */
	void mute();

	/**
	 * Notifies listeners with provided type.
	 * 
	 * @param listenerType
	 */
	void notifyListeners(Class<? extends PlayerListener> listenerType);

	/**
	 * Pauses player.
	 */
	void pause();

	/**
	 * Plays player.
	 */
	void play();

	/**
	 * Resumes player.
	 */
	void resume();

	/**
	 * Sets current playing time.
	 * 
	 * @param currentTimeInMilliseconds
	 *            time in milliseconds.
	 */
	void setCurrentTime(long currentTimeInMilliseconds);

	/**
	 * Sets default state to load first time.
	 * 
	 * @param state
	 */
	void setDefaultState(PlayerState state);

	/**
	 * Sets parameters to play regular file or live stream.
	 * 
	 * @param baseUrl
	 *            - base URL to play all videos
	 * @param urlPostfix
	 *            - It can be name of video file or name of live stream.
	 */
	void setLiveStreamParameters(String baseUrl, String urlPostfix);

	/**
	 * Sets parameters to play recorded stream.
	 * 
	 * @param baseUrl
	 *            - base URL to play all videos
	 * @param downloadUrl
	 *            - base URL for download video
	 * @param recordedStreamIdentifier
	 *            - some identifier of recorded stream
	 * @param startDateTime
	 *            - start time of the requested video
	 * @param endDateTime
	 *            - end time of the requested video
	 */
	void setRecordedStreamParameters(String baseUrl,
			String downloadUrl, String recordedStreamIdentifier,
			Date startDateTime, Date endDateTime);

	/**
	 * Sets volume from 0 to 1.
	 * 
	 * @param volume
	 *            - double value from 0 to 1.
	 */
	void setVolume(double volume);

	/**
	 * Calls pause or resume depended on current state.
	 * 
	 * If current state is pause then resume will be done. If current state is
	 * play then pause will be done.
	 */
	void togglePause();

	/**
	 * Unmutes player.
	 */
	void unmute();
}
