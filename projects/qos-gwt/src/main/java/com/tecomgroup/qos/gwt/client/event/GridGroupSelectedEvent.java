/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupSelectedEvent.GridGroupSelectedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class GridGroupSelectedEvent<M>
		extends
			GwtEvent<GridGroupSelectedEventHandler<M>> {

	public static interface GridGroupSelectedEventHandler<M>
			extends
				EventHandler {
		void onGroupSelected(GridGroupSelectedEvent<M> event);
	}

	public final static Type<GridGroupSelectedEventHandler<?>> TYPE = new Type<GridGroupSelectedEventHandler<?>>();

	@Override
	protected void dispatch(final GridGroupSelectedEventHandler<M> handler) {
		handler.onGroupSelected(this);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Type<GridGroupSelectedEventHandler<M>> getAssociatedType() {
		return (Type) TYPE;
	}

}
