/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ListMenu;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterHeaderContextMenuHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridView.SelectedItemsActionHandler;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author sviyazov.a
 * 
 */
public abstract class AbstractDataGridView<M, U extends AbstractGridWidgetPresenter<M, ?>>
		extends
			ViewWithUiHandlers<U>
		implements
			AbstractGridWidgetPresenter.MyView<M, U> {

	protected Grid<M> grid;

	protected ListStore<M> store;

	protected VerticalLayoutContainer widget;

	protected final QoSMessages messages;

	protected final AppearanceFactory appearanceFactory;

	protected CssFloatLayoutContainer toolbar;

	protected final LocalizedFilterFactory filterFactory;

	protected GridAppearance gridAppearance;

	protected final DialogFactory dialogFactory;

	protected int CONFIRMATION_DIALOG_WIDTH = DialogFactory.DEFAULT_CONFIRMATION_DIALOG_WIDTH;

	/**
	 * Important: filters should be added using {{{@link #addFilters(List)}
	 * method, so all necessary handlers are set.
	 */
	protected GridFilters<M> filters;

	public AbstractDataGridView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.dialogFactory = dialogFactory;
		this.filterFactory = filterFactory;
		toolbar = new CssFloatLayoutContainer();
	}

	/**
	 * 
	 * @param toolbar
	 * @return false if no toolbar needed
	 */
	protected abstract boolean addButtonsToToolbar();

	abstract protected void addFilters(final List<Filter<M, ?>> filters);

	@Override
	public void addItem(final M item) {
		grid.getStore().add(item);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	protected void clearFilters(final boolean refresh) {
		clearListFilters();
		filters.clearFilters();
	}

	/**
	 * Must be called before {@link GridFilters#clearFilters()}, otherwise
	 * {@link GridFilters#getFilterData()} returns empty list.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void clearListFilters() {
		for (final Filter<M, ?> filter : filters.getFilterData()) {
			if (filter instanceof ListFilter) {
				((ListMenu) filter.getMenu()).setSelected(Collections
						.emptyList());
			}
		}
	}

	protected Image createClickableToolBarButton(final ImageResource icon,
			final String tooltip, final SelectedItemsActionHandler<M> handler) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (tooltip != null) {
			button.setTitle(tooltip);
		}
		if (handler != null) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					final List<M> items = grid.getSelectionModel()
							.getSelectedItems();
					if (SimpleUtils.isNotNullAndNotEmpty(items)) {
						handler.onAction(items, null);
					}
				}
			});
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		return button;
	}

	protected abstract List<Filter<M, ?>> createFilters();

	protected abstract GridAppearance createGridAppearance();

	abstract protected GridFilters<M> createGridFilters();

	/**
	 * @return
	 */
	protected Image createSeparator() {
		return StyleUtils.createSeparator(new Margins(6, 7, 3, 7));
	}

	protected abstract ListStore<M> createStore();

	protected Image createToolBarButton(final ImageResource icon,
			final String title, final SelectedItemsActionHandler<M> handler) {
		return createToolBarButton(icon, title, null, CommentMode.OPTIONAL,
				handler);
	}

	protected Image createToolBarButton(final ImageResource icon,
			final String title, final String message,
			final CommentMode commentMode,
			final SelectedItemsActionHandler<M> handler) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (title != null) {
			button.setTitle(title);
		}
		if (handler != null) {
			button.addClickHandler(getToolBarButtonClickHandler(handler, title,
					message, commentMode, grid));
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		return button;
	}

	protected Image createToolBarButtonWithWarningConfirmation(
			final ImageResource icon, final String title, final String message,
			final SelectedItemsActionHandler<M> handler) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (title != null) {
			button.setTitle(title);
		}
		if (handler != null) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					final List<M> items = grid.getSelectionModel()
							.getSelectedItems();
					if (SimpleUtils.isNotNullAndNotEmpty(items)) {
						dialogFactory.createWarningDialog(title, message,
								new ConfirmationHandler() {

									@Override
									public void onCancel() {
										// do nothing
									}

									@Override
									public void onConfirm(final String comment) {
										handler.onAction(items, comment);
									}
								}).show();
					}
				}
			});
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		return button;
	}

	@Override
	public Map<String, String> getColumnNames() {
		final Map<String, String> names = new LinkedHashMap<String, String>();
		for (final ColumnConfig<M, ?> column : grid.getColumnModel()
				.getColumns()) {
			if (SimpleUtils.isNotNullAndNotEmpty(column.getPath())
					&& column.getHeader() != null) {
				names.put(column.getPath(), column.getHeader().asString());
			}
		}
		return names;
	}

	protected abstract List<ColumnConfig<M, ?>> getGridColumns();

	/**
	 * Get custom grid styles
	 * 
	 * @see ClientConstants#QOS_GRID_STANDARD_STYLE
	 * @see ClientConstants#QOS_GRID_ALERTS_STYLE
	 * @return
	 */
	protected abstract String[] getGridStyles();

	@Override
	public String[] getHiddenColumns() {
		final List<String> hiddenColumns = new ArrayList<String>();
		for (final ColumnConfig<M, ?> config : grid.getColumnModel()
				.getColumns()) {
			if (config.isHidden()) {
				hiddenColumns.add(config.getPath());
			}
		}
		return hiddenColumns.toArray(new String[0]);
	}

	/**
	 * @return highlighted column path to sort by it.
	 */
	protected String getHighlightedColumnPath() {
		return "";
	}

	public ListStore<M> getStore() {
		return store;
	}

	public <T> ClickHandler getToolBarButtonClickHandler(
			final SelectedItemsActionHandler<T> handler, final String title,
			final String message, final CommentMode commentMode,
			final Grid<T> grid) {
		return new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final List<T> items = grid.getSelectionModel()
						.getSelectedItems();
				if (SimpleUtils.isNotNullAndNotEmpty(items)) {
					dialogFactory.createConfirmationDialog(
							new ConfirmationHandler() {
								@Override
								public void onCancel() {
									// Do nothing
								}

								@Override
								public void onConfirm(final String comment) {
									handler.onAction(items, comment);
								}
							}, title, message, commentMode,
							CONFIRMATION_DIALOG_WIDTH).show();
				}
			}
		};
	}

	/**
	 * @param hiddenColumns
	 */
	protected void hideColumns(final String[] hiddenColumns,
			final boolean refresh) {
		if (hiddenColumns != null) {
			final List<String> hidenColumnsList = Arrays.asList(hiddenColumns);
			grid.getColumnModel().getColumns();
			final ColumnModel<M> columnModel = grid.getColumnModel();
			for (int index = 0; index < columnModel.getColumnCount(); index++) {
				final String path = columnModel.getColumn(index).getPath();
				columnModel.setHidden(index, hidenColumnsList.contains(path));
			}
		}
		if (refresh) {
			grid.getView().refresh(true);
		}
	}

	@Inject
	public void initialize() {
		initializeGrid();
		initializeWidget();
		initializeFilters();

		if (grid.getView() instanceof CustomGridView) {
			((CustomGridView<M>) grid.getView()).setFilters(filters);
		}
	}

	private void initializeFilters() {
		filters = createGridFilters();
		filters.initPlugin(grid);
		filters.setUpdateBuffer(TimeConstants.FILTER_UPDATE_DELAY);

		addFilters(createFilters());
		/*
		 * This handler provides correct displaying of 'Filters' menu item
		 */
		grid.addHeaderContextMenuHandler(new LocalizedFilterHeaderContextMenuHandler(
				messages));
	}

	protected void initializeGrid() {
		final IdentityValueProvider<M> identity = new IdentityValueProvider<M>();
		final CheckBoxSelectionModel<M> selectionModel = new CustomCheckBoxSelectionModel<M>(
				identity, appearanceFactory.<M> checkBoxColumnAppearance());
		selectionModel.setSelectionMode(SelectionMode.MULTI);

		store = createStore();

		gridAppearance = createGridAppearance();

		final List<ColumnConfig<M, ?>> columns = getGridColumns();

		final ColumnConfig<M, String> highlightedColumn = new ColumnConfig<M, String>(
				new ValueProviderWithPath<M, String>(getHighlightedColumnPath()) {

					@Override
					public String getValue(final M model) {
						return "";
					}
				});
		highlightedColumn.setFixed(true);
		highlightedColumn.setSortable(isHighlightedColumnSortable());
		highlightedColumn.setRowHeader(false);
		highlightedColumn.setMenuDisabled(true);
		highlightedColumn.setWidth(15);

		columns.add(0, highlightedColumn);
		columns.add(1, selectionModel.getColumn());

		grid = new Grid<M>(store, new ColumnModel<M>(columns),
				new CustomGridView<M>(gridAppearance,
						appearanceFactory.columnHeaderAppearance())) {

			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				AbstractDataGridView.this.onAfterFirstAttach();
			}
		};

		for (final String style : getGridStyles()) {
			grid.addStyleName(style);
		}

		grid.getView().setStripeRows(false);
		grid.getView().setAutoFill(true);
		grid.getView().setColumnLines(true);
		grid.getStore().setAutoCommit(true);
		grid.setSelectionModel(selectionModel);
	}

	protected void initializeWidget() {
		widget = new VerticalLayoutContainer();
		widget.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		widget.setBorders(false);
		if (addButtonsToToolbar()) {
			widget.add(toolbar);
		}
		widget.add(grid, new VerticalLayoutData(1, 1));
	}

	/**
	 * @return if highlighted column is sortable or not.
	 */
	protected boolean isHighlightedColumnSortable() {
		return false;
	}

	/**
	 * Provides future class implementations with a hook for after first attach
	 * event. If not overridden, does nothing.
	 */
	protected void onAfterFirstAttach() {
		// do nothing by default
	}

	@Override
	public void removeItem(final M item) {
		grid.getStore().remove(item);
	}

	@Override
	public void removeItems(final Set<String> keys) {
		for (final String key : keys) {
			final M item = grid.getStore().findModelWithKey(key);
			if (item != null) {
				grid.getStore().remove(item);
			}
		}
	}

	@Override
	public void updateItem(final M item) {
		grid.getStore().update(item);

	}

}
