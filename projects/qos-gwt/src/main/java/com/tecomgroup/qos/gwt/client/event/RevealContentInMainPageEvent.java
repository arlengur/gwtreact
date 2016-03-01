/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.tecomgroup.qos.gwt.client.event.RevealContentInMainPageEvent.RevealContentInMainPagePresenterEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class RevealContentInMainPageEvent
		extends
			GwtEvent<RevealContentInMainPagePresenterEventHandler> {

	public static interface RevealContentInMainPagePresenterEventHandler
			extends
				EventHandler {
		void onRevealInMainPage(RevealContentInMainPageEvent event);
	}
	public final static Type<RevealContentInMainPagePresenterEventHandler> TYPE = new Type<RevealContentInMainPagePresenterEventHandler>();

	private final Type<RevealContentHandler<?>> slot;

	private final PresenterWidget<?> content;

	public RevealContentInMainPageEvent(
			final Type<RevealContentHandler<?>> slot,
			final PresenterWidget<?> content) {
		this.slot = slot;
		this.content = content;
	}

	@Override
	protected void dispatch(
			final RevealContentInMainPagePresenterEventHandler handler) {
		handler.onRevealInMainPage(this);
	}

	@Override
	public Type<RevealContentInMainPagePresenterEventHandler> getAssociatedType() {
		return TYPE;
	}

	public PresenterWidget<?> getContent() {
		return content;
	}

	public Type<RevealContentHandler<?>> getSlot() {
		return slot;
	}
}
