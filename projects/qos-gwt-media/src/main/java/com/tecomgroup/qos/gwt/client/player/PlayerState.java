/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

/**
 * This is an immutable class initialized only by using constructor.
 * 
 * @author kunilov.p
 * 
 */
public class PlayerState {
	private final boolean paused;
	private final long currentTime;
	private final double volume;

	public PlayerState(final boolean paused, final long currentTime,
			final double volume) {
		this.paused = paused;
		this.currentTime = currentTime;
		this.volume = volume;
	}

	/**
	 * @return the currentTime
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}

	public boolean isMuted() {
		return volume == 0.0;
	}

	/**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

}
