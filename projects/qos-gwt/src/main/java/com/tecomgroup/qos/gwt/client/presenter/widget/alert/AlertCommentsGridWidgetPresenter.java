/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.service.AlertHistoryRetrieverAsync;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author novohatskiy.r
 * 
 */
public class AlertCommentsGridWidgetPresenter
		extends
			SingleAlertHistoryGridWidgetPresenter {

	public interface MyView
			extends
				SingleAlertHistoryGridWidgetPresenter.MyView {
	}

	@Inject
	public AlertCommentsGridWidgetPresenter(final EventBus eventBus,
			final MyView view, final AlertHistoryRetrieverAsync historyService) {
		super(eventBus, view, historyService);
		getView().setUiHandlers(this);
	}

	@Override
	protected Criterion createLoadingCriterion() {
		final Criterion parentCriterion = super.createLoadingCriterion();

		final Criterion currentCriterion = CriterionQueryFactory.getQuery()
				.isNotNull(alertHistoryProperties.comment().getPath());

		return SimpleUtils.mergeCriterions(parentCriterion, currentCriterion);
	}
}
