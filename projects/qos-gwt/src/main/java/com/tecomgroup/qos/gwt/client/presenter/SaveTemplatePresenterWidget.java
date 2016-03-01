/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.template.TemplateFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public class SaveTemplatePresenterWidget
		extends
			AbstractTemplatePresenterWidget {

	public interface MyView
			extends
				AbstractTemplatePresenterWidget.MyView,
				HasUiHandlers<SaveTemplatePresenterWidget> {

	}

	private final TemplateFactory templateFactory;
	@Inject
	public SaveTemplatePresenterWidget(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserServiceAsync userService,
			final TemplateFactory templateFactory) {
		super(eventBus, view, messages, userService);

		this.templateFactory = templateFactory;

		((MyView) getView()).setUiHandlers(this);
	}

	public MUserAbstractTemplate createTemplate(final String templateName) {
		return templateFactory.createTemplate(templateType, templateName);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadTemplates();
	}

	public void saveTemplate(final MUserAbstractTemplate template) {
		if (template.getUser() == null) {
			template.setUser(getCurrentUser());
		}
		getEventBus().fireEvent(new SaveTemplateEvent(template));

		if (!template.isValid()) {
			AppUtils.showInfoMessage(messages.templateIsNotValid());
		} else {
			userService.saveTemplate(template,
					new AutoNotifyingAsyncCallback<MUserAbstractTemplate>(
							messages.templateSavingFail(), true) {

						@Override
						protected void success(
								final MUserAbstractTemplate result) {
							AppUtils.showInfoMessage(messages
									.templateSavingSuccess());
						}
					});
		}
	}
}
