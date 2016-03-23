/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.exception.SecurityException;
import com.tecomgroup.qos.exception.SecurityException.Reason;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public class ChangeUserPasswordWidgetPresenter
		extends
			PresenterWidget<ChangeUserPasswordWidgetPresenter.MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				PopupView,
				HasUiHandlers<ChangeUserPasswordWidgetPresenter> {
	}

	private final QoSMessages messages;

	private final UserServiceAsync userService;

	@Inject
	public ChangeUserPasswordWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserServiceAsync userService) {
		super(eventBus, view);
		this.userService = userService;
		this.messages = messages;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().hide();
	}

	public void updatePassword(final String oldPassword,
			final String newPassword) {
		userService.updatePassword(oldPassword, newPassword,
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>() {

					@Override
					protected void failure(final Throwable caught) {
						super.failure(caught);

						if (caught instanceof SecurityException) {
							final SecurityException securityException = (SecurityException) caught;
							if (securityException.getReason() == Reason.INCORRECT_OLD_PASSWORD) {
								AppUtils.showErrorMessage(messages
										.incorrectCurrentPassword());
							}
						} else {
							AppUtils.showErrorMessage(messages
									.passwordUpdatingFail());
						}
					}

					@Override
					protected void success(final Void result) {
						AppUtils.showInfoMessage(messages
								.passwordUpdatedSuccessfully());
						getView().hide();
					}

				});
	}
}
