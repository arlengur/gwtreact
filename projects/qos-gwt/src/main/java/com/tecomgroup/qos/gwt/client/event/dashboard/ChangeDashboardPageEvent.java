/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.dashboard;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.ChangeDashboardPageEvent.ChangeDashboardPageEventHandler;

/**
 * @author kshnyakin.m
 * 
 */
public class ChangeDashboardPageEvent
		extends
			GwtEvent<ChangeDashboardPageEventHandler> {
	public static interface ChangeDashboardPageEventHandler
			extends
				EventHandler {
		void onChangePage(ChangeDashboardPageEvent event);
	}

	public final static Type<ChangeDashboardPageEventHandler> TYPE = new Type<ChangeDashboardPageEventHandler>();

	private final int pageNumber;

	public ChangeDashboardPageEvent(final int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	protected void dispatch(final ChangeDashboardPageEventHandler handler) {
		handler.onChangePage(this);
	}

	@Override
	public Type<ChangeDashboardPageEventHandler> getAssociatedType() {
		return TYPE;
	}

	public int getPageNumber() {
		return pageNumber;
	}
}
