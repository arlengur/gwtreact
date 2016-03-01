/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.ChangeLocaleEvent.ChangeLocaleHandler;

/**
 * Событие на смену языка
 * 
 * @author novohatskiy.r
 * 
 */
public class ChangeLocaleEvent extends GwtEvent<ChangeLocaleHandler> {
	public static interface ChangeLocaleHandler extends EventHandler {
		void onEvent(ChangeLocaleEvent event);
	}
	public final static Type<ChangeLocaleHandler> TYPE = new Type<ChangeLocaleHandler>();

	private String locale;

	public ChangeLocaleEvent() {
		this(null);
	}

	/**
	 * @param newLocale
	 *            - locale, на которую необходимо сменить
	 */
	public ChangeLocaleEvent(final String newLocale) {
		this.locale = newLocale;
	}

	@Override
	protected void dispatch(final ChangeLocaleHandler handler) {
		handler.onEvent(this);
	}

	@Override
	public Type<ChangeLocaleHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(final String locale) {
		this.locale = locale;
	}

}
