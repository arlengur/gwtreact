/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

/**
 * @author kunilov.p
 * 
 */
public interface PlayerContainer {

	/**
	 * Adds new player to contayner
	 * 
	 * @param player
	 */
	void addPlayer(QoSPlayer player);

	/**
	 * 
	 * @param playerId
	 * @return
	 */
	QoSPlayer getPlayer(String playerId);

	/**
	 * Close player and remove from container This method invokes
	 * {@link QoSPlayer#close()}
	 * 
	 * @param player
	 */
	void removePlayer(QoSPlayer player);

	/**
	 * Close player and remove from container This method invokes
	 * {@link QoSPlayer#close()}
	 * 
	 * @param playerId
	 */
	void removePlayer(String playerId);
}
