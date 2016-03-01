/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * A {@link GridViewConfig} to show disabled entities inheriting an interface
 * {@link Disabled} as grey rows.
 * 
 * @author kunilov.p
 * 
 */
public class DisabledGridViewConfig<M extends Disabled>
		implements
			GridViewConfig<M> {

	private final AppearanceFactory appearanceFactory;

	public DisabledGridViewConfig(final AppearanceFactory appearanceFactory) {
		this.appearanceFactory = appearanceFactory;
	}

	@Override
	public String getColStyle(final M model,
			final ValueProvider<? super M, ?> valueProvider,
			final int rowIndex, final int colIndex) {
		return null;
	}

	@Override
	public String getRowStyle(final M model, final int rowIndex) {
		String style = null;
		if (model.isDisabled()) {
			style = appearanceFactory.resources().css().textDisabledColor();
		}
		return style;
	}
}
