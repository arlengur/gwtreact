/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AlertHistoryProperties;
import com.tecomgroup.qos.service.AlertHistoryRetrieverAsync;

/**
 * @author abondin
 * 
 */
public class AlertsHistoryGridWidgetPresenter
		extends
			AbstractRemoteDataGridWidgetPresenter<MAlertUpdate, AlertsHistoryGridWidgetPresenter.MyView>
		implements
			UiHandlers {

	public interface MyView
			extends
				AbstractRemoteDataGridWidgetPresenter.MyView<MAlertUpdate, AlertsHistoryGridWidgetPresenter> {

		AlertHistoryProperties getAlertHistoryProperties();
	}

	private final AlertHistoryRetrieverAsync historyService;

	protected AlertHistoryProperties alertHistoryProperties;

	@Inject
	public AlertsHistoryGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final AlertHistoryRetrieverAsync historyService) {
		super(eventBus, view);
		this.historyService = historyService;
		getView().setUiHandlers(this);
		alertHistoryProperties = view.getAlertHistoryProperties();
	}

	/**
	 * 
	 * @param criterion
	 * @param order
	 * @param startPosition
	 * @param size
	 * @param callback
	 */
	public void actionLoadHistory(final Criterion criterion, final Order order,
			final int startPosition, final int size,
			final AsyncCallback<List<MAlertUpdate>> callback) {
		historyService.getAllAlertHistory(criterion, order, startPosition,
				size, callback);
	}

	/**
	 * @param item
	 */
	public void addHistory(final MAlertUpdate item) {
		getView().addItem(item);
	}

	@Override
	protected Criterion createFilteringCriterion() {
		return null;
	}

	@Override
	protected Criterion createLoadingCriterion() {
		return null;
	}

	/**
	 * 
	 * @param criterion
	 * @param callback
	 */
	public void getTotalHistoryCount(final Criterion criterion,
			final AsyncCallback<Long> callback) {
		historyService.getAlertHistoryTotalCount(criterion, callback);
	}

	/**
	 * Overriding of this method in subclasses is prohibited in order to prevent
	 * redundant invocation of
	 * {@link AbstractRemoteDataGridWidgetPresenter#reload(boolean)}
	 * 
	 * @see com.gwtplatform.mvp.client.PresenterWidget#onReveal()
	 */
	@Override
	protected final void onReveal() {
		super.onReveal();
	}

	/**
	 * @param item
	 */
	public void removeHistory(final MAlertUpdate item) {
		getView().removeItem(item);
	}

	/**
	 * @param item
	 */
	public void updateHistory(final MAlertUpdate item) {
		getView().updateItem(item);
	}

}
