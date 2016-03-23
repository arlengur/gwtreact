/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyActionType;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyActionWrapper;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.UserServiceAsync;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * @author ivlev.e
 * 
 */
public class PolicyActionsTemplateGridWidgetPresenter
		extends
			AbstractLocalDataGridWidgetPresenter<PolicyActionWrapper, PolicyActionsTemplateGridWidgetPresenter.MyView> {

	public interface MyView
			extends
				AbstractLocalDataGridWidgetPresenter.MyView<PolicyActionWrapper, PolicyActionsTemplateGridWidgetPresenter> {

		void clearObsoleteContacts(Set<String> deletedKeys);

		List<PolicyActionWrapper> getPolicyActionWrappers();

		void reset();

		void setAvailableContacts(List<MContactInformation> contacts);

		void setPolicyActions(List<PolicyActionWrapper> actions);

		boolean validate(boolean isEmptyValid);
	}

	private final UserServiceAsync userService;

	private final QoSMessages messages;

	@Inject
	public PolicyActionsTemplateGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final UserServiceAsync userService,
			final QoSMessages messages) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		this.userService = userService;
		this.messages = messages;
	}

	public void clearObsoleteContacts(final Set<String> deletedKeys) {
		getView().clearObsoleteContacts(deletedKeys);
	}

	public List<MPolicyActionWithContacts> getPolicyActions() {
		final List<MPolicyActionWithContacts> actions = new ArrayList<MPolicyActionWithContacts>();
		final List<PolicyActionWrapper> wrappers = getView()
				.getPolicyActionWrappers();
		for (final PolicyActionWrapper wrapper : wrappers) {
			MPolicyActionWithContacts actionWithContacts = null;
			if (PolicyActionType.EMAIL.toString().equals(wrapper.getType())) {
				actionWithContacts = new MPolicySendEmail();
			} else if (PolicyActionType.SMS.toString()
					.equals(wrapper.getType())) {
				actionWithContacts = new MPolicySendSms();
			}
			if (actionWithContacts != null) {
				actionWithContacts.setContacts(Arrays.asList(wrapper
						.getRecipient()));
				actions.add(actionWithContacts);
			}
		}
		PolicyUtils.setDefaultPolicyActionNames(actions);
		return actions;
	}

	public void loadUsers() {
		userService
				.getAllContactInformations(new AutoNotifyingAsyncLogoutOnFailureCallback<List<MContactInformation>>(
						messages.usersLoadingFail(), true) {

					@Override
					protected void success(
							final List<MContactInformation> contacts) {
						getView().setAvailableContacts(contacts);
					}
				});
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadUsers();
	}

	@Override
	public void reload(final boolean force) {

	}

	public void reset() {
		getView().reset();
	}

	public void setPolicyActions(final List<MPolicyActionWithContacts> actions) {
		final List<PolicyActionWrapper> wrappers = new ArrayList<PolicyActionWrapper>();
		for (final MPolicyActionWithContacts action : actions) {

			final List<MContactInformation> contacts = action.getContacts();
			final MContactInformation recipient = contacts.isEmpty()
					? null
					: contacts.get(0);
			wrappers.add(new PolicyActionWrapper(action.getId(),
					PolicyActionType.getByActionClass(action.getClass()),
					recipient));
		}
		getView().setPolicyActions(wrappers);
	}

	public boolean validate(final boolean isEmptyValid) {
		return getView().validate(isEmptyValid);
	}
}