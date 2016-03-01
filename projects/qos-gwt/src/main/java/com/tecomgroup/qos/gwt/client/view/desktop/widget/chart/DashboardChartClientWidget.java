package com.tecomgroup.qos.gwt.client.view.desktop.widget.chart;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartSettings;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.AbstractWidgetTileContentElement;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author smyshlyaev.s
 */
public class DashboardChartClientWidget
        extends AbstractWidgetTileContentElement<DashboardChartWidget> {

    private final UserServiceAsync userService;

    private final SimplePanel content;
    private final QoSMessages messages;

    public DashboardChartClientWidget(final DashboardChartWidget model,
                                      final UserServiceAsync userService,
                                      final QoSMessages messages) {
        super(model);
        this.userService = userService;
        this.messages = messages;
        content = new SimplePanel();
        content.getElement().setId("chartWidget-" + model.getKey());
    }

    @Override
    public Widget getContentElement() {
        return content;
    }

    public void loadData() {
        userService.loadWigetData(model, new AsyncCallback<List<MChartSeries>>() {
            @Override
            public void onFailure(final Throwable caught) {
                AppUtils.showInfoMessage(messages.loadWidgetDataFail()
                        + model.getKey());
            }

            @Override
            public void onSuccess(final List<MChartSeries> result) {
                model.setSeries(result);
                createChart();
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void dispose() {
        ChartResultUtils.removeChart(model.getKey());
    }

    @Override
    public void initialize() {
        loadData();
    }

    @Override
    public void refresh() {
        dispose();
        createChart();
    }

    private void createChart() {
        final long now = System.currentTimeMillis();
        final long startDate = now - model.getIntervalType().getMillis();
        final ChartSettings settings = new ChartSettings.Builder()
                .autoscaling(model.isAutoscalingEnabled())
                .captions(false)
                .chartName(model.getChartName())
                .startDate(startDate)
                .series(model.getSeries())
                .endDate(now)
                .zoom(false)
                .divElementId(content.getElement().getId())
                .chartType(model.getChartType())
                .timeZoneOffset(DateUtils.getCurrentTimeZoneOffset())
                .lineType(model.getLineType().toLowerCase())
                .mouseTracking(false)
                .legend(model.isLegendEnabled())
                .chartName(model.getKey())
                .chartType(model.getChartType())
                .thresholds(model.isThresholdsEnabled())
                .build();
        ChartResultUtils.createChart(settings, content.getElement().getClientHeight(), "");
    }
}
