/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.tecomgroup.qos.UpdatableEntity;

/**
 * @author sviyazov.a
 * 
 */
@SuppressWarnings("serial")
@Embeddable
public class UserSettings
		implements
			Serializable,
			UpdatableEntity<UserSettings> {

	public static enum AudibleAlertFeatureMode {
		// Don't change the order of the enum values. The order is used in
		// {@link UserSettingsWidgetView}. Otherwise the order will
		// be changed in the GUI.
		OFF, MUTE, ON
	}

    public static enum NotificationLanguage {
        EN("en"),
        RU("ru"),
        RU_TRANSLIT("ru");

        private final String languageTag;

        /**
         * @param langTag a language tag, that can be used to construct {@link java.util.Locale}.
         * @see java.util.Locale#forLanguageTag
         */
        NotificationLanguage(final String langTag) {
            languageTag = langTag;
        }

        public String getLanguageTag() {
            return languageTag;
        }
    }

	@Enumerated(value = EnumType.STRING)
	private AudibleAlertFeatureMode audibleAlertMode;

    @Enumerated(value = EnumType.STRING)
    private NotificationLanguage notificationLanguage;

	public AudibleAlertFeatureMode getAudibleAlertMode() {
		return audibleAlertMode;
	}

	public void setAudibleAlertMode(
			final AudibleAlertFeatureMode audibleAlertMode) {
		this.audibleAlertMode = audibleAlertMode;
	}

    public NotificationLanguage getNotificationLanguage() {
        return notificationLanguage;
    }

    public void setNotificationLanguage(final NotificationLanguage notificationLanguage) {
        this.notificationLanguage = notificationLanguage;
    }


	@Override
	public boolean updateSimpleFields(final UserSettings settings) {
		boolean isUpdated = false;

		if (settings != null) {
			if (!MAbstractEntity.equals(getAudibleAlertMode(),
					settings.getAudibleAlertMode())) {
				setAudibleAlertMode(settings.getAudibleAlertMode());
				isUpdated = true;
			}
            if (!MAbstractEntity.equals(getNotificationLanguage(),
                    settings.getNotificationLanguage())) {
                setNotificationLanguage(settings.getNotificationLanguage());
                isUpdated = true;
            }
		}

		return isUpdated;
	}
}
