/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.BuildInfo;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.SystemInformationPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;

/**
 * @author abondin
 * 
 */
public class SystemInformationView
		extends
			ViewWithUiHandlers<SystemInformationPresenter>
		implements
			SystemInformationPresenter.MyView {
	interface ViewUiBinder extends UiBinder<Widget, SystemInformationView> {
	}

	@UiField(provided = true)
	Dialog dialog;
	@UiField
	protected TextField applicationVersion;
	@UiField
	protected SimpleContainer applicationVersionContainer;
	@UiField
	protected TextField buildTime;
	@UiField
	protected SimpleContainer buildTimeContainer;

	private final Widget widget;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	@Inject
	public SystemInformationView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		dialog = new QoSDialog(appearanceFactoryProvider.get(), messages) {

			@Override
			protected String getTitleText(final QoSMessages messages) {
				return messages.applicationVersion();
			}

			@Override
			protected void initializeComponents() {
				// do nothing
			}
		};
		widget = UI_BINDER.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setBuildInfo(final BuildInfo buildInfo) {
		if (buildInfo != null) {
			applicationVersion.setText(buildInfo.getApplicationVersion());
			setTimeField(buildTime, buildInfo.getBuildTime());
		}
	}

	private void setTimeField(final TextField input, final Date date) {
		input.setText(DateUtils.DATE_TIME_FORMATTER.format(date));
	}
}
