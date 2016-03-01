/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import com.google.gwt.dom.client.Element;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;

/**
 * A chart instance which can be injected in any page. See examples in
 * {@link QoSNameTokens#tableResults} and {@link QoSNameTokens#alertDetails}
 *
 * @author sviyazov.a
 *
 */
public class ChartWidgetPresenter
		extends
			PresenterWidget<ChartWidgetPresenter.MyView> implements UiHandlers {

	public interface MyView extends View, HasUiHandlers<ChartWidgetPresenter> {
	}

	private final QoSMessages messages;

	@Inject
	public ChartWidgetPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		this.messages = AppUtils.getMessages();
	}

	public void createChart(final ChartSettings settings) {
		getView().asWidget().getElement().setId(settings.getDivElementId());
		ChartResultUtils.createChart(settings, getChartHeight(),
				messages.time());
	}

	private int getChartHeight() {
		final Element domElement = getView().asWidget().getElement();
		int chartHeight = domElement.getOffsetHeight();
		chartHeight = chartHeight == 0 ? domElement.getParentElement()
				.getOffsetHeight() : chartHeight;
		return chartHeight;
	}

	@Override
	protected void onBind() {
		super.onBind();
		ChartResultUtils.initGeneralChartParameters(messages);
	}
}
