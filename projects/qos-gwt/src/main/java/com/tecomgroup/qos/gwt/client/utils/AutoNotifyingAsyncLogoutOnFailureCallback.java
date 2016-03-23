/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import com.google.gwt.user.client.rpc.StatusCodeException;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;

import java.util.logging.Logger;

/**
 * Implementation of {@link com.gwtplatform.mvp.client.proxy.NotifyingAsyncCallback} where
 * {@link com.gwtplatform.mvp.client.proxy.AsyncCallStartEvent}, {@link com.gwtplatform.mvp.client.proxy.AsyncCallSucceedEvent},
 * {@link com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent} are fired without explicitly calling prepare() and
 * checkLoading() methods.
 *
 * <p>
 * Important! For correct usage <b>always</b> create new callback instance for
 * each request.
 * </p>
 *
 * {@link com.gwtplatform.mvp.client.proxy.AsyncCallStartEvent} fires right after callback is created.
 *
 * See {@link com.gwtplatform.mvp.client.proxy.NotifyingAsyncCallback} and {@link com.google.gwt.user.client.rpc.AsyncCallback}.
 *
 * @author sviyazov.a
 *
 */
public abstract class AutoNotifyingAsyncLogoutOnFailureCallback<T>
		extends
			AutoNotifyingAsyncCallback<T> {
	private static int SC_UNAUTHORIZED = 401;
	private static int SC_FORBIDDEN = 403;

	public static Logger LOGGER = Logger
			.getLogger(AutoNotifyingAsyncLogoutOnFailureCallback.class.getName());

	public AutoNotifyingAsyncLogoutOnFailureCallback() {
		this(null, false);
	}
	public AutoNotifyingAsyncLogoutOnFailureCallback(final String errorMessage,
													 final boolean showErrorDialog) {
		super(errorMessage, showErrorDialog);
	}

	@Override
	protected void failure(final Throwable caught) {
		super.failure(caught);
		checkAndPerformRedirect(caught);
	}

	private void checkAndPerformRedirect(final Throwable caught) {
		if(caught instanceof StatusCodeException) {
			int statusCode = ((StatusCodeException) caught).getStatusCode();
			if(statusCode == SC_UNAUTHORIZED || statusCode == SC_FORBIDDEN) {
				sendBeforeLogoutEvent();
			}
		}
	}

	private void sendBeforeLogoutEvent() {
		AppUtils.getEventBus().fireEvent(new BeforeLogoutEvent());
	}
}
