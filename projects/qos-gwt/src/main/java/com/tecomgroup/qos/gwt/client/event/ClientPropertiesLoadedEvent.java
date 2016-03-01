/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.ClientPropertiesLoadedEvent.ClientPropertiesLoadedEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class ClientPropertiesLoadedEvent
		extends
			GwtEvent<ClientPropertiesLoadedEventHandler> {

	public static interface ClientPropertiesLoadedEventHandler
			extends
				EventHandler {
		void onClientPropertiesLoaded(ClientPropertiesLoadedEvent event);
	}

	public final static Type<ClientPropertiesLoadedEventHandler> TYPE = new Type<ClientPropertiesLoadedEventHandler>();

	@Override
	protected void dispatch(final ClientPropertiesLoadedEventHandler handler) {
		handler.onClientPropertiesLoaded(this);
	}

	@Override
	public Type<ClientPropertiesLoadedEventHandler> getAssociatedType() {
		return TYPE;
	}

}
