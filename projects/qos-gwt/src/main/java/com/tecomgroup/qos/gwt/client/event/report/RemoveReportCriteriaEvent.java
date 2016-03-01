/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.report;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.report.RemoveReportCriteriaEvent.RemoveReportCriteriaEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class RemoveReportCriteriaEvent
		extends
			GwtEvent<RemoveReportCriteriaEventHandler> {

	public interface RemoveReportCriteriaEventHandler extends EventHandler {
		void onRemoveReportCriteriaEvent(RemoveReportCriteriaEvent event);
	}

	public final static Type<RemoveReportCriteriaEventHandler> TYPE = new Type<RemoveReportCriteriaEventHandler>();

	private final List<String> modelKeys;

	public RemoveReportCriteriaEvent(final List<String> modelKeys) {
		this.modelKeys = modelKeys;
	}

	@Override
	protected void dispatch(final RemoveReportCriteriaEventHandler handler) {
		handler.onRemoveReportCriteriaEvent(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RemoveReportCriteriaEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the modelKeys
	 */
	public List<String> getModelKeys() {
		return modelKeys;
	}

}
