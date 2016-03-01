/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataTreeGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.TreeGridFields;

/**
 * @author ivlev.e
 * 
 */
public abstract class AbstractLocalDataTreeGridView<M, U extends AbstractLocalDataTreeGridWidgetPresenter<M, ?>>
		extends
			ViewWithUiHandlers<U>
		implements
			AbstractLocalDataTreeGridWidgetPresenter.MyView<M, U> {

	protected TreeGrid<M> grid;

	protected TreeStore<M> store;

	protected GridAppearance gridAppearance;

	protected final QoSMessages messages;

	protected final AppearanceFactory appearanceFactory;

	protected CssFloatLayoutContainer toolbar;

	protected VerticalLayoutContainer widget;

	public AbstractLocalDataTreeGridView() {
		super();
		appearanceFactory = AppearanceFactoryProvider.instance();
		messages = AppUtils.getMessages();
	}

	private void addButtonsToToolbar(final List<IsWidget> buttons) {
		if (toolbar != null) {
			for (final IsWidget button : buttons) {
				toolbar.add(button);
			}
		}
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	/**
	 * Creates {@link GridViewConfig}. Overrides in subclasses to create custom
	 * config. Default value is null
	 * 
	 * @return
	 */
	protected GridViewConfig<M> createGirdViewConfig() {
		return null;
	}

	protected abstract GridAppearance createGridAppearance();

	protected ColumnConfig<M, ?> createPaddingColumn() {
		final ColumnConfig<M, String> paddingColumn = new ColumnConfig<M, String>(
				new ValueProviderWithPath<M, String>(
						TreeGridFields.PADDING.toString()) {

					@Override
					public String getValue(final M row) {
						return null;
					}
				});
		paddingColumn.setWidth(15);
		paddingColumn.setFixed(true);
		paddingColumn.setSortable(false);
		paddingColumn.setMenuDisabled(true);
		return paddingColumn;
	}

	protected abstract TreeStore<M> createStore();

	/**
	 * Method must be overridden if one needs to have toolbar with buttons
	 * 
	 * @return list of buttons
	 */
	protected List<IsWidget> createToolbarButtons() {
		return Collections.emptyList();
	}

	protected abstract List<ColumnConfig<M, ?>> getGridColumns();

	protected abstract String[] getGridStyles();

	/**
	 * Returns index of column in the {@link List} of {@link ColumnConfig} which
	 * will be a tree (grouping) column
	 * 
	 * @return
	 */
	protected abstract int getTreeColumnIndex();

	@Override
	public void initialize() {
		initializeTreeGrid();
		setupTreeGridView(null);
		initializeWidget();
	}

	private void initializeToolbar() {
		toolbar = new CssFloatLayoutContainer();
		toolbar.getElement().getStyle().setLineHeight(32, Unit.PX);
	}

	private void initializeTreeGrid() {
		store = createStore();
		gridAppearance = createGridAppearance();
		final List<ColumnConfig<M, ?>> columns = getGridColumns();

		grid = new TreeGrid<M>(store, new ColumnModel<M>(columns),
				columns.get(getTreeColumnIndex()), gridAppearance);
		for (final String style : getGridStyles()) {
			grid.addStyleName(style);
		}
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setHideHeaders(true);
		grid.getTreeStore().setAutoCommit(true);
		grid.getStyle().setNodeOpenIcon(
				appearanceFactory.resources().transparent1x1());
		grid.getStyle().setNodeCloseIcon(
				appearanceFactory.resources().transparent1x1());
		grid.getStyle().setJointOpenIcon(
				appearanceFactory.resources().treeOpen());
		grid.getStyle().setJointCloseIcon(
				appearanceFactory.resources().treeClose());

		final GridViewConfig<M> gridViewConfig = createGirdViewConfig();
		if (gridViewConfig != null) {
			grid.getView().setViewConfig(gridViewConfig);
		}
	}

	protected void initializeWidget() {
		widget = new VerticalLayoutContainer();
		widget.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		widget.setBorders(false);
		final List<IsWidget> buttons = createToolbarButtons();
		if (!buttons.isEmpty()) {
			initializeToolbar();
			addButtonsToToolbar(buttons);
			widget.add(toolbar);
		}
		widget.add(grid, new VerticalLayoutData(1, 1));
	}

	/**
	 * Adds custom view to grid and setups them. If param view is null default
	 * view will be used
	 * 
	 * @param view
	 */
	protected final void setupTreeGridView(final TreeGridView<M> view) {
		if (view != null) {
			grid.setView(view);
		}
		grid.getView().setStripeRows(true);
		grid.getView().setAutoFill(true);
	}

}
