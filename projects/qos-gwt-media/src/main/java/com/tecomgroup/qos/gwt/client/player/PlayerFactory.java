/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author kunilov.p
 * 
 */
public interface PlayerFactory {

	/**
	 * Creates Flash player.
	 * 
	 * @param playerId
	 * @param messages
	 * @return
	 */
	QoSPlayer createFlashPlayer(String playerId, QoSMessages messages);

	/**
	 * Creates HTML5 player.
	 * 
	 * @param playerId
	 * @param messages
	 * @return
	 */
	QoSPlayer createHTML5Player(String playerId, QoSMessages messages);

	/**
	 * Creates flash or html5 player dependent on platform where the application
	 * is run.
	 * 
	 * @param playerId
	 *            the identifier of the player.
	 * @param messages
	 *            the user messages.
	 * @return an instance of {@link QoSPlayer}
	 */
	QoSPlayer createPlayerDependentOnPlatform(String playerId,
			QoSMessages messages);

/**
	 * Creates flash or html5 player dependent on platform where the application
	 * is run with default states for each platform.
	 * 
	 * @param playerId
	 * 			the identifier of the player.
	 * @param messages
	 * 			the user messages.
	 * @param defaultMobileState
	 * 			the default {@link PlayerState} for mobile platform.
	 * @param defaultDesktopState
	 * 			the default {@link PlayerState) for desktop platform.
	 * @return an instance of {@link QoSPlayer}
	 */
	QoSPlayer createPlayerDependentOnPlatform(String playerId,
			QoSMessages messages, final PlayerState defaultMobileState,
			final PlayerState defaultDesktopState);
}
