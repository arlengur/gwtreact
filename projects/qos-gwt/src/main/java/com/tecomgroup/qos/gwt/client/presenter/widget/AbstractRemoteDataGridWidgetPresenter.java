/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * 
 * @author kunilov.p
 * 
 */
public abstract class AbstractRemoteDataGridWidgetPresenter<M, V extends AbstractRemoteDataGridWidgetPresenter.MyView<M, ?>>
		extends
			AbstractGridWidgetPresenter<M, V> {

	/**
	 * @author abondin
	 * 
	 */
	public interface MyView<M, U extends AbstractRemoteDataGridWidgetPresenter<M, ?>>
			extends
				AbstractGridWidgetPresenter.MyView<M, U> {

		public static int DEFAULT_GRID_PAGE_SIZE = 100;

		Order getCurrentOrder();

		void loadFirstPage();

		void reload(boolean force);
	}

	/**
	 * A criterion which is created by user filters and can be changed by user.
	 * 
	 * It must be private.
	 * 
	 * 1) Override and use
	 * {@link AbstractRemoteDataGridWidgetPresenter#createFilteringCriterion()}
	 * in subclasses to create criterion. This method is called during
	 * initialization and can be called in runtime.
	 * 
	 * 2) Use
	 * {@link AbstractRemoteDataGridWidgetPresenter#setFilteringCriterion(Criterion)}
	 * to change criterion in runtime.
	 * 
	 * 3) Use
	 * {@link AbstractRemoteDataGridWidgetPresenter#getFilteringCriterion()} to
	 * get criterion.
	 * 
	 * 4) Use {#link
	 * {@link AbstractRemoteDataGridWidgetPresenter#getConfigurableCriterion()}
	 * to get combined criterion (loading and filtering).
	 */

	private Criterion filteringCriterion;

	/**
	 * An internal criterion described data restrications and relationships
	 * which must not be changed by user.
	 * 
	 * 
	 * It must be private.
	 * 
	 * 1) Override and use
	 * {@link AbstractRemoteDataGridWidgetPresenter#createLoadingCriterion()} in
	 * subclasses to create criterion. This method is called during
	 * initialization and can be called in runtime.
	 * 
	 * 2) Use
	 * {@link AbstractRemoteDataGridWidgetPresenter#setLoadingCriterion(Criterion)}
	 * to change criterion in runtime.
	 * 
	 * 3) Use
	 * {@link AbstractRemoteDataGridWidgetPresenter#getLoadingCriterion()} to
	 * get criterion.
	 * 
	 * 4) Use {#link
	 * {@link AbstractRemoteDataGridWidgetPresenter#getConfigurableCriterion()}
	 * to get combined criterion (loading and filtering).
	 */
	private Criterion loadingCriterion;

	public AbstractRemoteDataGridWidgetPresenter(final EventBus eventBus,
			final V view) {
		super(eventBus, view);
	}

	/**
	 * Creates filtering criterion. This method is called during initialization
	 * and can be called in runtime.
	 * 
	 * @see {@link #filteringCriterion}
	 */
	protected abstract Criterion createFilteringCriterion();

	/**
	 * Creates loading criterion. This method is called during initialization
	 * and can be called in runtime.
	 * 
	 * @ see {@link #loadingCriterion}
	 */
	protected abstract Criterion createLoadingCriterion();

	/**
	 * Gets combined criterion (loading and filtering).
	 * 
	 * @return
	 */
	public Criterion getConfigurableCriterion() {
		return SimpleUtils
				.mergeCriterions(loadingCriterion, filteringCriterion);
	}

	public Order getCurrentOrder() {
		return getView().getCurrentOrder();
	}

	/**
	 * @see {@link #filteringCriterion}
	 * 
	 * @return {@link #filteringCriterion}
	 */
	public Criterion getFilteringCriterion() {
		return filteringCriterion;
	}

	/**
	 * @see {@link #loadingCriterion}
	 * 
	 * @return {@link #loadingCriterion}
	 */
	public Criterion getLoadingCriterion() {
		return loadingCriterion;
	}

	@Inject
	protected void initialize() {
		loadingCriterion = createLoadingCriterion();
		filteringCriterion = createFilteringCriterion();
	}

	public void loadFirstPage() {
		getView().loadFirstPage();
	}

	@Override
	public void reload(final boolean force) {
		getView().reload(force);
	}

	/**
	 * @see {@link #filteringCriterion}
	 * 
	 * @param filteringCriterion
	 */
	public void setFilteringCriterion(final Criterion filteringCriterion) {
		this.filteringCriterion = filteringCriterion;
	}

	/**
	 * @see {@link #loadingCriterion}
	 * 
	 * @param loadingCriterion
	 */
	public void setLoadingCriterion(final Criterion loadingCriterion) {
		this.loadingCriterion = loadingCriterion;
	}
}
