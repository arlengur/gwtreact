/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.data.shared.ListStore;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.presenter.AlertDetailsPresenter;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractHrefCell;

/**
 * Cell which has hyperlink to Alert Details page
 * 
 * @author ivlev.e
 */
public class AlertDetailsHrefCell extends AbstractHrefCell<MAlert> {

	public AlertDetailsHrefCell(final ListStore<MAlert> store) {
		super(store);
	}

	@Override
	protected PlaceRequest createPlaceRequest(final MAlert model) {
		return AlertDetailsPresenter.createAlertDetailsRequest(model);
	}
}
