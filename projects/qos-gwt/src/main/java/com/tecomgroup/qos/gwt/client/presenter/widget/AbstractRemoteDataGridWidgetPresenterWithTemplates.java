/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent.LoadTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent.SaveTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * Abstract grid presenter with template functionality
 * 
 * @author sviyazov.a
 * 
 */
public abstract class AbstractRemoteDataGridWidgetPresenterWithTemplates<M, V extends AbstractRemoteDataGridWidgetPresenterWithTemplates.SharedDataGridWidgetViewWithTemplates<M, ?>>
		extends
			AbstractRemoteDataGridWidgetPresenter<M, V>
		implements
			LoadTemplateEventHandler,
			SaveTemplateEventHandler {

	public interface SharedDataGridWidgetViewWithTemplates<M, U extends AbstractRemoteDataGridWidgetPresenterWithTemplates<M, ?>>
			extends
				AbstractRemoteDataGridWidgetPresenter.MyView<M, U> {

		void setTemplateLabel(String templateName);
	}

	protected final LoadTemplatePresenterWidget loadTemplatePresenter;

	protected final SaveTemplatePresenterWidget saveTemplatePresenter;

	private HandlerRegistration loadTemplateHandlerRegistration;

	private HandlerRegistration saveTemplateHandlerRegistration;

	private String selectedTemplateName;

	protected final UserServiceAsync userService;

	public AbstractRemoteDataGridWidgetPresenterWithTemplates(
			final EventBus eventBus, final V view,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final UserServiceAsync userService) {
		super(eventBus, view);
		this.loadTemplatePresenter = loadTemplatePresenter;
		this.saveTemplatePresenter = saveTemplatePresenter;
		this.userService = userService;
	}

	protected abstract TemplateType getTemplateType();

	protected void loadSelectedTemplate() {
		userService.getTemplate(getTemplateType(), AppUtils.getCurrentUser()
				.getUser().getId(), selectedTemplateName,
				new AutoNotifyingAsyncLogoutOnFailureCallback<MUserAbstractTemplate>() {

					@Override
					protected void success(final MUserAbstractTemplate template) {
						AppUtils.getEventBus().fireEvent(
								new LoadTemplateEvent(template));
					}

				});
	}

	@Override
	public void loadTemplate(final LoadTemplateEvent event) {
		setCurrentTemplate(event.getTemplate());
	}

	@Override
	protected void onHide() {
		super.onHide();
		if (loadTemplateHandlerRegistration != null) {
			loadTemplateHandlerRegistration.removeHandler();
		}
		if (saveTemplateHandlerRegistration != null) {
			saveTemplateHandlerRegistration.removeHandler();
		}
	}

	@Override
	protected void onReveal() {
		onReveal(true);
	}

	protected void onReveal(final boolean forceReload) {
		super.onReveal();

		if (selectedTemplateName != null) {
			loadSelectedTemplate();
		}

		reload(forceReload);
		loadTemplateHandlerRegistration = getEventBus().addHandler(
				LoadTemplateEvent.TYPE, this);
		saveTemplateHandlerRegistration = getEventBus().addHandler(
				SaveTemplateEvent.TYPE, this);
	}

	public void openLoadTemplateDialog() {
		loadTemplatePresenter.setTemplate(selectedTemplateName);
		loadTemplatePresenter.setTemplateType(getTemplateType());

		addToPopupSlot(loadTemplatePresenter, false);
	}

	public void openSaveTemplateDialog() {
		saveTemplatePresenter.setTemplate(selectedTemplateName);
		saveTemplatePresenter.setTemplateType(getTemplateType());

		addToPopupSlot(saveTemplatePresenter, false);
	}

	@Override
	public void saveTemplate(final SaveTemplateEvent event) {
		final MUserAbstractTemplate template = event.getTemplate();
		if (template.isValid()) {
			setCurrentTemplate(template);
		} else {
			saveTemplatePresenter.setTemplate(template.getName());
		}
	}

	public void setCurrentTemplate(final MUserAbstractTemplate template) {
		setSelectedTemplateName(template.getName());
		setTemplateLabel(template.getName());
		saveTemplatePresenter.setTemplate(selectedTemplateName);
		loadTemplatePresenter.setTemplate(selectedTemplateName);
	}

	public void setSelectedTemplateName(final String selectedTemplateName) {
		this.selectedTemplateName = selectedTemplateName;
	}

	public void setTemplateLabel(final String templateName) {
		getView().setTemplateLabel(templateName);
	}
}
