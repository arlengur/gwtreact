/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.CrudOperations;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.exception.UserValidationException;
import com.tecomgroup.qos.gwt.client.event.user.AfterUserSavedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractEntityEditorDialogPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.UserManagerServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public class UserInformationWidgetPresenter
		extends
			AbstractEntityEditorDialogPresenter<MUser, UserInformationWidgetPresenter.MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				AbstractEntityEditorDialogPresenter.MyView<MUser, UserInformationWidgetPresenter> {
		void setLdapUsers(List<MUser> result);

		void setUser(MUser user);
	}

	private final QoSMessages messages;

	private final UserManagerServiceAsync userManagerService;

	private final Map<String, MUser> users = new HashMap<String, MUser>();

	private final UserServiceAsync userService;

	@Inject
	public UserInformationWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserManagerServiceAsync userManagerService,
			final UserServiceAsync userService) {
		super(eventBus, view);
		this.userManagerService = userManagerService;
		this.userService = userService;
		this.messages = messages;
		loadLdapUsers();
		getView().setUiHandlers(this);
	}

	private Map<String, MUser> convertUsersToMap(final List<MUser> users) {
		final Map<String, MUser> map = new HashMap<String, MUser>();
		for (final MUser user : users) {
			map.put(user.getLogin(), user);
		}
		return map;
	}

	public Map<String, MUser> getUsers() {
		return users;
	}

	private void loadLdapUsers() {
		userService.getLdapUsers(new AutoNotifyingAsyncCallback<List<MUser>>() {

			@Override
			protected void success(final List<MUser> result) {
				getView().setLdapUsers(result);
			}
		});
	}

	private void loadUsers() {
		userManagerService
				.getAllUsers(new AutoNotifyingAsyncCallback<List<MUser>>() {

					@Override
					protected void success(final List<MUser> loadedUsers) {
						onLoadUsers(loadedUsers);
					}
				});
	}

	@Override
	protected void onHide() {
		super.onHide();
		getView().hide();
	}

	private void onLoadUsers(final List<MUser> loadedUsers) {
		users.clear();
		users.putAll(convertUsersToMap(loadedUsers));
		MUser userToWorkWith = null;
		if (CrudOperations.UPDATE.equals(getCurrentMode())) {
			userToWorkWith = editableEntity;
		} else {
			userToWorkWith = new MUser();
		}
		getView().setUser(userToWorkWith);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadUsers();
	}

	public void saveOrUpdateUser(final MUser userToSaveOrUpdate,
			final boolean updatePassword) {
		userManagerService.saveOrUpdateUser(userToSaveOrUpdate, updatePassword,
				new AutoNotifyingAsyncCallback<MUser>(
						messages.userSavingFail(), false) {

					@Override
					protected void failure(final Throwable caught) {
						super.failure(caught);

						if (caught instanceof UserValidationException) {
							final UserValidationException userValidationException = (UserValidationException) caught;
							final UserValidationException.Reason reason = userValidationException
									.getReason();
							if (reason == UserValidationException.Reason.INCORRECT_LOGIN_FORMAT) {
								AppUtils.showErrorMessage(messages
										.incorrectLoginFormat());
							} else if (reason == UserValidationException.Reason.INCORRECT_EMAIL_FORMAT) {
								AppUtils.showErrorMessage(messages
										.incorrectEmailFormat());
							} else if (reason == UserValidationException.Reason.INCORRECT_PHONE_NUMBER_FORMAT) {
								AppUtils.showErrorMessage(messages
										.incorrectPhoneNumberFormat());
							}
						} else {
							AppUtils.showErrorMessage(messages.userSavingFail());
						}
					}

					@Override
					protected void success(final MUser savedOrUpdatedUser) {
						getEventBus().fireEvent(
								new AfterUserSavedEvent(savedOrUpdatedUser));

						final String message;
						if (userToSaveOrUpdate.getId() == null) {
							message = messages.userCreatedSuccessfully();
						} else {
							message = messages.userUpdatedSuccessfully();
						}
						AppUtils.showInfoMessage(message);
						getView().hide();
					}
				});
	}
}
