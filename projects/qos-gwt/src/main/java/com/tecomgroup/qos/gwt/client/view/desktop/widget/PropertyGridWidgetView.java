/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.PropertyGridWidgetPresenter.Property;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertProbableCausePropertyEditor;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSeverityMarkedCell;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.PropertyGridMultiTypedCell;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PropertyGridPropertyAccess;

/**
 * Represents a list of properties. You can implement your own representation
 * for any type of property, see {@link PropertyGridMultiTypedCell}
 * 
 * @see PropertyGridWidgetPresenter
 * @see PropertyGridWidgetPresenter.Property
 * 
 * @author novohatskiy.r
 * 
 */
public class PropertyGridWidgetView
		extends
			ViewWithUiHandlers<PropertyGridWidgetPresenter>
		implements
			PropertyGridWidgetPresenter.MyView {

	private final AppearanceFactory appearanceFactory;
	private final QoSMessages messages;

	protected PropertyGridPropertyAccess props;
	private Grid<Property> grid;
	private ListStore<Property> store;

	@Inject
	public PropertyGridWidgetView(final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		this.appearanceFactory = appearanceFactory;
		this.messages = messages;
		props = GWT.create(PropertyGridPropertyAccess.class);
	}

	@Override
	public void addProperty(final Property property, final int index) {
		store.add(index, property);
	}

	@Override
	public Widget asWidget() {
		final ColumnModel<Property> cm = new ColumnModel<Property>(
				getGridColumns());
		store = new ListStore<Property>(props.key());
		grid = new Grid<Property>(store, cm, new CustomGridView<Property>(
				appearanceFactory.gridPropertyAppearance(),
				appearanceFactory.columnHeaderAppearance()));
		grid.getView().setStripeRows(true);
		grid.setHideHeaders(true);
		grid.getView().setAutoFill(true);
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.addStyleName(ClientConstants.QOS_GRID_PROPERTY_STYLE);
		return grid;
	}

	protected PropertyGridMultiTypedCell createMultiTypedCell() {
		final PropertyGridMultiTypedCell multiTypedCell = new PropertyGridMultiTypedCell();
		multiTypedCell.addCell(
				PerceivedSeverity.class,
				new AlertSeverityMarkedCell(messages, appearanceFactory
						.alertSeverityMarkerAppearance()));
		multiTypedCell.addCell(ProbableCause.class,
				new AlertProbableCausePropertyEditor.Cell(messages));
		return multiTypedCell;
	}

	protected List<ColumnConfig<Property, ?>> getGridColumns() {
		final ColumnConfig<Property, String> nameColumn = new ColumnConfig<Property, String>(
				props.name(), 50);
		final ColumnConfig<Property, Object> valueColumn = new ColumnConfig<Property, Object>(
				props.value(), 50);
		valueColumn.setCell(createMultiTypedCell());
		final List<ColumnConfig<Property, ?>> columns = new ArrayList<ColumnConfig<Property, ?>>();
		columns.add(nameColumn);
		columns.add(valueColumn);
		return columns;
	}

	@Override
	public void setProperties(final List<Property> properties) {
		store.clear();
		store.addAll(properties);
	}
}
