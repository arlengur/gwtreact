/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.tecomgroup.qos.gwt.client.event.ClearContentOnMainPageEvent.ClearContentInMainPagePresenterEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class ClearContentOnMainPageEvent
		extends
			GwtEvent<ClearContentInMainPagePresenterEventHandler> {

	public static interface ClearContentInMainPagePresenterEventHandler
			extends
				EventHandler {
		void onClearContentOnMainPage(ClearContentOnMainPageEvent event);
	}
	public final static Type<ClearContentInMainPagePresenterEventHandler> TYPE = new Type<ClearContentInMainPagePresenterEventHandler>();

	private final Type<RevealContentHandler<?>> slot;

	public ClearContentOnMainPageEvent(final Type<RevealContentHandler<?>> slot) {
		this.slot = slot;
	}

	@Override
	protected void dispatch(
			final ClearContentInMainPagePresenterEventHandler handler) {
		handler.onClearContentOnMainPage(this);
	}

	@Override
	public Type<ClearContentInMainPagePresenterEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Type<RevealContentHandler<?>> getSlot() {
		return slot;
	}
}
