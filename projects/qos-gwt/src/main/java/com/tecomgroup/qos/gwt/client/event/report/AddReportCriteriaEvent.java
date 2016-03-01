/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.report;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.gwt.client.event.report.AddReportCriteriaEvent.AddReportCriteriaEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class AddReportCriteriaEvent
		extends
			GwtEvent<AddReportCriteriaEventHandler> {

	public interface AddReportCriteriaEventHandler extends EventHandler {
		void onAddReportCriteriaEvent(AddReportCriteriaEvent event);
	}

	public final static Type<AddReportCriteriaEventHandler> TYPE = new Type<AddReportCriteriaEventHandler>();

	private final String agentKey;

	private final List<MAgentTask> tasks;

	public AddReportCriteriaEvent(final String agentKey,
			final List<MAgentTask> tasks) {
		this.agentKey = agentKey;
		this.tasks = tasks;
	}

	@Override
	protected void dispatch(final AddReportCriteriaEventHandler handler) {
		handler.onAddReportCriteriaEvent(this);
	}

	/**
	 * @return the agentKey
	 */
	public String getAgentKey() {
		return agentKey;
	}

	@Override
	public Type<AddReportCriteriaEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the tasks
	 */
	public List<MAgentTask> getTasks() {
		return tasks;
	}

}
