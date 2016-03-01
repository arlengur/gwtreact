/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.gis;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.gis.AddMapToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.AddAgentsToDashboardView;

/**
 * @author kunilov.p
 *
 */
public class AddMapToDashboardView extends AddAgentsToDashboardView
		implements
			AddMapToDashboardWidgetPresenter.MyView {

	@Inject
	public AddMapToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages) {
		super(eventBus, appearanceFactoryPrvider, messages);
	}

	@Override
	protected void createWidget() {
		getUiHandlers().<AddMapToDashboardWidgetPresenter> cast()
				.actionCreateWidget(getSelectedAgentKeys());
	}

	@Override
	protected String getDialogTitle() {
		return messages.newMapWidget();
	}
}
