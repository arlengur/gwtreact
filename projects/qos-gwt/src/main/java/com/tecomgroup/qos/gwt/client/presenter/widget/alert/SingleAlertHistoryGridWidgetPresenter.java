/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.service.AlertHistoryRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Shows history for one single alert. To load alert history please use
 * {@link #loadHistory(MAlert)}
 * 
 * @author novohatskiy.r
 * 
 */
public class SingleAlertHistoryGridWidgetPresenter
		extends
			AlertsHistoryGridWidgetPresenter {

	public interface MyView extends AlertsHistoryGridWidgetPresenter.MyView {
		/**
		 * Apply default filters, set default order, etc
		 */
		public void applyDefaultConfiguration();
	}

	private MAlert alert;

	@Inject
	public SingleAlertHistoryGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final AlertHistoryRetrieverAsync historyService) {
		super(eventBus, view, historyService);
	}

	@Override
	protected Criterion createLoadingCriterion() {
		final Criterion parentCriterion = super.createLoadingCriterion();

		Criterion currentCriterion = null;
		if (alert != null) {
			currentCriterion = CriterionQueryFactory.getQuery().eq("alert.id",
					alert.getId());
		}

		return SimpleUtils.mergeCriterions(parentCriterion, currentCriterion);
	}

	/**
	 * Loads history into view for given alert.
	 * 
	 * @param alert
	 */
	public void loadHistory(final MAlert alert) {
		if (this.alert == null || !this.alert.equals(alert)) {
			this.alert = alert;
			setLoadingCriterion(createLoadingCriterion());
			((MyView) getView()).applyDefaultConfiguration();
			reload(true);
		}
	}

	@Override
	protected void onHide() {
		super.onHide();
		this.alert = null;
	}
}
