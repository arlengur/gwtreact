/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.AppearanceUtils;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance.AlertsGridResources;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertsGridAppearance.AlertsGridStyle;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * A grid view config of the model having {@link PerceivedSeverity} property to
 * paint rows depended on severity.
 * 
 * @author kunilov.p
 * 
 */
public abstract class SeverityGridViewConfig<M> implements GridViewConfig<M> {

	protected final AppearanceFactory appearanceFactory;

	protected final AlertsGridStyle style;

	/**
	 * 
	 */
	public SeverityGridViewConfig(final AlertsGridResources resources,
			final AppearanceFactory appearanceFactory) {
		this.style = resources.css();
		this.appearanceFactory = appearanceFactory;
	}

	@Override
	public String getColStyle(final M model,
			final ValueProvider<? super M, ?> valueProvider,
			final int rowIndex, final int colIndex) {
		return colIndex == 0 ? (AppearanceUtils.getSeverityStyle(style,
				getPerceivedSeverity(model)) + " " + style
				.alertrHighlightedColumn()) : null;
	}

	public abstract PerceivedSeverity getPerceivedSeverity(final M model);

	@Override
	public String getRowStyle(final M model, final int rowIndex) {
		return model == null ? null : AppearanceUtils.getSeverityStyle(style,
				getPerceivedSeverity(model));
	}

}
