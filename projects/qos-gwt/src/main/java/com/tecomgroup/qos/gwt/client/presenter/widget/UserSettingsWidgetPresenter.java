/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.UserSettings;
import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.domain.UserSettings.NotificationLanguage;
import com.tecomgroup.qos.gwt.client.event.AudibleAlertModeChangeEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author sviyazov.a
 * 
 */
public class UserSettingsWidgetPresenter
		extends
			PresenterWidget<UserSettingsWidgetPresenter.MyView>
		implements
			UiHandlers {

	public interface MyView
			extends
				View,
				HasUiHandlers<UserSettingsWidgetPresenter> {

	}

	private final UserServiceAsync userService;

	private final QoSMessages messages;

	@Inject
	public UserSettingsWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserServiceAsync userService) {
		super(eventBus, view);
		this.userService = userService;
		this.messages = messages;
		getView().setUiHandlers(this);
	}

	public void updateAudibleAlertMode(
			final AudibleAlertFeatureMode audibleAlertFeatureMode) {
		final MUser user = AppUtils.getCurrentUser().getUser();
		final UserSettings userSettings = user.getOrCreateSettings();

		final AudibleAlertFeatureMode currentAudibleAlertMode = userSettings
				.getAudibleAlertMode();
		if (currentAudibleAlertMode == null
				|| !currentAudibleAlertMode.equals(audibleAlertFeatureMode)) {
			userSettings.setAudibleAlertMode(audibleAlertFeatureMode);

			userService.updateCurrentUser(
					user,
					new AutoNotifyingAsyncCallback<Void>(messages
							.settingsUpdateFailure(), true) {

						@Override
						protected void success(final Void result) {
							getEventBus().fireEvent(
									new AudibleAlertModeChangeEvent(
											audibleAlertFeatureMode));
                            AppUtils.showInfoMessage(messages.alertNotificationModeUpdated());
						}
					});
		}
	}

    public void updateNotificationLanguage(
            final NotificationLanguage selectedValue) {
        final MUser user = AppUtils.getCurrentUser().getUser();
        final UserSettings userSettings = user.getOrCreateSettings();

        final NotificationLanguage currentLang = userSettings
                .getNotificationLanguage();
        if (currentLang == null
                || !currentLang.equals(selectedValue)) {
            userSettings.setNotificationLanguage(selectedValue);

            userService.updateCurrentUser(
                    user,
                    new AutoNotifyingAsyncCallback<Void>(messages
                            .settingsUpdateFailure(), true) {

                        @Override
                        protected void success(final Void result) {
                            AppUtils.showInfoMessage(messages.notificationLanguageUpdated());
                        }
                    });
        }

    }

}
