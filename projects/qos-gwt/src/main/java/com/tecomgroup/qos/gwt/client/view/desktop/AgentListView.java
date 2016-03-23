/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.tecomgroup.qos.gwt.client.presenter.AgentListPresenter;

/**
 * @author abondin
 *
 */
public class AgentListView extends ViewWithUiHandlers<AgentListPresenter>
		implements
			AgentListPresenter.MyView {

	private final VerticalLayoutContainer widget;

	@Inject
	public AgentListView() {
		widget = new VerticalLayoutContainer();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		widget.add(content, new VerticalLayoutData(1, 0.5));
	}
}
