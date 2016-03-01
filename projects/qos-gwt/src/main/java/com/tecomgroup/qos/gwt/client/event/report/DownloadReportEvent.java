/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.report;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.report.DownloadReportEvent.DownloadReportEventHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.report.ReportsGridWidgetView;

/**
 * Event is fired in {@link ReportsGridWidgetView}. The cause of using this
 * event is weak commitment between view which fires event and event's handler.
 * 
 * @author ivlev.e
 */
public class DownloadReportEvent extends GwtEvent<DownloadReportEventHandler> {

	public interface DownloadReportEventHandler extends EventHandler {
		void onDownloadReportEvent(DownloadReportEvent event);
	}

	public final static Type<DownloadReportEventHandler> TYPE = new Type<DownloadReportEventHandler>();

	public DownloadReportEvent() {
		// TODO add needed parameters from grid: checked severity, filters etc.
	}

	@Override
	protected void dispatch(final DownloadReportEventHandler handler) {
		handler.onDownloadReportEvent(this);
	}

	@Override
	public Type<DownloadReportEventHandler> getAssociatedType() {
		return TYPE;
	}

}
