/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent.GridGroupRemovedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class GridGroupRemovedEvent<M>
		extends
			GwtEvent<GridGroupRemovedEventHandler<M>> {

	public static interface GridGroupRemovedEventHandler<M>
			extends
				EventHandler {
		void onGridGroupRemovedEvent(GridGroupRemovedEvent<M> event);
	}

	private final List<M> items;

	public final static Type<GridGroupRemovedEventHandler<?>> TYPE = new Type<GridGroupRemovedEventHandler<?>>();

	public GridGroupRemovedEvent(final List<M> items) {
		this.items = items;
	}

	@Override
	protected void dispatch(final GridGroupRemovedEventHandler<M> handler) {
		handler.onGridGroupRemovedEvent(this);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Type<GridGroupRemovedEventHandler<M>> getAssociatedType() {
		return (Type) TYPE;
	}

	public List<M> getItems() {
		return items;
	}

}
