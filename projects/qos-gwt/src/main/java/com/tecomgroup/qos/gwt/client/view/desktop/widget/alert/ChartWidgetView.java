/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartWidgetPresenter;

/**
 * @author sviyazov.a
 * 
 */
public class ChartWidgetView
		extends
			ViewWithUiHandlers<ChartWidgetPresenter>
		implements
			ChartWidgetPresenter.MyView {

	private final SimplePanel chartContainer;

	@Inject
	public ChartWidgetView() {
		chartContainer = new SimplePanel();
	}

	@Override
	public Widget asWidget() {
		return chartContainer;
	}
}
