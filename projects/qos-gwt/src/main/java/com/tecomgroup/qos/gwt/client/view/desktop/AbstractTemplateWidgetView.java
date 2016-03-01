/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AbstractTemplatePresenterWidget;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;

/**
 * @author meleshin.o
 * 
 */
public abstract class AbstractTemplateWidgetView<P extends AbstractTemplatePresenterWidget>
		extends
			SenchaPopupView<P>
		implements
			AbstractTemplatePresenterWidget.MyView {

	protected final AppearanceFactory appearanceFactory;
	protected final QoSMessages messages;
	protected QoSDialog dialog;

	public AbstractTemplateWidgetView(final EventBus eventBus,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(eventBus);
		this.appearanceFactory = appearanceFactory;
		this.messages = messages;
	}

	@Override
	public Widget asWidget() {
		return dialog;
	}

	@Override
	public void show() {
		dialog.show();
	}
}
