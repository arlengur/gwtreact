/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.google.inject.Inject;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.SingleAlertHistoryGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;

/**
 * Shows history for one single alert.
 * 
 * @author novohatskiy.r
 * 
 */
public class SingleAlertHistoryGridWidgetView
		extends
			AlertsHistoryGridWidgetView
		implements
			SingleAlertHistoryGridWidgetPresenter.MyView {

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public SingleAlertHistoryGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		refreshAfterGridDataReload = true;
	}

	@Override
	public void applyDefaultConfiguration() {
		super.applyDefaultConfiguration();
		// only filtering criterion should be applied to filters.
		applyCriterionToFilters(loadConfig, getUiHandlers()
				.getFilteringCriterion());
		hideColumns(new String[]{alertHistoryProperties.alertTypeDisplayName()
				.getPath()}, false);
	}
}
