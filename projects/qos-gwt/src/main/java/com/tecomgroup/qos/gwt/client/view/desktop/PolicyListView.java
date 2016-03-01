/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.logging.Logger;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.PolicyListPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;

/**
 * @author ivlev.e
 * 
 */
public class
        PolicyListView extends ViewWithUiHandlers<PolicyListPresenter>
		implements
			PolicyListPresenter.MyView {

	public static Logger LOGGER = Logger.getLogger(PolicyListView.class
			.getName());

	private final BorderLayoutContainer widget;

	@Inject
	public PolicyListView(final AppearanceFactoryProvider factoryProvider,
			final QoSMessages messages) {
		widget = new BorderLayoutContainer(factoryProvider.get()
				.borderLayoutAppearance());
		widget.setBorders(false);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		widget.setWidget(content);
	}

}
