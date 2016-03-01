/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author abondin
 * 
 */
public class QoSPlaceManager extends PlaceManagerImpl {

	/**
	 * @param eventBus
	 * @param tokenFormatter
	 */

	@Inject
	public QoSPlaceManager(final EventBus eventBus,
			final TokenFormatter tokenFormatter) {
		super(eventBus, tokenFormatter);
	}

	@Override
	public void revealDefaultPlace() {
		revealPlace(
				new PlaceRequest.Builder().nameToken(
						AppUtils.getDefaultNavigationLink()).build(), false);
	}

	@Override
	public void revealErrorPlace(final String invalidHistoryToken) {
		revealErrorPage(invalidHistoryToken);
	}

	private void revealPage404(final String invalidHistoryToken) {
		revealPlace(new PlaceRequest.Builder().nameToken(QoSNameTokens.page404)
				.with(RequestParams.invalidHistoryToken, invalidHistoryToken)
				.build(), false);
	}

	private void revealErrorPage(final String historyToken) {
			revealPage404(historyToken);
	}
}
