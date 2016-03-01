package com.tecomgroup.qos.gwt.client.event.chart;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.domain.MChartSeries;

/**
 * This event is sent when user clicks "Navigate to chart" button
 *
 * @author smyshlyaev.s
 */
public class NavigateToChartEvent
		extends
			GwtEvent<NavigateToChartEvent.NavigateToChartEventHandler> {

	public static interface NavigateToChartEventHandler extends EventHandler {
		void onNavigateToChart(NavigateToChartEvent event);
	}

	public static final Type<NavigateToChartEventHandler> TYPE = new Type<NavigateToChartEventHandler>();

	private final boolean isAutoscalingEnabled;

	private final boolean isThresholdsEnabled;

	private final boolean isAddedToDashboard;

	private final String chartName;

	private final List<MChartSeries> series;

	private final TimeInterval timeInterval;

	private final String lineType;

	public NavigateToChartEvent(final DashboardChartWidget chartWidget) {
		this(chartWidget.getChartName(), chartWidget.getSeries(), TimeInterval
				.get(chartWidget.getIntervalType()), chartWidget
				.isAutoscalingEnabled(), chartWidget.isThresholdsEnabled(),
				true, chartWidget.getLineType());
	}

	public NavigateToChartEvent(final String chartName,
			final List<MChartSeries> series, final TimeInterval timeInterval,
			final boolean isAddedToDashboard,
			final boolean isAutoscalingEnabled,
			final boolean isThresholdsEnabled,
			final String lineType) {
		this.isAutoscalingEnabled = isAutoscalingEnabled;
		this.isThresholdsEnabled = isThresholdsEnabled;
		this.isAddedToDashboard = isAddedToDashboard;
		this.chartName = chartName;
		this.series = series;
		this.timeInterval = timeInterval;
		this.lineType = lineType;
	}

	@Override
	protected void dispatch(final NavigateToChartEventHandler handler) {
		handler.onNavigateToChart(this);
	}

	@Override
	public Type<NavigateToChartEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the chartName
	 */
	public String getChartName() {
		return chartName;
	}

	/**
	 * @return the series
	 */
	public List<MChartSeries> getSeries() {
		return series;
	}

	/**
	 * @return the timeInterval
	 */
	public TimeInterval getTimeInterval() {
		return timeInterval;
	}

	/**
	 * @return the isAddedToDashboard
	 */
	public boolean isAddedToDashboard() {
		return isAddedToDashboard;
	}

	/**
	 * @return the autoscalingEnabled
	 */
	public boolean isAutoscalingEnabled() {
		return isAutoscalingEnabled;
	}

	/**
	 * @return the thresholdsEnabled
	 */
	public boolean isThresholdsEnabled() {
		return isThresholdsEnabled;
	}

	/**
	 * @return the lineType
	 */
	public String getLineType() {
		return lineType;
	}

}
