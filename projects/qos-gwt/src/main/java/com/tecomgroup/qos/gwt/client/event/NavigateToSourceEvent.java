/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.gwt.client.event.NavigateToSourceEvent.NavigateToSourceEventHandler;

/**
 * Navigates UI to specific Source page for given {@link MSource}
 * 
 * @author novohatskiy.r
 * 
 */
public class NavigateToSourceEvent
		extends
			GwtEvent<NavigateToSourceEventHandler>
		implements
			HasPostActionCallback {

	public static interface NavigateToSourceEventHandler extends EventHandler {
		void onNavigateToSource(NavigateToSourceEvent event);
	}
	public final static Type<NavigateToSourceEventHandler> TYPE = new Type<NavigateToSourceEventHandler>();

	private final MSource source;
	private final PostActionCallback callback;

	public NavigateToSourceEvent(final MSource source,
			final PostActionCallback callback) {
		this.source = source;
		this.callback = callback;
	}

	@Override
	protected void dispatch(final NavigateToSourceEventHandler handler) {
		handler.onNavigateToSource(this);
	}

	@Override
	public Type<NavigateToSourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	public PostActionCallback getCallback() {
		return callback;
	}

	/**
	 * @return the source
	 */
	@Override
	public MSource getSource() {
		return source;
	}
}