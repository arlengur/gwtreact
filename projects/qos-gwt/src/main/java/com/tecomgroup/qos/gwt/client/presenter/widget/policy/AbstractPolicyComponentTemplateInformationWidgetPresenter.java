/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;


import java.util.logging.Level;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.gwt.client.event.policy.AfterUpdatePolicyComponentTemplateEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractEntityEditorDialogPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public abstract class AbstractPolicyComponentTemplateInformationWidgetPresenter<M extends MPolicyComponentTemplate, V extends AbstractPolicyComponentTemplateInformationWidgetPresenter.MyView<M, ?>>
		extends
			AbstractEntityEditorDialogPresenter<M, V> implements UiHandlers {

	public static interface MyView<M extends MPolicyComponentTemplate, U extends AbstractPolicyComponentTemplateInformationWidgetPresenter<M, ?>>
			extends
				AbstractEntityEditorDialogPresenter.MyView<M, U> {

		String getName();

		@Override
		void reset();

		void setPolicyComponentTemplateName(String name);

		void showTemplateUpdateModeSelectionDialog();
	}

	protected final PolicyComponentTemplateServiceAsync policyComponentTemplateService;

	protected final QoSMessages messages;

	@Inject
	public AbstractPolicyComponentTemplateInformationWidgetPresenter(
			final EventBus eventBus,
			final V view,
			final QoSMessages messages,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService) {
		super(eventBus, view);
		this.messages = messages;
		this.policyComponentTemplateService = policyComponentTemplateService;
	}

	public void actionOkButtonPressed() {
		if (validateForm()) {
			final M template;
			switch (getCurrentMode()) {
				case CREATE :
					template = createTemplate();
					policyComponentTemplateService.doesTemplateExist(
							template.getName(),
							template.getClass().getName(),
							new AutoNotifyingAsyncCallback<Boolean>(messages
									.policyComponentTemplateCreationFail(),
									true) {
								@Override
								protected void success(
										final Boolean templateExists) {
									if (templateExists) {
										AppUtils.showErrorMessage(messages
												.entityAlreadyExists(messages
														.template()));
									} else {
										saveOrUpdateTemplate(
												template,
												null,
												messages.policyComponentTemplateCreatedSuccessfully(),
												messages.policyComponentTemplateCreationFail(),
												false);
									}
								}
							});
					break;
				case UPDATE :
					final String oldName = editableEntity.getName();
					final String newName = getView().getName();
					policyComponentTemplateService.doesTemplateExist(newName,
							editableEntity.getClass().getName(),
							new AutoNotifyingAsyncCallback<Boolean>() {

								@Override
								protected void success(final Boolean result) {
									if (result && !oldName.equals(newName)) {
										AppUtils.showErrorMessage(messages
												.entityAlreadyExists(messages
														.templateName()));
									} else {
										updateTemplate();
									}
								}
							});
					break;
				default :
					break;
			}
		}
	}

	protected abstract M createTemplate();

	protected void fillTemplate(final M template) {
		template.setName(getView().getName().trim());
	}

	protected abstract String saveOrUpdateFailureHandler(final Throwable caught);

	public void saveOrUpdateTemplate(final boolean reapplyToPolicies) {
		saveOrUpdateTemplate(editableEntity, editableEntity.getName(),
				messages.policyComponentTemplateSavedSuccessfully(),
				messages.policyComponentTemplateSaveFail(), reapplyToPolicies);
	}

	protected void saveOrUpdateTemplate(final M template,
			final String oldTemplateName, final String successMessage,
			final String failMessage, final boolean reapplyToPolicies) {
		policyComponentTemplateService.saveOrUpdateTemplate(template,
				reapplyToPolicies, new AutoNotifyingAsyncCallback<M>(
						failMessage, true) {

					@Override
					protected void failure(final Throwable caught) {
						final String errorMessage = saveOrUpdateFailureHandler(caught);
						LOGGER.log(Level.SEVERE, errorMessage, caught);
						AppUtils.showErrorMessage(errorMessage);
					}

					@Override
					protected void success(final M template) {
						AppUtils.showInfoMessage(successMessage);
						getView().hide();
						AppUtils.getEventBus().fireEvent(
								new AfterUpdatePolicyComponentTemplateEvent(
										template, oldTemplateName));
					}
				});
	}

	@Override
	public void setUpdateMode(final M editableEntity) {
		super.setUpdateMode(editableEntity);
		getView().setPolicyComponentTemplateName(editableEntity.getName());
	}

	protected abstract void updateTemplate();

	protected boolean validateForm() {
		return getView().validate();
	}
}
