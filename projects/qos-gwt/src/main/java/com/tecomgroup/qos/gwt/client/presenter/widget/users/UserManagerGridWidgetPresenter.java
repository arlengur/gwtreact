/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.users;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.event.user.AfterUserSavedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.UserProperties;
import com.tecomgroup.qos.service.UserManagerServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public class UserManagerGridWidgetPresenter
		extends
			AbstractRemoteDataGridWidgetPresenter<MUser, UserManagerGridWidgetPresenter.MyView>
		implements
			AfterUserSavedEvent.AfterUserSavedEventHandler {

	public interface MyView
			extends
				AbstractRemoteDataGridWidgetPresenter.MyView<MUser, UserManagerGridWidgetPresenter> {

		UserProperties getUserProperties();
	}

	private final UserInformationWidgetPresenter userInformationPresenter;

	private HandlerRegistration saveUserHandlerRegistration;

	private final UserManagerServiceAsync userManagerService;

	private final QoSMessages messages;

	@Inject
	public UserManagerGridWidgetPresenter(final EventBus eventBus,
			final MyView view,
			final UserManagerServiceAsync userManagerService,
			final UserInformationWidgetPresenter userInformationPresenter,
			final QoSMessages messages) {
		super(eventBus, view);
		this.userManagerService = userManagerService;
		this.messages = messages;
		this.userInformationPresenter = userInformationPresenter;
		getView().setUiHandlers(this);
	}

	@SuppressWarnings("unchecked")
	public <X> X cast() {
		return (X) this;
	}

	private void changeUserStatus(final List<MUser> users,
			final boolean isDisabled, final AsyncCallback<Void> callback) {
		final Set<String> keySet = new HashSet<String>();
		for (final MUser user : users) {
			keySet.add(user.getLogin());
		}
		if (isDisabled) {
			userManagerService.disableUsers(keySet, callback);
		} else {
			userManagerService.enableUsers(keySet, callback);
		}
	}

	@Override
	protected Criterion createFilteringCriterion() {
		return null;
	}

	@Override
	protected Criterion createLoadingCriterion() {
		return null;
	}

	public void disableUsers(final List<MUser> users) {
		changeUserStatus(users, true, new AutoNotifyingAsyncCallback<Void>(
				messages.usersDisableFail(), true) {

			@Override
			protected void success(final Void result) {
				getView().reload(true);
				AppUtils.showInfoMessage(messages.usersDisableSuccess());
			}
		});
	}

	public void enableUsers(final List<MUser> users) {
		changeUserStatus(users, false, new AutoNotifyingAsyncCallback<Void>(
				messages.usersEnableFail(), true) {

			@Override
			protected void success(final Void result) {
				getView().reload(true);
				AppUtils.showInfoMessage(messages.usersEnableSuccess());
			}
		});
	}

	public void getUsersCount(final Criterion criterion,
			final AsyncCallback<Long> callback) {
		userManagerService.getUsersCount(criterion, callback);
	}

	public void loadUsers(final Criterion criterion, final Order order,
			final int startPosition, final int size,
			final AsyncCallback<List<MUser>> callback) {
		userManagerService.getUsers(criterion, order, startPosition, size,
				callback);
	}

	@Override
	public void onAfterUserSaved(final AfterUserSavedEvent event) {
		getView().reload(false);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		saveUserHandlerRegistration = getEventBus().addHandler(
				AfterUserSavedEvent.TYPE, this);
	}

	public void openUserInformationDialog(final MUser user) {
		if (user != null) {
			assert (user.getId() != null);
			userInformationPresenter.setUpdateMode(user);
		} else {
			userInformationPresenter.setCreateMode();
		}
		addToPopupSlot(userInformationPresenter, false);
	}
}
