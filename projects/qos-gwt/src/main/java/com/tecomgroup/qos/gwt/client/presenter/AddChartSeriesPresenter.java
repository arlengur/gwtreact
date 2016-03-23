/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.gwt.client.event.chart.BuildChartsEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesAddedEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesEvent;
import com.tecomgroup.qos.gwt.client.event.chart.ChartSeriesRemovedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectionListener;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ChartSelectionWidget.ChartSelectionListener;
import com.tecomgroup.qos.service.AgentServiceAsync;

/**
 *
 * Добавление серии на график
 *
 * @ticket #1736
 * @author abondin
 *
 */
public class AddChartSeriesPresenter
		extends
			PresenterWidget<AddChartSeriesPresenter.MyView>
		implements
			UiHandlers,
			AgentSelectionListener,
			ChartSelectionListener{

	public static interface MyView
			extends
				PopupView,
				HasUiHandlers<AddChartSeriesPresenter> {

		void loadAgents(List<MAgent> agents);

		void loadCharts();

		void postInitialize();

		void select(MAgent agent, MAgentTask task,
				MResultParameterConfiguration parameter, String chart);

		void updateSeries(final boolean updateChartWizard);

		String validate();
	}

	private final List<ChartSeriesEvent<?>> events = new ArrayList<ChartSeriesEvent<?>>();

	private final AgentServiceAsync agentService;

	public static Logger LOGGER = Logger
			.getLogger(AddChartSeriesPresenter.class.getName());

	private final List<MChartSeries> allSeries = new ArrayList<MChartSeries>();

	private final QoSMessages messages;

	/**
	 * @param eventBus
	 * @param view
	 */
	@Inject
	public AddChartSeriesPresenter(final EventBus eventBus, final MyView view,
			final QoSMessages messages, final AgentServiceAsync agentService) {
		super(eventBus, view);
		this.messages = messages;
		this.agentService = agentService;
		getView().setUiHandlers(this);
	}

	public void actionAddChartSeries(final MAgentTask task,
			final MResultParameterConfiguration parameter,
			final String chartName) {
		String errorMessage = null;
		if (task == null) {
			errorMessage = messages.taskNotSelected();
		} else if (parameter == null) {
			errorMessage = messages.parameterNotSelected();
		} else if (chartName == null) {
			errorMessage = messages.chartNotSelected();
		} else {
			errorMessage = getView().validate();
			if (errorMessage == null) {
				final MChartSeries newItem = new MChartSeries(task, parameter,
						chartName);
				final MChartSeries chart = ChartResultUtils.findSeriesByKey(
						allSeries, newItem.getUniqueKey());
				if (chart == null) {
					if (ChartResultUtils.isUnitCompatible(newItem,
							ChartResultUtils.findSeriesByChartName(allSeries,
									chartName), true)) {
						addSeries(newItem);
					} else {
						errorMessage = messages
								.equalsMeasureUnitsOneChartConstraint();
					}
				} else {
					errorMessage = messages.seriesExistsConstraint();
				}
			}
		}
		if (errorMessage != null) {
			AppUtils.showErrorMessage(errorMessage);
		}
	}

	public void actionBuildCharts() {
		AppUtils.getEventBus().fireEvent(new BuildChartsEvent());
	}

	public void actionCommit() {
		for (final ChartSeriesEvent<?> event : events) {
			AppUtils.getEventBus().fireEvent(event);
		}
	}

	public void actionRemoveChartSeries(final List<String> seriesKeys) {
		events.add(new ChartSeriesRemovedEvent(seriesKeys));
		for (final String key : seriesKeys) {
			final MChartSeries series = ChartResultUtils.findSeriesByKey(
					allSeries, key);
			if (series != null) {
				allSeries.remove(series);
			}
		}
		getView().updateSeries(false);
	}

	public void addSeries(final MChartSeries series) {
		if (series != null) {
			allSeries.add(series);
			events.add(new ChartSeriesAddedEvent(series));
		}
		getView().updateSeries(true);
	}

	@Override
	public void agentSelected(final MAgent agent) {
		getView().select(agent, null, null, null);
	}

	@Override
	public void chartSelectionChanged() {
		getView().updateSeries(false);
	}

	/**
	 * @return the allSeries
	 */
	public List<MChartSeries> getAllSeries() {
		return allSeries;
	}

	private void loadAgents() {
		agentService
				.getAllAgents(new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgent>>() {

					@Override
					protected void failure(final Throwable caught) {
						LOGGER.log(Level.SEVERE, "Cannot load agents", caught);
					}

					@Override
					protected void success(final List<MAgent> result) {
						if (result != null) {
							getView().loadAgents(result);
							if (result.size() == 1) {
								getView().select(result.iterator().next(),
										null, null, null);
							}
						}
					}
				});
	}

	@Override
	protected void onBind() {
		getView().postInitialize();
		super.onBind();
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadAgents();
		getView().loadCharts();
		getView().updateSeries(false);
		events.clear();
	}

	public void removeSeries(final MChartSeries series) {
		getView().updateSeries(true);
	}
	/**
	 * @param allSeries
	 *            the allSeries to set
	 */
	public void setAllSeries(final List<MChartSeries> allSeries) {
		this.allSeries.clear();
		if (allSeries != null) {
			this.allSeries.addAll(allSeries);
		}
	}
}
