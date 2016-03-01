/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.sound;

/**
 * @author ivlev.e
 * 
 */
public interface SoundConstants {

	public static enum SeveritySoundFile {

		CRITICAL("client.audible.alarm.sound.severity.critical"), MAJOR(
				"client.audible.alarm.sound.severity.major"), WARNING(
				"client.audible.alarm.sound.severity.warning"), MINOR(
				"client.audible.alarm.sound.severity.minor"), NOTICE(
				"client.audible.alarm.sound.severity.notice"), INDETERMINATE(
				"client.audible.alarm.sound.severity.indeterminate");

		private final String propertyName;

		private SeveritySoundFile(final String propertyName) {
			this.propertyName = propertyName;
		}

		/**
		 * @return the propertyName
		 */
		public String getPropertyName() {
			return propertyName;
		}
	}

	static final String SOUND_FOLDER = "client.audible.alarm.sound.path";

	static final String REPEAT_INTERVAL = "client.audible.alarm.repeat.interval";

	static final String MIN_SEVERITY = "client.audible.alarm.min.severity";

	static final String MOBILE_ENABLED = "client.audible.alarm.mobile.enabled";

	static final String FEATURE_MODE = "client.audible.alarm.mode";
}
