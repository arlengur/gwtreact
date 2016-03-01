/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.sound;

import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;
import com.tecomgroup.qos.TimeConstants;

/**
 * @author ivlev.e
 * 
 */
public class AudibleAlert extends Audio {

	private class SoundEndedHandler implements EndedHandler {

		private Timer timer;

		public Timer getTimer() {
			return timer;
		}

		@Override
		public void onEnded(final EndedEvent event) {
			final Audio audio = (Audio) event.getSource();
			timer = new Timer() {

				@Override
				public void run() {
					audio.setCurrentTime(0);
					audio.play();
				}
			};
			timer.schedule(repeatInterval
					* TimeConstants.MILLISECONDS_PER_SECOND);
		}
	}

	/**
	 * Return a new {@link AudibleAlert} if supported, and null otherwise.
	 * 
	 * @return a new {@link AudibleAlert} if supported, and null otherwise
	 */
	public static AudibleAlert createIfSupported() {
		final Audio audio = Audio.createIfSupported();
		AudibleAlert audibleAlert = null;
		if (audio != null) {
			audibleAlert = new AudibleAlert(audio.getAudioElement());
		}
		return audibleAlert;
	}

	private int repeatInterval;

	private final SoundEndedHandler handler;

	/**
	 * @param element
	 */
	protected AudibleAlert(final AudioElement element) {
		super(element);
		handler = new SoundEndedHandler();
		addEndedHandler(handler);
	}

	public void setRepeatInterval(final int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public void stop() {
		final Timer timer = handler.getTimer();
		if (timer != null) {
			timer.cancel();
		}
		setCurrentTime(0);
		pause();
	}
}
