/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.List;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.exception.DeletedContactInformationException;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplateInformationWidgetPresenter.MyView;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * @author ivlev.e
 *
 */
public class PolicyActionsTemplateInformationWidgetPresenter
		extends
			AbstractPolicyComponentTemplateInformationWidgetPresenter<MPolicyActionsTemplate, MyView>
		implements
			UiHandlers {

	public static interface MyView
			extends
				AbstractPolicyComponentTemplateInformationWidgetPresenter.MyView<MPolicyActionsTemplate, PolicyActionsTemplateInformationWidgetPresenter> {

		@Override
		String getName();

		@Override
		void reset();

		@Override
		void showTemplateUpdateModeSelectionDialog();
	}

	private final PolicyActionsTemplateGridWidgetPresenter policyActionsTemplateGridWidgetPresenter;

	@Inject
	public PolicyActionsTemplateInformationWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final QoSMessages messages,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final PolicyActionsTemplateGridWidgetPresenter policyActionsTemplateGridWidgetPresenter) {
		super(eventBus, view, messages, policyComponentTemplateService);
		getView().setUiHandlers(this);
		this.policyActionsTemplateGridWidgetPresenter = policyActionsTemplateGridWidgetPresenter;
	}

	@Override
	protected MPolicyActionsTemplate createTemplate() {
		final MPolicyActionsTemplate template = new MPolicyActionsTemplate();
		fillTemplate(template,
				policyActionsTemplateGridWidgetPresenter.getPolicyActions());
		PolicyUtils.setDefaultPolicyActionNames(template.getActions());

		return template;
	}

	private void fillTemplate(final MPolicyActionsTemplate template,
			final List<MPolicyActionWithContacts> actions) {
		super.fillTemplate(template);
		template.setActions(actions);
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(1, policyActionsTemplateGridWidgetPresenter);
	}

	@Override
	protected void reset() {
		super.reset();
		policyActionsTemplateGridWidgetPresenter.reset();
	}

	@Override
	protected String saveOrUpdateFailureHandler(final Throwable caught) {
		String errorMessage = null;
		if (caught instanceof DeletedContactInformationException) {
			errorMessage = messages.deletedRecipientsError();

			final DeletedContactInformationException exception = (DeletedContactInformationException) caught;
			policyActionsTemplateGridWidgetPresenter
					.clearObsoleteContacts(exception.getDeletedKeys());
			policyActionsTemplateGridWidgetPresenter.loadUsers();
		} else {
			errorMessage = caught.getMessage();
		}
		return errorMessage;
	}

	@Override
	public void saveOrUpdateTemplate(final boolean reapplyToPolicies) {
		fillTemplate(editableEntity,
				policyActionsTemplateGridWidgetPresenter.getPolicyActions());
		super.saveOrUpdateTemplate(reapplyToPolicies);
	}

	@Override
	public void setUpdateMode(final MPolicyActionsTemplate editableEntity) {
		super.setUpdateMode(editableEntity);
		policyActionsTemplateGridWidgetPresenter
				.setPolicyActions(editableEntity.getActions());
	}

	@Override
	protected void updateTemplate() {
		final List<MPolicyActionWithContacts> actions = policyActionsTemplateGridWidgetPresenter
				.getPolicyActions();
		if (!PolicyUtils.arePolicyActionsEqual(actions,
				editableEntity.getActions())) {
			getView().showTemplateUpdateModeSelectionDialog();
		} else {
			saveOrUpdateTemplate(false);
		}
	}

	@Override
	protected boolean validateForm() {
		return super.validateForm()
				&& policyActionsTemplateGridWidgetPresenter.validate(false);
	}
}
