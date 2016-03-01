/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.series.PieSeries;
import com.sencha.gxt.chart.client.chart.series.Series.LabelPosition;
import com.sencha.gxt.chart.client.chart.series.SeriesLabelConfig;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.Gradient;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.Stop;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextAnchor;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite.TextBaseline;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.dashboard.EmergencyAgentsTopWidget;
import com.tecomgroup.qos.dashboard.EmergencyAgentsTopWidget.ChartData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ColorConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.AbstractWidgetTileContentElement;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author ivlev.e
 *
 */
public class EmergencyAgentsTopClientWidget
		extends
			AbstractWidgetTileContentElement<EmergencyAgentsTopWidget> {

	public interface ChartDataPropertyAccess extends PropertyAccess<ChartData> {
		ValueProvider<ChartData, String> displayName();

		@Path("displayName")
		ModelKeyProvider<ChartData> key();

		ValueProvider<ChartData, Integer> summaryDuration();
	}

	private class LegendLabelProivider implements LabelProvider<String> {

		private final static int MAX_AGENT_NAME_LENGTH = 10;

		@Override
		public String getLabel(String item) {
			if (item.equals(EmergencyAgentsTopWidget.OTHERS_AGENTS)) {
				item = messages.others();
			} else if (item.length() > MAX_AGENT_NAME_LENGTH) {
				item = item.substring(0, MAX_AGENT_NAME_LENGTH) + "...";
			}
			return item;
		}
	}

	/*
	 * TODO: get the property of this value from client properties map.
	 */
	private final static int REFRESH_DELAY_IN_SEC = 600;

	private static final ChartDataPropertyAccess chartDataProperties = GWT
			.create(ChartDataPropertyAccess.class);

	private final Chart<ChartData> chart;

	private final ListStore<ChartData> store;

	private final UserServiceAsync userService;

	private final VerticalLayoutContainer content;

	private final QoSMessages messages;

	private Timer loadingDataTask;

	private final Label noDataLabel;

	public EmergencyAgentsTopClientWidget(final UserServiceAsync userService,
			final EmergencyAgentsTopWidget model, final QoSMessages messages) {
		super(model);
		this.userService = userService;
		this.messages = messages;
		this.noDataLabel = new Label(messages.noAnalyticsData());
		noDataLabel.addStyleName(AppearanceFactoryProvider.instance()
				.resources().css().textMainColor());

		store = new ListStore<ChartData>(chartDataProperties.key());

		chart = new Chart<ChartData>();
		chart.setShadowChart(false);
		chart.setStore(store);
		chart.setBackground(Color.NONE);

		final PieSeries<ChartData> pieSeries = createPieSeries();
		configureColors(pieSeries);
		chart.addSeries(pieSeries);

		chart.setLegend(createLegend());

		content = new VerticalLayoutContainer();
		content.setScrollMode(ScrollMode.NONE);
	}

	private void configureColors(final PieSeries<ChartData> pieSeries) {
		final Gradient slice1 = new Gradient("slice1", 45);
		slice1.addStop(new Stop(0, new RGB(148, 174, 10)));
		slice1.addStop(new Stop(100, new RGB(107, 126, 7)));
		chart.addGradient(slice1);

		final Gradient slice2 = new Gradient("slice2", 45);
		slice2.addStop(new Stop(0, new RGB(17, 95, 166)));
		slice2.addStop(new Stop(100, new RGB(12, 69, 120)));
		chart.addGradient(slice2);

		final Gradient slice3 = new Gradient("slice3", 45);
		slice3.addStop(new Stop(0, new RGB(166, 17, 32)));
		slice3.addStop(new Stop(100, new RGB(120, 12, 23)));
		chart.addGradient(slice3);

		final Gradient slice4 = new Gradient("slice4", 45);
		slice4.addStop(new Stop(0, new RGB(255, 136, 9)));
		slice4.addStop(new Stop(100, new RGB(213, 110, 0)));
		chart.addGradient(slice4);

		final Gradient slice5 = new Gradient("slice5", 45);
		slice5.addStop(new Stop(0, new RGB(255, 209, 62)));
		slice5.addStop(new Stop(100, new RGB(255, 197, 11)));
		chart.addGradient(slice5);

		final Gradient slice6 = new Gradient("slice6", 45);
		slice6.addStop(new Stop(0, new RGB(166, 17, 135)));
		slice6.addStop(new Stop(100, new RGB(120, 12, 97)));
		chart.addGradient(slice6);

		pieSeries.addColor(slice1);
		pieSeries.addColor(slice2);
		pieSeries.addColor(slice3);
		pieSeries.addColor(slice4);
	}

	private SeriesLabelConfig<ChartData> createLabelConfig() {
		final TextSprite textConfig = new TextSprite();
		textConfig.setTextBaseline(TextBaseline.MIDDLE);
		textConfig.setFontSize(14);
		textConfig.setTextAnchor(TextAnchor.MIDDLE);

		final SeriesLabelConfig<ChartData> labelConfig = new SeriesLabelConfig<ChartData>();
		labelConfig.setSpriteConfig(textConfig);
		labelConfig.setLabelPosition(LabelPosition.START);
		labelConfig.setValueProvider(chartDataProperties.summaryDuration(),
				new LabelProvider<Integer>() {

					@Override
					public String getLabel(final Integer item) {
						return item.toString() + "%";
					}
				});
		return labelConfig;
	}

	private Legend<ChartData> createLegend() {
		final Legend<ChartData> legend = new Legend<ChartData>();
		legend.setPosition(Position.RIGHT);
		legend.setItemHighlighting(true);
		legend.setItemHiding(true);
		legend.getBorderConfig().setStrokeWidth(0);
		legend.getBorderConfig().setFill(
				new Color(ColorConstants
						.getDashboardChartLegendBackgroundColor()));
		return legend;
	}

	private PieSeries<ChartData> createPieSeries() {
		final PieSeries<ChartData> pieSeries = new PieSeries<ChartData>();
		pieSeries.setAngleField(chartDataProperties.summaryDuration());
		pieSeries.setLabelConfig(createLabelConfig());
		pieSeries.setHighlighting(true);
		pieSeries.setPopOutMargin(5);
		pieSeries.setLegendValueProvider(chartDataProperties.displayName(),
				new LegendLabelProivider());
		return pieSeries;
	}

	@Override
	public void dispose() {
		if (loadingDataTask != null) {
			loadingDataTask.cancel();
			loadingDataTask = null;
		}
	}

	@Override
	public Widget getContentElement() {
		return content;
	}

	@Override
	public void initialize() {
		hideWidget(content, true);
		loadData();
		scheduleLoadingDataTask();
	}

	public void loadData() {
		userService.loadWigetData(model, new AsyncCallback<List<ChartData>>() {

			@Override
			public void onFailure(final Throwable caught) {
				AppUtils.showInfoMessage("Cannot load data for "
						+ model.getKey());
			}

			@Override
			public void onSuccess(final List<ChartData> chartData) {
				final VerticalLayoutData layoutData = new VerticalLayoutData(1,
						1);
				if (chartData == null) {
					if (chart.isAttached()) {
						content.remove(chart);
					}
					if (!noDataLabel.isAttached()) {
						content.add(noDataLabel, layoutData);
					}
				} else {
					if (noDataLabel.isAttached()) {
						content.remove(noDataLabel);
					}
					if (!chart.isAttached()) {
						content.add(chart, layoutData);
					}
					store.clear();
					store.addAll(chartData);
				}
				refresh();
				hideWidget(content, false);
			}
		});

	}

	@Override
	public void refresh() {
		updateChart();
	}

	private void scheduleLoadingDataTask() {
		loadingDataTask = new Timer() {

			@Override
			public void run() {
				loadData();
			}
		};
		loadingDataTask.scheduleRepeating(REFRESH_DELAY_IN_SEC
				* TimeConstants.MILLISECONDS_PER_SECOND);

	}

	private void updateChart() {
		chart.redrawChartForced();
		content.forceLayout();
	}
}
