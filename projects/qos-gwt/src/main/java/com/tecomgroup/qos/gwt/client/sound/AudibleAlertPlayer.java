/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.sound;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.user.client.Window.Navigator;
import com.google.inject.Inject;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.gwt.client.event.alert.FlickeringEvent;
import com.tecomgroup.qos.gwt.client.event.alert.StopAudibleAlertEvent;
import com.tecomgroup.qos.gwt.client.event.alert.StopAudibleAlertEvent.StopAudibleAlertEventHandler;
import com.tecomgroup.qos.gwt.client.sound.SoundConstants.SeveritySoundFile;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.UserAgentUtils;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Simple player for playing of {@link AudibleAlert}
 * 
 * @author ivlev.e
 */
public class AudibleAlertPlayer implements StopAudibleAlertEventHandler {

	public static Logger LOGGER = Logger.getLogger(AudibleAlertPlayer.class
			.getName());

	private PerceivedSeverity lastSeverity;

	private final Map<PerceivedSeverity, AudibleAlert> audibleAlerts = new HashMap<PerceivedSeverity, AudibleAlert>();

	private final PerceivedSeverity MIN_SEVERITY;

	private final String SOUND_FOLDER;

	private final Integer REPEAT_INTERVAL;

	private final boolean MOBILE_ENABLED;

	@Inject
	public AudibleAlertPlayer() {
		AppUtils.getEventBus().addHandler(StopAudibleAlertEvent.TYPE, this);

		final Map<String, Object> clientProperties = AppUtils
				.getClientProperties();
		MIN_SEVERITY = PerceivedSeverity.valueOf(((String) clientProperties
				.get(SoundConstants.MIN_SEVERITY)).toUpperCase());
		SOUND_FOLDER = (String) clientProperties
				.get(SoundConstants.SOUND_FOLDER);
		REPEAT_INTERVAL = Integer.valueOf((String) clientProperties
				.get(SoundConstants.REPEAT_INTERVAL));
		MOBILE_ENABLED = new Boolean(
				(String) clientProperties.get(SoundConstants.MOBILE_ENABLED));

		initializeAudibleAlerts();
	}

	private void createAudibleAlert(final PerceivedSeverity severity) {
		final AudibleAlert audibleAlert = AudibleAlert.createIfSupported();
		audibleAlert.setSrc(createSoundUrl(getSoundFileName(severity)));
		if (UserAgentUtils.isDesktop()) {
			audibleAlert.setPreload(MediaElement.PRELOAD_AUTO);
		}
		audibleAlert.setRepeatInterval(REPEAT_INTERVAL);
		audibleAlerts.put(severity, audibleAlert);
	}

	private String createSoundUrl(final String fileName) {
		return GWT.getModuleBaseForStaticFiles() + SOUND_FOLDER
				+ SimpleUtils.SLASH + fileName;
	}

	/**
	 * @return the audibleAlerts
	 */
	public Map<PerceivedSeverity, AudibleAlert> getAudibleAlerts() {
		return audibleAlerts;
	}

	public PerceivedSeverity getLastSeverity() {
		return lastSeverity;
	}

	private String getSoundFileName(final PerceivedSeverity severity) {
		return (String) AppUtils.getClientProperties().get(
				SeveritySoundFile.valueOf(severity.toString())
						.getPropertyName());
	}

	public void handleAlertEvent(final PerceivedSeverity severity) {
		if (severity.ge(MIN_SEVERITY)
				&& (lastSeverity == null || severity
						.greater(lastSeverity))) {
			if (isFlickeringComponentSupported()) {
				AppUtils.getEventBus().fireEvent(new FlickeringEvent(severity));
			}
			if (isSoundComponentSupported()) {
				playAudibleAlert(severity);
			}
			lastSeverity = severity;
		}
	}

	private void initializeAudibleAlerts() {
		final PerceivedSeverity[] severities = PerceivedSeverity.values();
		for (int i = 0; i < severities.length; i++) {
			createAudibleAlert(severities[i]);
		}
	}

	private boolean isFlickeringComponentSupported() {
		return !AudibleAlertFeatureMode.OFF.equals(AppUtils
				.getAudibleAlertMode());
	}

	private boolean isSoundComponentSupported() {
		return (UserAgentUtils.isDesktop() && AudibleAlertFeatureMode.ON
				.equals(AppUtils.getAudibleAlertMode()))
				|| (UserAgentUtils.isMobile() && !UserAgentUtils.isIOSDevice() && MOBILE_ENABLED);
	}

	@Override
	public void onStopAudibleAlert(final StopAudibleAlertEvent event) {
		stop();
		lastSeverity = null;
	}

	private void playAudibleAlert(final PerceivedSeverity severity) {
		stop();

		final AudibleAlert audibleAlert = audibleAlerts.get(severity);
		try {
			audibleAlert.load();
			audibleAlert.play();
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to load/start sound", e);
		}
	}

	private void stop() {
		if (lastSeverity != null) {
			final AudibleAlert currentAlert = audibleAlerts
					.get(lastSeverity);
			try {
				currentAlert.stop();
			} catch (final Exception e) {
				LOGGER.log(Level.WARNING, "Unable to stop sound. User-agent: "
						+ Navigator.getUserAgent());
			}
		}
	}

}
