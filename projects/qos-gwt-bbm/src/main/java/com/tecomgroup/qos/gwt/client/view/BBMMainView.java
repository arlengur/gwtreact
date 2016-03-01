/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.tecomgroup.qos.gwt.client.presenter.BBMMainPresenter;

/**
 * @author abondin
 * 
 */
public class BBMMainView extends ViewWithUiHandlers<BBMMainPresenter>
		implements
			BBMMainPresenter.MyView {

	interface ViewUiBinder extends UiBinder<Widget, BBMMainView> {
	}

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private final Widget widget;

	@Inject
	public BBMMainView() {
		widget = UI_BINDER.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@UiHandler("testButton")
	void onTestClicked(final ClickEvent event) {
		getUiHandlers().actionTest();
	}

	@Override
	public void sayHello() {
		Window.alert("Hello");
	}
}
