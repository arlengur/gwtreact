/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.data.shared.ListStore;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * Abstract cell to create hyperlink.
 * 
 * @author kunilov.p
 * 
 */
public abstract class AbstractHrefCell<T> extends AbstractCell<String> {

	private final ListStore<T> store;

	public AbstractHrefCell(final ListStore<T> store) {
		super();
		this.store = store;
	}

	abstract protected PlaceRequest createPlaceRequest(T model);

	@Override
	public void render(final Context context, final String value,
			final SafeHtmlBuilder sb) {

		final T model = store.get(context.getIndex());
		final PlaceRequest request = createPlaceRequest(model);

		final String href = AppUtils.createHref(request);
		final Anchor anchor = new Anchor(href, value);
		sb.append(SafeHtmlUtils.fromTrustedString(anchor.getElement()
				.getString()));
	}
}
