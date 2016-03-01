/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.player.PlayerRegistry;
import com.tecomgroup.qos.gwt.client.player.PlayerState;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * Abstract player widget which provides base player functionality.
 * 
 * 1) Use {@link PlayerWidget#getPlayer(String)} in subclasses to get new or
 * cached player. <br />
 * 
 * 2) Use {@link PlayerWidget#destroyPlayer()} to destroy player and clear its
 * container. <br />
 * 
 * 3) Use {@link PlayerWidget#getPlayerContainer()} to get player container
 * which is initialized in constructor and exists during the whole player widget
 * lifecycle even after calling {@link PlayerWidget#destroyPlayer()}.
 * 
 * @author kunilov.p
 * 
 */
public abstract class PlayerWidget {

	public final static int VIDEO_PANEL_WIDTH = 354;

	public final static int VIDEO_PANEL_HEIGHT = 290;

	private final QoSMessages messages;

	protected final BorderLayoutContainer playerContainer;

	protected VideoPanel videoPanel;

	private final PlayerFactory playerFactory;

	private final String playerId;

	private QoSPlayer player;

	public PlayerWidget(final String playerId,
			final PlayerFactory playerFactory,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		this.playerId = playerId;
		this.playerFactory = playerFactory;
		this.messages = messages;
		playerContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
	}

	private void clearPlayerContainer() {
		playerContainer.clear();
	}

	private QoSPlayer createNewPlayer(final String playerId) {
		final QoSPlayer player = playerFactory.createPlayerDependentOnPlatform(
				playerId, messages);
		player.setDefaultState(new PlayerState(true, 0, 1));

		return player;
	}

	/**
	 * Destroys player and clear its container.
	 */
	public void destroyPlayer() {
		if (player != null) {
			PlayerRegistry.getInstance().removePlayer(player.getPlayerId());
			if (videoPanel != null) {
				videoPanel.removeFromParent();
			}
			clearPlayerContainer();
			player = null;
		}
	}

	/**
	 * Gets player with provided title.
	 * 
	 * @param playerTitle
	 */
	protected QoSPlayer getPlayer(final String playerTitle) {
		player = PlayerRegistry.getInstance().getPlayer(playerId);
		if (player == null) {
			player = createNewPlayer(playerId);
			PlayerRegistry.getInstance().addPlayer(player);

			videoPanel = new VideoPanel(player, playerTitle, false, true, messages.downloadVideo(),
					false, false, VIDEO_PANEL_WIDTH, VIDEO_PANEL_HEIGHT);
			clearPlayerContainer();
			playerContainer.setWidget(videoPanel);
		}

		return player;
	}

	/**
	 * Returns container with player.
	 * 
	 * @return
	 */
	public Widget getPlayerContainer() {
		return playerContainer;
	}

	public VideoPanel getVideoPanel() {
		return videoPanel;
	}
}
