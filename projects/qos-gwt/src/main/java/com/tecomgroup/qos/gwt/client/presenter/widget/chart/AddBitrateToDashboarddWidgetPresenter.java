/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.presenter.widget.chart;

import com.google.inject.Inject;
import com.tecomgroup.qos.dashboard.DashboardBitrateWidget;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddNamedWidgetToDashboardWidgetPresenter;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.gwt.client.event.dashboard.AddWidgetToDashboardEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author sviyazov.a
 */
public class AddBitrateToDashboarddWidgetPresenter extends
        AddNamedWidgetToDashboardWidgetPresenter {

    public static interface MyView
            extends
            AddNamedWidgetToDashboardWidgetPresenter.MyView {
        void setTitle(String taskName);
    }

    private String taskKey;

    @Inject
    public AddBitrateToDashboarddWidgetPresenter(final EventBus eventBus,
                                                  final MyView view, final QoSMessages messages) {
        super(eventBus, view, messages);
    }

    public void actionCreateWidget(final int updateInterval, final int capacity) {
        assert (taskKey != null);

        final DashboardBitrateWidget widget = new DashboardBitrateWidget();
        fillWidgetTitle(widget);
        fillWidgetSize(widget);
        widget.setTaskKey(taskKey);
        widget.setUpdateInterval(updateInterval);
        widget.setCapacity(capacity);
        getEventBus().fireEvent(new AddWidgetToDashboardEvent(widget));
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public void setTitle(String taskName) {
        ((MyView)getView()).setTitle(taskName);
    }
}
