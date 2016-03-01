/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.view.desktop.widget.chart;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.dashboard.DashboardBitrateWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.AbstractWidgetTileContentElement;

public class DashboardBitrateClientWidget extends AbstractWidgetTileContentElement<DashboardBitrateWidget> {

    private final SimplePanel content;

    private final String id;

    public DashboardBitrateClientWidget(final DashboardBitrateWidget model) {
        super(model);
        id = "id" + model.getKey().hashCode();
        content = new SimplePanel();
        content.getElement().setId(id);
    }

    @Override
    public void initialize() {
        // This is a crutch to make dashboard layout work. Widget is made visible again in #drawPiechart.
        hideWidget(content, true);
        updateChart();
    }

    @Override
    public void dispose() {
    }

    @Override
    public Widget getContentElement() {
        return content;
    }

    @Override
    public void refresh() {
        updateChart();
    }

    private void updateChart() {
        drawPiechart(id, (double)model.getCapacity(), model.getTaskKey());
    }

    private native void drawPiechart(String id, Double capacity, String taskKey) /*-{
        $wnd.$.ajax({
            url: 'rest/widgets/bitrate?taskKey='+taskKey,
            dataType: 'json',
            success: function(data) {
                console.log(data);
                var content = $wnd.$('#'+id);
                content.css('visibility', 'visible');
                var widgetHeight = content.parent().height() - 10;
                var widgetWidth = content.parent().width() - 10;
                content.height(widgetHeight);
                content.width(widgetWidth);
                $wnd.initPieChart(id, data, capacity);
            }
        });
    }-*/;

}
