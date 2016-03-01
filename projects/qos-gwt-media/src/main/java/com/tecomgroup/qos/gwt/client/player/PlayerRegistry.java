/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author kunilov.p
 * 
 */
public final class PlayerRegistry implements PlayerContainer {

	private static volatile PlayerRegistry playerRegistry;

	private final static String QOS_PLAYER_TEMPLATE_ID = "qligentPlayer";

	private final static String QOS_PLAYER_ID_SEPARATOR = ".";

	public static String createPlayerId(final String idPostfix) {
		return QOS_PLAYER_TEMPLATE_ID + QOS_PLAYER_ID_SEPARATOR + idPostfix;
	}

	public static PlayerRegistry getInstance() {
		if (playerRegistry == null) {
			synchronized (PlayerRegistry.class) {
				if (playerRegistry == null) {
					playerRegistry = new PlayerRegistry();
				}
			}
		}
		return playerRegistry;
	}

	private final Map<String, QoSPlayer> players;

	private PlayerRegistry() {
		super();
		players = new HashMap<String, QoSPlayer>();
	}

	@Override
	public void addPlayer(final QoSPlayer player) {
		synchronized (players) {
			players.put(player.getPlayerId(), player);
		}
	}

	public void clearRegistry() {
		synchronized (players) {
			for (final String playerId : new ArrayList<String>(players.keySet())) {
				removePlayer(playerId);
			}
			players.clear();
		}
	}

	@Override
	public QoSPlayer getPlayer(final String playerId) {
		synchronized (players) {
			return players.get(playerId);
		}
	}

	public Set<String> getPlayerIds() {
		synchronized (players) {
			return Collections.<String> unmodifiableSet(players.keySet());
		}
	}

	@Override
	public void removePlayer(final QoSPlayer player) {
		removePlayer(player.getPlayerId());
	}

	@Override
	public void removePlayer(final String playerId) {
		synchronized (players) {
			final QoSPlayer player = players.remove(playerId);
			if (player != null) {
				player.clearListeners();
				player.clearState();
				player.close();
			}
		}
	}
}
