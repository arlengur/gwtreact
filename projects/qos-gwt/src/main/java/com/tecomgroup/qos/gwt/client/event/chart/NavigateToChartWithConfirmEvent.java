package com.tecomgroup.qos.gwt.client.event.chart;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.domain.MChartSeries;

import java.util.List;

/**
 * This event is sent when user clicks "Navigate to chart" button
 *
 * @author galin.a
 */
public class NavigateToChartWithConfirmEvent
        extends
            GwtEvent<NavigateToChartWithConfirmEvent.NavigateToChartWithConfirmEventHandler> {

    public static interface NavigateToChartWithConfirmEventHandler extends EventHandler {
        void onNavigateToChartWithConfirm(NavigateToChartWithConfirmEvent event);
    }

    public static final Type<NavigateToChartWithConfirmEventHandler> TYPE = new Type<NavigateToChartWithConfirmEventHandler>();

    private final boolean isAutoscalingEnabled;

    private final boolean isThresholdsEnabled;

    private final boolean isAddedToDashboard;

    private final String chartName;

    private final List<MChartSeries> series;

    private final TimeInterval timeInterval;

    private final String lineType;

    public NavigateToChartWithConfirmEvent(final DashboardChartWidget chartWidget) {
        this(chartWidget.getChartName(), chartWidget.getSeries(), TimeInterval
                        .get(chartWidget.getIntervalType()), chartWidget
                        .isAutoscalingEnabled(), chartWidget.isThresholdsEnabled(),
                true, chartWidget.getLineType());
    }

    public NavigateToChartWithConfirmEvent(final String chartName,
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
    protected void dispatch(final NavigateToChartWithConfirmEventHandler handler) {
        handler.onNavigateToChartWithConfirm(this);
    }

    @Override
    public Type<NavigateToChartWithConfirmEventHandler> getAssociatedType() {
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
