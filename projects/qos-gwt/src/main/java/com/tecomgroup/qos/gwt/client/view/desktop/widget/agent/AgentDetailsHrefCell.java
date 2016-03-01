/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.agent;

import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.data.shared.ListStore;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.model.AgentWrapper;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractHrefCell;

/**
 * @author sviyazov.a
 * 
 */
public class AgentDetailsHrefCell extends AbstractHrefCell<AgentWrapper> {

	public AgentDetailsHrefCell(final ListStore<AgentWrapper> store) {
		super(store);
	}

	@Override
	protected PlaceRequest createPlaceRequest(final AgentWrapper model) {
		final PlaceRequest.Builder requestBuilder = new PlaceRequest.Builder();
		requestBuilder.nameToken(QoSNameTokens.agentStatus).with(
				RequestParams.agentName, model.getAgent());

		return requestBuilder.build();
	}
}
