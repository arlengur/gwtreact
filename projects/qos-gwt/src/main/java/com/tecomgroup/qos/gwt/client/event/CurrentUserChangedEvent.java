/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.event.CurrentUserChangedEvent.CurrentUserChangedEventHandler;
import com.tecomgroup.qos.gwt.client.presenter.MainPagePresenter;

/**
 * 
 * Событие посылается после загрузки главного презентера
 * 
 * @see MainPagePresenter
 * 
 * @author abondin
 * 
 */
public class CurrentUserChangedEvent
		extends
			GwtEvent<CurrentUserChangedEventHandler> {
	public static interface CurrentUserChangedEventHandler extends EventHandler {
		void onEvent(CurrentUserChangedEvent event);
	}
	public final static Type<CurrentUserChangedEventHandler> TYPE = new Type<CurrentUserChangedEventHandler>();

	private MUser user;

	/**
	 * 
	 */
	CurrentUserChangedEvent() {
	}

	public CurrentUserChangedEvent(final MUser currentUser) {
		this.user = currentUser;
	}

	@Override
	protected void dispatch(final CurrentUserChangedEventHandler handler) {
		handler.onEvent(this);
	}

	@Override
	public Type<CurrentUserChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the currentUser
	 */
	public MUser getUser() {
		return user;
	}
}
