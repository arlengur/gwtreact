/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.inject.Inject;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.ActivateEvent;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.UpdateEvent;
import com.sencha.gxt.widget.core.client.event.UpdateEvent.UpdateHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.filter.EnumMapper;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 *
 * Grid with paging support, server-side ordering and filtering.
 *
 * @author abondin
 *
 */
public abstract class AbstractRemoteDataGridView<M, U extends AbstractRemoteDataGridWidgetPresenter<M, ?>>
		extends
		AbstractDataGridView<M, U> {

	public interface SelectedItemsActionHandler<M> {
		void onAction(final List<M> items, final String comment);
	}

	protected static final String ORDER_ALIAS_PREFIX = "alias_";

	/**
	 * Removes alias prefix for fields with alias.
	 *
	 * @param field
	 * @return
	 */
	private static String processOrderField(final String field) {
		String processedField = field;
		if (SimpleUtils.isNotNullAndNotEmpty(field)) {
			processedField = field.replaceFirst(ORDER_ALIAS_PREFIX, "");
		}
		return processedField;
	}

	protected ToolTipPagingToolBar pagingToolbar;

	protected final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<M>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<M>>(
			initializeLoaderProxy()) {
		@Override
		protected FilterPagingLoadConfig newLoadConfig() {
			return loadConfig;
		}
	};

	private boolean readyToReload = false;

	protected final FilterPagingLoadConfig loadConfig;

	private final int gridPageSize;

	/**
	 * Flag means neediness to refresh view after grid's data was loaded. Used
	 * together with lazy loading of grid's data.
	 */
	protected boolean refreshAfterGridDataReload = false;

	private static final int DEFAULT_GRID_PAGE_SIZE = 100;

	@Inject
	public AbstractRemoteDataGridView(final QoSMessages messages,
									  final AppearanceFactoryProvider appearanceFactoryProvider,
									  final DialogFactory dialogFactory,
									  final LocalizedFilterFactory filterFactory) {
		this(messages, appearanceFactoryProvider, dialogFactory, filterFactory,
				DEFAULT_GRID_PAGE_SIZE);
	}

	public AbstractRemoteDataGridView(final QoSMessages messages,
									  final AppearanceFactoryProvider appearanceFactoryProvider,
									  final DialogFactory dialogFactory,
									  final LocalizedFilterFactory filterFactory, final int gridPageSize) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		this.gridPageSize = gridPageSize;
		loadConfig = new FilterPagingLoadConfigBean();
		loadConfig.setLimit(gridPageSize);
	}

	@Override
	protected final void addFilters(final List<Filter<M, ?>> filters) {
		for (final Filter<M, ?> filter : filters) {
			filter.addUpdateHandler(new UpdateHandler() {

				@Override
				public void onUpdate(final UpdateEvent event) {
					// load first page when filter is used only by user
					setLoaderSettingsToLoadFirstPage();
				}
			});
			filter.addActivateHandler(new ActivateHandler<Filter<M, ?>>() {

				@Override
				public void onActivate(final ActivateEvent<Filter<M, ?>> event) {
					// load first page when filter is used only by user
					setLoaderSettingsToLoadFirstPage();
				}
			});
			filter.addDeactivateHandler(new DeactivateHandler<Filter<M, ?>>() {

				@Override
				public void onDeactivate(
						final DeactivateEvent<Filter<M, ?>> event) {
					// load first page when filter is used only by user
					setLoaderSettingsToLoadFirstPage();
				}
			});
			this.filters.addFilter(filter);
		}
	}

	@Override
	public final void addItem(final M item) {
		super.addItem(item);
		refreshFilters();
	}

	protected final void applyCriterionToFilters(
			final FilterPagingLoadConfig loadConfig, final Criterion criterion) {
		AppUtils.applyCriterionToFilters(loadConfig, filters, criterion);
	}

	/**
	 * Apply default filters, set default order, etc
	 */
	protected abstract void applyDefaultConfiguration();

	/**
	 *
	 * @param order
	 */
	protected final void applyOrder(final Order order, final boolean refresh) {
		final ColumnConfig<M, ?> config = grid.getColumnModel()
				.findColumnConfig(order.getPropertyName());
		loader.clearSortInfo();
		if (config != null) {
			loader.addSortInfo(0, new SortInfoBean(order.getPropertyName(),
					order.getType() == OrderType.ASC
							? SortDir.ASC
							: SortDir.DESC));
			if (refresh) {
				grid.getView().refresh(true);
			}
		}
	}

	@Override
	protected final void clearFilters(final boolean refresh) {
		super.clearFilters(refresh);
		if (refresh) {
			refreshFilters();
		}
	}

	protected final Criterion convertFiltersToCriterion(
			final FilterPagingLoadConfig loadConfig, final EnumMapper mapper) {
		return AppUtils.convertFiltersToCriterion(loadConfig, mapper, filters);
	}

	@Override
	protected final GridFilters<M> createGridFilters() {
		return new GridFilters<M>(loader);
	}

	public final Order getCurrentOrder() {
		final List<? extends SortInfo> list = loader.getSortInfo();
		if (list == null || list.isEmpty()) {
			return null;
		} else {
			final SortInfo info = list.iterator().next();
			final String sortField = processOrderField(info.getSortField());
			return info.getSortDir() == SortDir.ASC
					? Order.asc(sortField)
					: Order.desc(sortField);
		}
	}

	@Override
	@Inject
	public void initialize() {
		super.initialize();
		initializeLoader();
	}

	protected void initializeLoader() {
		loader.setRemoteSort(true);
		grid.setLoader(loader);
		loader.addLoadHandler(new LoadResultListStoreBinding<FilterPagingLoadConfig, M, PagingLoadResult<M>>(
				grid.getStore()));
		loader.addLoadHandler(new LoadHandler<FilterPagingLoadConfig, PagingLoadResult<M>>() {

			@Override
			public void onLoad(
					final LoadEvent<FilterPagingLoadConfig, PagingLoadResult<M>> event) {
				if (refreshAfterGridDataReload) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {

						@Override
						public void execute() {
							widget.forceLayout();
						}
					});
				}
			}
		});

		if (pagingToolbar == null) {
			pagingToolbar = new ToolTipPagingToolBar(
					appearanceFactory.toolBarAppearance(),
					appearanceFactory.pagingToolBarAppearance(), gridPageSize);
			pagingToolbar.addStyleName(ClientConstants.QOS_PAGING_TOOLBAR);
			widget.add(pagingToolbar, new VerticalLayoutData(-1, 40));
		}
		pagingToolbar.bind(loader);
	}

	/**
	 * It must use
	 * {@link AbstractRemoteDataGridWidgetPresenter#getConfigurableCriterion()}
	 * to load data from server.
	 *
	 * @return
	 */
	protected abstract RpcProxy<FilterPagingLoadConfig, PagingLoadResult<M>> initializeLoaderProxy();

	public final void loadFirstPage() {
		loadFirstPage(true);
	}

	private void loadFirstPage(final boolean force) {
		// Не загружать данные если ещё неизвестно какие
		if (readyToReload) {
			loader.load(0, gridPageSize);
		} else if (force) {
			loader.load(0, gridPageSize);
			readyToReload = true;
		}
	}

	@Override
	protected void onAfterFirstAttach() {
		applyDefaultConfiguration();
	}

	public void refreshFilters() {
		loadFirstPage(false);
	}

	public void reload(final boolean force) {
		// Не загружать данные если ещё неизвестно какие
		if (readyToReload) {
			loader.load();
		} else if (force) {
			loader.load(loadConfig.getOffset(), loadConfig.getLimit());
			readyToReload = true;
		}
	}

	@Override
	public final void removeItem(final M item) {
		super.removeItem(item);
		refreshFilters();
	}

	@Override
	public final void removeItems(final Set<String> keys) {
		super.removeItems(keys);
		refreshFilters();
	}

	private void setLoaderSettingsToLoadFirstPage() {
		loader.setOffset(0);
	}

	@Override
	public final void updateItem(final M item) {
		super.updateItem(item);
		refreshFilters();
	}
}
