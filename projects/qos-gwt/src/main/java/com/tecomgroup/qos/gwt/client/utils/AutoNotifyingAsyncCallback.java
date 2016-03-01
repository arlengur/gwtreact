/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallStartEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallSucceedEvent;
import com.gwtplatform.mvp.client.proxy.NotifyingAsyncCallback;

/**
 * Implementation of {@link NotifyingAsyncCallback} where
 * {@link AsyncCallStartEvent}, {@link AsyncCallSucceedEvent},
 * {@link AsyncCallFailEvent} are fired without explicitly calling prepare() and
 * checkLoading() methods.
 * 
 * <p>
 * Important! For correct usage <b>always</b> create new callback instance for
 * each request.
 * </p>
 * 
 * {@link AsyncCallStartEvent} fires right after callback is created.
 * 
 * See {@link NotifyingAsyncCallback} and {@link AsyncCallback}.
 * 
 * @author sviyazov.a
 * 
 */
public abstract class AutoNotifyingAsyncCallback<T>
		extends
			NotifyingAsyncCallback<T> {

	public static Logger LOGGER = Logger
			.getLogger(AutoNotifyingAsyncCallback.class.getName());

	private final String errorMessage;

	private final boolean showErrorDialog;

	/**
	 * @param eventBus
	 */
	public AutoNotifyingAsyncCallback() {
		this(null, false);
	}
	public AutoNotifyingAsyncCallback(final String errorMessage,
			final boolean showErrorDialog) {
		super(AppUtils.getEventBus());

		this.errorMessage = errorMessage;
		this.showErrorDialog = showErrorDialog;

		prepare();
		checkLoading();
	}

	@Override
	protected void failure(final Throwable caught) {
		if (errorMessage == null) {
			LOGGER.log(Level.SEVERE, caught.getMessage());
		} else {
			LOGGER.log(Level.SEVERE, errorMessage, caught);
			if (showErrorDialog) {
				AppUtils.showErrorMessage(errorMessage, caught);
			}
		}

	}

}
