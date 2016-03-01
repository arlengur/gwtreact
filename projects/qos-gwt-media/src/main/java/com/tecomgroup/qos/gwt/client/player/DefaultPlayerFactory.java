/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.UserAgentUtils;

/**
 * @author kunilov.p
 * 
 */
public class DefaultPlayerFactory implements PlayerFactory {

	@Override
	public QoSPlayer createFlashPlayer(final String playerId,
			final QoSMessages messages) {
		return new FlashPlayer(playerId, messages);
	}

	@Override
	public QoSPlayer createHTML5Player(final String playerId,
			final QoSMessages messages) {
		return new Html5Player(playerId, messages);
	}

	@Override
	public QoSPlayer createPlayerDependentOnPlatform(final String playerId,
			final QoSMessages messages) {
		return createPlayerDependentOnPlatform(playerId,
				messages, null, null);
	}

	@Override
	public QoSPlayer createPlayerDependentOnPlatform(
			final String playerId, final QoSMessages messages,
			final PlayerState defaultMobileState,
			final PlayerState defaultDesktopState) {
		QoSPlayer player = null;
		if (UserAgentUtils.isMobile()) {
			player = new Html5Player(playerId, messages);
			player.setDefaultState(defaultMobileState);
		} else {
			player = new FlashPlayer(playerId, messages);
			player.setDefaultState(defaultDesktopState);
		}
		return player;
	}
}
