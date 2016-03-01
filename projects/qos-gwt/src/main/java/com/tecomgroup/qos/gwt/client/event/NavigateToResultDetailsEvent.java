/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import java.util.Date;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.gwt.client.event.NavigateToResultDetailsEvent.NavigateToResultDetailsEventHandler;

/**
 * Navigates to Result details page.
 * 
 * @author kunilov.p
 * 
 */
public class NavigateToResultDetailsEvent
		extends
			GwtEvent<NavigateToResultDetailsEventHandler>
		implements
			HasPostActionCallback {

	public static interface NavigateToResultDetailsEventHandler
			extends
				EventHandler {
		void onNavigateToResultDetails(NavigateToResultDetailsEvent event);
	}

	private final MSource source;
	private final ParameterIdentifier parameterIdentifier;
	private final Date startDateTime;
	private final Date endDateTime;
	private final String timeZone;
	private final TimeZoneType timeZoneType;
	private final String chartName;
	private final PostActionCallback callback;

	public final static Type<NavigateToResultDetailsEventHandler> TYPE = new Type<NavigateToResultDetailsEventHandler>();

	public NavigateToResultDetailsEvent(final MSource source,
			final ParameterIdentifier parameterIdentifier,
			final Date startDateTime, final Date endDateTime,
			final String timeZone, final TimeZoneType timeZoneType,
			final String chartName, final PostActionCallback callback) {
		super();
		this.source = source;
		this.parameterIdentifier = parameterIdentifier;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.timeZone = timeZone;
		this.timeZoneType = timeZoneType;
		this.chartName = chartName;
		this.callback = callback;
	}

	@Override
	protected void dispatch(final NavigateToResultDetailsEventHandler handler) {
		handler.onNavigateToResultDetails(this);
	}

	@Override
	public Type<NavigateToResultDetailsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	public PostActionCallback getCallback() {
		return callback;
	}

	/**
	 * @return the chartName
	 */
	public String getChartName() {
		return chartName;
	}

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @return the parameterIdentifier
	 */
	public ParameterIdentifier getParameterIdentifier() {
		return parameterIdentifier;
	}

	/**
	 * @return the source
	 */
	@Override
	public MSource getSource() {
		return source;
	}

	/**
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @return the timeZoneType
	 */
	public TimeZoneType getTimeZoneType() {
		return timeZoneType;
	}
}
