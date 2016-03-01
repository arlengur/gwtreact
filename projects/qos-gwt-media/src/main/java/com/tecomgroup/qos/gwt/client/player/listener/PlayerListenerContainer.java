/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tecomgroup.qos.gwt.client.player.QoSPlayer;

/**
 * @author kunilov.p
 * 
 */
public abstract class PlayerListenerContainer {
	private final Map<Class<? extends PlayerListener>, List<PlayerListener>> listeners;

	private static Logger LOGGER = Logger
			.getLogger(PlayerListenerContainer.class.getName());

	public PlayerListenerContainer() {
		listeners = new HashMap<Class<? extends PlayerListener>, List<PlayerListener>>();
	}

	protected <T extends PlayerListener> void addPlayerListener(
			final Class<T> listenerType, final T listener) {
		List<PlayerListener> requestedTypeListeners = listeners
				.get(listenerType);
		if (requestedTypeListeners == null) {
			requestedTypeListeners = new ArrayList<PlayerListener>();
			listeners.put(listenerType, requestedTypeListeners);
		}
		requestedTypeListeners.add(listener);
	}

	public void clearListeners() {
		for (final Map.Entry<Class<? extends PlayerListener>, List<PlayerListener>> listenersEntry : listeners
				.entrySet()) {
			listenersEntry.getValue().clear();
		}
		listeners.clear();
	}

	public abstract void notifyListeners(
			Class<? extends PlayerListener> listenerType);

	protected void notifyListeners(final QoSPlayer player,
			final Class<? extends PlayerListener> listenerType) {
		final List<PlayerListener> typedListeners = listeners.get(listenerType);
		if (typedListeners != null) {
			for (final PlayerListener listener : new ArrayList<PlayerListener>(
					typedListeners)) {
				listener.handle(player);
			}
		}
	}

	protected <T extends PlayerListener> void removePlayerListener(
			final Class<T> listenerType, final T listener) {
		final List<PlayerListener> requestedTypeListeners = listeners
				.get(listenerType);

		if (requestedTypeListeners != null) {
			requestedTypeListeners.remove(listener);
			if (requestedTypeListeners.isEmpty()) {
				listeners.remove(listenerType);
			}
		} else {
			LOGGER.log(Level.WARNING, "There are no listeners of the type "
					+ listenerType + "in the pool");
		}
	}
}
