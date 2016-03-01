/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

/**
 * @author ivlev.e
 * 
 */
public class CustomCheckBoxSelectionModel<M> extends CheckBoxSelectionModel<M> {

	private final CheckBoxColumnAppearance<M> appearance;

	public CustomCheckBoxSelectionModel(
			final IdentityValueProvider<M> valueProvider,
			final CheckBoxColumnAppearance<M> appearance) {
		super(valueProvider);
		this.appearance = appearance;
		config = newColumnConfig(valueProvider);
		config.setColumnClassSuffix("checker");
		/*
		 * При задании большей (> 18) ширины появляются артефакты при
		 * отображении чекбокса.
		 */
		config.setWidth(16);
		config.setSortable(false);
		config.setResizable(false);
		config.setFixed(true);
		config.setMenuDisabled(true);

		config.setCell(new AbstractCell<M>() {
			@Override
			public void render(final Context context, final M value,
					final SafeHtmlBuilder sb) {
				CustomCheckBoxSelectionModel.this.appearance.renderCheckBox(
						context, value, sb);
			}
		});
	}

	@Override
	protected void handleHeaderClick(final HeaderClickEvent event) {
		final ColumnConfig<M, ?> c = grid.getColumnModel().getColumn(
				event.getColumnIndex());
		if (c == config) {
			final XElement hd = event.getEvent().getEventTarget()
					.<Element> cast().getParentElement().cast();
			final boolean isChecked = appearance.isHeaderChecked(hd);
			if (isChecked) {
				setChecked(false);
				deselectAll();
			} else {
				setChecked(true);
				selectAll();
			}
		}
	}
}
