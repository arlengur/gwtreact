/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.report;

import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.presenter.ReportsGridWidgetWithVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedPlayerDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.report.ReportsGridWidgetView;

/**
 * @author kunilov.p
 * 
 */
public class ReportsGridWidgetWithVideoView extends ReportsGridWidgetView
		implements
			ReportsGridWidgetWithVideoPresenter.MyView {

	private final RecordedPlayerDialog playerDialog;

	@Inject
	public ReportsGridWidgetWithVideoView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory,
			final PlayerFactory playerFactory, final QoSMessages messages) {
		super(appearanceFactoryProvider, dialogFactory, filterFactory, messages);

		playerDialog = new RecordedPlayerDialog(playerFactory,
				appearanceFactory, messages);
	}

	private ColumnConfig<MAlertReport, MAlertReport> createVideoActionColumn(
			final String columnDisplayName, final ImageResource icon) {

		final IconedActionCell<MAlertReport> actionCell = new IconedActionCell<MAlertReport>(
				appearanceFactory.<MAlertReport> iconedActionCellAppearance(
						icon, columnDisplayName), columnDisplayName,
				new ActionCell.Delegate<MAlertReport>() {

					@Override
					public void execute(final MAlertReport alertReport) {
						getUiHandlers()
								.<ReportsGridWidgetWithVideoPresenter> cast()
								.loadRelatedVideo(alertReport);
					}
				}) {

			@Override
			public void render(final Context context,
					final MAlertReport alertReport, final SafeHtmlBuilder sb) {
				if (alertReport.getAlert().getRelatedRecordingTaskKey() != null) {
					super.render(context, alertReport, sb);
				}
			};
		};
		final ColumnConfig<MAlertReport, MAlertReport> actionColumn = new ColumnConfig<MAlertReport, MAlertReport>(
				createReportEmptyValueProvider(columnDisplayName));
		actionColumn.setCell(actionCell);

		return actionColumn;
	}
	@Override
	protected List<ColumnConfig<MAlertReport, ?>> getGridColumns() {
		final ColumnConfig<MAlertReport, MAlertReport> videoActionColumn = createVideoActionColumn(
				messages.video(), appearanceFactory.resources().playVideo());
		videoActionColumn.setSortable(false);
		videoActionColumn.setResizable(false);
		videoActionColumn.setFixed(true);
		videoActionColumn.setMenuDisabled(true);
		videoActionColumn.setWidth(30);

		final List<ColumnConfig<MAlertReport, ?>> columns = super
				.getGridColumns();
		columns.add(videoActionColumn);

		return columns;
	}

	@Override
	public void showPlayerDialog(final MRecordedStream stream,
			final Date startDateTime, final Date endDateTime) {
		playerDialog.show(stream, startDateTime, endDateTime);
	}
}
