/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player.listener;



import java.util.logging.Level;
import java.util.logging.Logger;

import com.tecomgroup.qos.gwt.client.player.PlayerRegistry;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;

/**
 * @author kunilov.p
 * 
 */
public final class PlayerListenerCallbacks {

	private static Logger LOGGER = Logger.getLogger(PlayerListenerCallbacks.class.getName());

	private static native void createFullScreenOffCallback() /*-{
		$wnd.playerFullScreenOffCallback = $entry(
			@com.tecomgroup.qos.gwt.client.player.listener.PlayerListenerCallbacks::fullScreenOff(Ljava/lang/String;));
	}-*/;

	private static native void createFullScreenOnCallback() /*-{
		$wnd.playerFullScreenOnCallback = $entry(
			@com.tecomgroup.qos.gwt.client.player.listener.PlayerListenerCallbacks::fullScreenOn(Ljava/lang/String;));
	}-*/;

	public static void createPlayerCallbacks() {
		createSetUpCallback();
		createStartedCallback();
		createFullScreenOnCallback();
		createFullScreenOffCallback();
	}

	private static native void createSetUpCallback() /*-{
		$wnd.playerSetUpCallback = $entry(
			@com.tecomgroup.qos.gwt.client.player.listener.PlayerListenerCallbacks::setUpCallback(Ljava/lang/String;));
	}-*/;

	private static native void createStartedCallback() /*-{
		$wnd.playerStartedCallback = $entry(
			@com.tecomgroup.qos.gwt.client.player.listener.PlayerListenerCallbacks::startedCallback(Ljava/lang/String;));
	}-*/;

	private static void fullScreenOff(final String playerId) {
		final QoSPlayer player = PlayerRegistry.getInstance().getPlayer(
				playerId);
		if (player != null) {
			player.notifyListeners(FullScreenOffPlayerListener.class);
		} else {
			LOGGER.log(Level.SEVERE, "Player " + playerId + " not found in registry");
		}
	}

	private static void fullScreenOn(final String playerId) {
		final QoSPlayer player = PlayerRegistry.getInstance().getPlayer(
				playerId);
		if (player != null) {
			player.notifyListeners(FullScreenOnPlayerListener.class);
		} else {
			LOGGER.log(Level.SEVERE, "Player " + playerId + " not found in registry");
		}
	}

	private static void setUpCallback(final String playerId) {
		final QoSPlayer player = PlayerRegistry.getInstance().getPlayer(
				playerId);
		if (player != null) {
			player.notifyListeners(AfterPlayerSetUpListener.class);
		} else {
			LOGGER.log(Level.SEVERE, "Player " + playerId + " not found in registry");
		}
	}

	private static void startedCallback(final String playerId) {
		final QoSPlayer player = PlayerRegistry.getInstance().getPlayer(
				playerId);
		if (player != null) {
			player.notifyListeners(StartPlayingListener.class);
		} else {
			LOGGER.log(Level.SEVERE, "Player " + playerId + " not found in registry");
		}
	}
}
