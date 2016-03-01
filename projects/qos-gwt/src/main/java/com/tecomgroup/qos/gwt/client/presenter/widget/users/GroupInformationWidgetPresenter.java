/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.users;

import java.util.List;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.gwt.client.event.user.AfterGroupSavedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractEntityEditorDialogPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.GroupInformationWidgetPresenter.MyView;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.UserGroupServiceAsync;
import com.tecomgroup.qos.service.UserManagerServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class GroupInformationWidgetPresenter
		extends
			AbstractEntityEditorDialogPresenter<MUserGroup, MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				AbstractEntityEditorDialogPresenter.MyView<MUserGroup, GroupInformationWidgetPresenter> {
		String getGroupName();

		List<MUser> getGroupUsers();

		/**
		 * Sets all users exixting in the system
		 * 
		 * @param users
		 */
		void setAllUsers(List<MUser> users);

		void setGroupName(String groupName);

		/**
		 * Sets users of editable group
		 * 
		 * @param users
		 */
		void setGroupUsers(List<MUser> users);

		void showErrorMessage(String title, String message);
	}

	private final QoSMessages messages;

	private final UserManagerServiceAsync userManagerService;

	private final UserGroupServiceAsync userGroupService;

	@Inject
	public GroupInformationWidgetPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserManagerServiceAsync userManagerService,
			final UserGroupServiceAsync userGroupService) {
		super(eventBus, view);
		this.messages = messages;
		this.userManagerService = userManagerService;
		this.userGroupService = userGroupService;
		getView().setUiHandlers(this);
	}

	public void actionOkButtonPressed() {
		if (getView().validate()) {
			final MUserGroup group;
			final String failMessage;
			final String successMessage;
			switch (getCurrentMode()) {
				case CREATE :
					group = new MUserGroup();
					fillGroup(group);
					failMessage = messages.groupCreationFail();
					successMessage = messages.groupCreatedSuccessfully();
					userGroupService.doesGroupExist(group.getName(),
							new AutoNotifyingAsyncCallback<Boolean>() {

								@Override
								protected void success(final Boolean groupExists) {
									if (groupExists) {
										getView()
												.showErrorMessage(
														messages.groupSaveFailTitle(),
														messages.entityAlreadyExists(messages
																.group()));
									} else {
										saveOrUpdateGroup(group, null,
												successMessage, failMessage);
									}
								}
							});
					break;
				case UPDATE :
					failMessage = messages.groupSaveFail();
					successMessage = messages.groupSavedSuccessfully();
					final String oldGroupName = editableEntity.getName();
					final String newGroupName = getView().getGroupName();
					userGroupService.doesGroupExist(newGroupName,
							new AutoNotifyingAsyncCallback<Boolean>() {

								@Override
								protected void success(final Boolean groupExists) {
									if (groupExists
											&& !oldGroupName
													.equals(newGroupName)) {
										getView()
												.showErrorMessage(
														messages.groupSaveFailTitle(),
														messages.entityAlreadyExists(messages
																.group()));
									} else {
										fillGroup(editableEntity);
										saveOrUpdateGroup(editableEntity,
												oldGroupName, successMessage,
												failMessage);
									}
								}
							});
					break;
				default :
					break;
			}
		}
	}

	private void fillGroup(final MUserGroup group) {
		group.setName(getView().getGroupName().trim());
		group.setUsers(getView().getGroupUsers());
	}

	private void loadUsers() {
		userManagerService
				.getAllUsers(new AutoNotifyingAsyncCallback<List<MUser>>(
						messages.usersLoadingFail(), true) {

					@Override
					protected void success(final List<MUser> users) {
						getView().setAllUsers(users);
					}
				});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadUsers();
	}

	private void saveOrUpdateGroup(final MUserGroup group,
			final String oldGroupName, final String successMessage,
			final String failMessage) {
		userGroupService.saveOrUpdateGroup(group,
				new AutoNotifyingAsyncCallback<MUserGroup>(failMessage, true) {

					@Override
					protected void success(final MUserGroup savedGroup) {
						AppUtils.showInfoMessage(successMessage);
						getView().hide();
						AppUtils.getEventBus().fireEvent(
								new AfterGroupSavedEvent(savedGroup,
										oldGroupName));
					}
				});
	}

	@Override
	public void setUpdateMode(final MUserGroup editableGroup) {
		super.setUpdateMode(editableGroup);
		getView().setGroupName(editableGroup.getName());
		getView().setGroupUsers(editableGroup.getUsers());
	}
}
