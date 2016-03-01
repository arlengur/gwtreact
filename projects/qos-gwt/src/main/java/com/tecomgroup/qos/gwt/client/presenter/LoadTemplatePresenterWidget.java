/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.sencha.gxt.data.shared.ListStore;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.UserTemplateProperties;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author meleshin.o
 * 
 */
public class LoadTemplatePresenterWidget
		extends
			AbstractTemplatePresenterWidget {

	public interface MyView
			extends
				AbstractTemplatePresenterWidget.MyView,
				HasUiHandlers<LoadTemplatePresenterWidget> {
		void initialize();
	}

	private ListStore<MUserAbstractTemplate> store;

	private final UserTemplateProperties properties = GWT
			.create(UserTemplateProperties.class);

	@Inject
	public LoadTemplatePresenterWidget(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final UserServiceAsync userService) {
		super(eventBus, view, messages, userService);

		((MyView) getView()).setUiHandlers(this);
		initializeStore();
	}

	public UserTemplateProperties getProperties() {
		return properties;
	}

	public ListStore<MUserAbstractTemplate> getStore() {
		return store;
	}

	private void initializeStore() {
		store = new ListStore<MUserAbstractTemplate>(properties.key());
	}

	public void loadTemplate(final MUserAbstractTemplate template) {
		getEventBus().fireEvent(new LoadTemplateEvent(template));
	}

	@Override
	protected void onBind() {
		final MyView view = (MyView) getView();
		view.initialize();
	}
	@Override
	protected void onLoadTemplates(final List<MUserAbstractTemplate> result) {
		store.clear();
		store.addAll(result);
		super.onLoadTemplates(result);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadTemplates();
	}
}
