/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.player.PlayerRegistry;
import com.tecomgroup.qos.gwt.client.player.PlayerState;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.PlayerUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.AbstractWidgetTileContentElement;

/**
 * @author ivlev.e
 *
 */
public class LiveVideoPlayerDelegate
		extends
			AbstractWidgetTileContentElement<LiveStreamWidget> {

	private QoSPlayer player;

	private MLiveStreamWrapper liveStreamWrapper;

	private Widget error;

	/**
	 * Used as id prefix of Dashboard page. It could be valuable if several
	 * instances of the same widget share static registry (for example
	 * {@link QoSPlayer})
	 */
	private final static String DASHBOARD_ID_PREFIX = "Dashboard_";

	public LiveVideoPlayerDelegate(final LiveStreamWidget model,
			final PlayerFactory playerFactory,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(model);
		init(playerFactory, appearanceFactory, messages);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (player != null) {
			PlayerRegistry.getInstance().removePlayer(player);
		}
	}

	@Override
	public void dispose() {
		if (player != null) {
			player.saveState();
			player.close();
		}
	}

	@Override
	public Widget getContentElement() {
		if (player != null) {
			return player.asWidget();
		} else {
			return error;
		}
	}

	private void init(final PlayerFactory playerFactory,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		MLiveStream stream = model.getStream();

		if (stream.isDisabled()) {
			Label label = new Label(messages.disabledTask(stream.getTaskDisplayName()));
			label.addStyleName(appearanceFactory.resources().css()
					.labelErrorWidget());
			error = label.asWidget();
			return;
		}

		liveStreamWrapper = new MLiveStreamWrapper();
		liveStreamWrapper.setStream(stream);
		liveStreamWrapper.setStreamKey(model.getStreamKey());
		liveStreamWrapper.setTaskKey(model.getTaskKey());

		final String playerId = PlayerRegistry
				.createPlayerId(DASHBOARD_ID_PREFIX
						+ liveStreamWrapper.getUniqueKey());
		player = PlayerRegistry.getInstance().getPlayer(playerId);
		if (player == null) {
			player = playerFactory.createPlayerDependentOnPlatform(playerId,
					messages, new PlayerState(true, 0, 1), new PlayerState(
							false, 0, 0));
			PlayerRegistry.getInstance().addPlayer(player);
		}
		player.asWidget().addStyleName(
				appearanceFactory.resources().css().size100pct());
	}

	@Override
	public void initialize() {
		if (player != null) {
			PlayerUtils.setUpLiveVideoPlayer(player, liveStreamWrapper);
		}
	}
}
