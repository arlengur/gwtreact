/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public abstract class AbstractTemplatePresenterWidget
		extends
			PresenterWidget<AbstractTemplatePresenterWidget.MyView>
		implements
			UiHandlers {

	public interface MyView extends PopupView {

		void setTemplate(MUserAbstractTemplate template);
	}

	protected final QoSMessages messages;

	protected final UserServiceAsync userService;

	protected TemplateType templateType;

	protected String selectedTemplateName;

	private final Map<String, MUserAbstractTemplate> templates = new HashMap<String, MUserAbstractTemplate>();

	@Inject
	public AbstractTemplatePresenterWidget(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserServiceAsync userService) {
		super(eventBus, view);
		this.messages = messages;
		this.userService = userService;
	}

	private Map<String, MUserAbstractTemplate> convertTemplatesToMap(
			final List<MUserAbstractTemplate> templates) {
		final Map<String, MUserAbstractTemplate> map = new HashMap<String, MUserAbstractTemplate>();
		for (final MUserAbstractTemplate template : templates) {
			map.put(template.getName(), template);
		}

		return map;
	}

	protected MUser getCurrentUser() {
		return AppUtils.getCurrentUser().getUser();
	}

	public Map<String, MUserAbstractTemplate> getTemplates() {
		return templates;
	}

	public TemplateType getTemplateType() {
		return templateType;
	}

	protected void loadTemplates() {
		userService.getTemplates(templateType, getCurrentUser().getId(),
				new AutoNotifyingAsyncCallback<List<MUserAbstractTemplate>>() {
					@Override
					protected void success(
							final List<MUserAbstractTemplate> result) {
						onLoadTemplates(result);
					}
				});
	}

	protected void onLoadTemplates(final List<MUserAbstractTemplate> result) {
		templates.clear();
		templates.putAll(convertTemplatesToMap(result));
		if (selectedTemplateName != null) {
			final MUserAbstractTemplate template = templates
					.get(selectedTemplateName);
			if (template != null) {
				getView().setTemplate(template);
			}
		}
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		// reset old value from templateControl
		getView().setTemplate(null);
	}

	public void setTemplate(final String templateName) {
		selectedTemplateName = templateName;
	}

	public void setTemplateType(final TemplateType templateType) {
		this.templateType = templateType;
	}
}
