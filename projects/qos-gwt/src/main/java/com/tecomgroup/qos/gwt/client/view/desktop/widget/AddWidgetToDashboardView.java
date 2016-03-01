/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddWidgetToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;

/**
 * @author tabolin.a
 *
 */
public class AddWidgetToDashboardView extends AddNamedWidgetToDashboardView
		implements
			AddWidgetToDashboardWidgetPresenter.MyView {
	private final static int DEFAULT_DIALOG_HEIGHT = 150;

	private final static int DEFAULT_DIALOG_WIDTH = 340;

	@Inject
	public AddWidgetToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages) {
		super(eventBus, appearanceFactoryPrvider, messages);

	}

	@Override
	protected void createWidget() {
		getUiHandlers().<AddWidgetToDashboardWidgetPresenter> cast()
		.actionCreateWidget();
	}

	@Override
	protected int getDialogHeight() {
		return DEFAULT_DIALOG_HEIGHT;
	}

	@Override
	protected int getDialogWidth() {
		return DEFAULT_DIALOG_WIDTH;
	}

}
