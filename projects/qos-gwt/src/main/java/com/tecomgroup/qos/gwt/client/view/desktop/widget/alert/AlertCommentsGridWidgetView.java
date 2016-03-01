/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.gwt.client.bean.AlertCommentDetails;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AlertCommentsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.AlertCommentDetailsValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AlertCommentCell;

/**
 * @author novohatskiy.r
 * 
 */
public class AlertCommentsGridWidgetView
		extends
			SingleAlertHistoryGridWidgetView
		implements
			AlertCommentsGridWidgetPresenter.MyView {

	/**
	 * @param messages
	 * @param appearanceFactoryProvider
	 * @param dialogFactory
	 * @param filterFactory
	 */
	@Inject
	public AlertCommentsGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
	}

	@Override
	public void applyDefaultConfiguration() {
		super.applyDefaultConfiguration();
		hideColumns(new String[]{alertHistoryProperties.dateTime().getPath(),
				""}, false);
	}

	@Override
	protected List<ColumnConfig<MAlertUpdate, ?>> getGridColumns() {
		final ColumnConfig<MAlertUpdate, Date> dateTime = new ColumnConfig<MAlertUpdate, Date>(
				alertHistoryProperties.dateTime(), 50, messages.time());
		final ColumnConfig<MAlertUpdate, AlertCommentDetails> column = new ColumnConfig<MAlertUpdate, AlertCommentDetails>(
				new AlertCommentDetailsValueProvider(messages));
		column.setCell(new AlertCommentCell(appearanceFactory
				.alertCommentCellAppearance()));
		final List<ColumnConfig<MAlertUpdate, ?>> columns = new ArrayList<ColumnConfig<MAlertUpdate, ?>>();
		columns.add(dateTime);
		columns.add(column);
		return columns;
	}

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		grid.getView().setStripeRows(false);
		grid.setHideHeaders(true);
		grid.setSelectionModel(null);
	}

}
