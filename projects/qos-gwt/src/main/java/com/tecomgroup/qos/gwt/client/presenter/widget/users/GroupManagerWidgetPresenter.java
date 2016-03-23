/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.gwt.client.event.user.AfterGroupSavedEvent;
import com.tecomgroup.qos.gwt.client.event.user.AfterGroupSavedEvent.AfterGroupSavedEventHandler;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.model.users.UserGroupRow;
import com.tecomgroup.qos.gwt.client.model.users.UserRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataTreeGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.UserGroupServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class GroupManagerWidgetPresenter
		extends
			AbstractLocalDataTreeGridWidgetPresenter<TreeGridRow, GroupManagerWidgetPresenter.MyView>
		implements
			AfterGroupSavedEventHandler {

	public interface MyView
			extends
				AbstractLocalDataTreeGridWidgetPresenter.MyView<TreeGridRow, GroupManagerWidgetPresenter> {
		void onGroupRemoved(String name);

		void onGroupSaved(TreeGridRow group, List<TreeGridRow> users);

		void setGroups(Map<TreeGridRow, Collection<TreeGridRow>> groups);
	}

	private final UserGroupServiceAsync userGroupService;

	private final GroupInformationWidgetPresenter groupInformationWidgetPresenter;

	@Inject
	public GroupManagerWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final UserGroupServiceAsync userGroupService,
			final GroupInformationWidgetPresenter groupInformationWidgetPresenter) {
		super(eventBus, view);
		this.userGroupService = userGroupService;
		this.groupInformationWidgetPresenter = groupInformationWidgetPresenter;
		getView().setUiHandlers(this);
		getEventBus().addHandler(AfterGroupSavedEvent.TYPE, this);
	}

	public void actionOpenGroupCreationDialog() {
		groupInformationWidgetPresenter.setCreateMode();
		addToPopupSlot(groupInformationWidgetPresenter, true);
	}

	public void actionOpenGroupEditDialog(final String groupName) {
		userGroupService.getGroupByName(
				groupName,
				new AutoNotifyingAsyncLogoutOnFailureCallback<MUserGroup>(messages
						.groupsLoadingFail(), true) {

					@Override
					protected void success(final MUserGroup group) {
						groupInformationWidgetPresenter.setUpdateMode(group);
						addToPopupSlot(groupInformationWidgetPresenter, true);
					}
				});
	}

	public void actionRemoveGroup(final String name) {
		userGroupService.removeGroup(
				name,
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(messages
						.groupRemovalFail(), false) {

					@Override
					protected void success(final Void result) {
						getView().onGroupRemoved(name);
						AppUtils.showInfoMessage(messages
								.groupDeletedSuccessfully());
					}
				});
	}

	private Map<TreeGridRow, Collection<TreeGridRow>> convertToGridModels(
			final Collection<MUserGroup> groups) {
		final Map<TreeGridRow, Collection<TreeGridRow>> gridModels = new LinkedHashMap<TreeGridRow, Collection<TreeGridRow>>();
		for (final MUserGroup group : groups) {
			final UserGroupRow groupRow = getGroupRow(group);
			final List<TreeGridRow> userRows = getUserRows(group);
			gridModels.put(groupRow, userRows);
		}
		return gridModels;
	}

	private UserGroupRow getGroupRow(final MUserGroup group) {
		return new UserGroupRow(group.getName());
	}

	private List<TreeGridRow> getUserRows(final MUserGroup group) {
		final List<TreeGridRow> userRows = new ArrayList<TreeGridRow>();
		for (final MUser user : group.getUsers()) {
			userRows.add(new UserRow(user, group.getName()));
		}
		return userRows;
	}

	private void loadGroups() {
		userGroupService
				.getAllGroups(new AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MUserGroup>>(
						messages.groupsLoadingFail(), true) {

					@Override
					protected void success(final Collection<MUserGroup> groups) {
						getView().setGroups(convertToGridModels(groups));
					}
				});
	}

	@Override
	public void onAfterGroupSaved(final AfterGroupSavedEvent event) {
		final MUserGroup group = event.getGroup();
		final TreeGridRow groupRow = getGroupRow(group);
		final List<TreeGridRow> userRows = getUserRows(group);

		final String oldGroupName = event.getOldGroupName();
		// delete group with changed name
		if (oldGroupName != null && !oldGroupName.equals(group.getName())) {
			getView().onGroupRemoved(event.getOldGroupName());
		}
		getView().onGroupSaved(groupRow, userRows);
	}

	@Override
	public void reload(final boolean force) {
		loadGroups();
	}
}
