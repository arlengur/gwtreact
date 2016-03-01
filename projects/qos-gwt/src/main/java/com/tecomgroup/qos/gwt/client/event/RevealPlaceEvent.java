/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.RevealPlaceEvent.RevealPlaceEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class RevealPlaceEvent extends GwtEvent<RevealPlaceEventHandler> {

	public static interface RevealPlaceEventHandler extends EventHandler {
		void onRevealPlaceRequested(RevealPlaceEvent event);
	}

	private final String placeToken;

	public final static Type<RevealPlaceEventHandler> TYPE = new Type<RevealPlaceEventHandler>();

	public RevealPlaceEvent(final String placeToken) {
		this.placeToken = placeToken;
	}

	@Override
	protected void dispatch(final RevealPlaceEventHandler handler) {
		handler.onRevealPlaceRequested(this);
	}

	@Override
	public Type<RevealPlaceEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getPlaceToken() {
		return placeToken;
	}

}
