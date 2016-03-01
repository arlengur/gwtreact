/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.report;

import java.util.Set;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.report.AddAnalyticsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AddNamedWidgetToDashboardView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeIntervalWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.alert.AlertSeverityToolbarWidget;

/**
 * @author ivlev.e
 * 
 */
public class AddAnalyticsToDashboardView extends AddNamedWidgetToDashboardView
		implements
			AddAnalyticsToDashboardWidgetPresenter.MyView {

	private final static int DIALOG_HEIGHT = 260;

	private final AlertSeverityToolbarWidget severityToolbarWidget;

	private final DateTimeIntervalWidget dateTimeIntervalWidget;

	private ComboBox<Integer> topSizeControl;

	private final static int DEFAULT_TOP_SIZE = 3;

	private final static int TOP_SIZE_1 = 1;

	private final static int TOP_SIZE_2 = 2;

	private final static int TOP_SIZE_3 = 3;

	@Inject
	public AddAnalyticsToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages,
			final AlertSeverityToolbarWidget alertSeverityToolbarWidget,
			final DateTimeIntervalWidget dateTimeIntervalWidget) {
		super(eventBus, appearanceFactoryPrvider, messages);
		this.severityToolbarWidget = alertSeverityToolbarWidget;
		this.dateTimeIntervalWidget = dateTimeIntervalWidget;
	}

	private void addToolbarToParentContainer(final Widget toolbar) {
		final VerticalLayoutData layoutData = new VerticalLayoutData(1, -1,
				new Margins(1, 5, 0, 5));
		mainContainer.add(toolbar, layoutData);
	}

	@Override
	protected boolean areUserFieldsValid() {
		return super.areUserFieldsValid()
				&& severityToolbarWidget.isValid()
				&& isIntervalTypeValid(dateTimeIntervalWidget
						.getTimeIntervalType())
				&& isTopSizeValid(topSizeControl.getCurrentValue());
	}

	private void createAndAddToolbarToParentContainer(
			final CssFloatLayoutContainer child) {
		final FramedPanel toolbar = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());
		toolbar.setHeaderVisible(false);
		toolbar.setBodyBorder(false);
		toolbar.setBorders(false);
		toolbar.add(child);

		addToolbarToParentContainer(toolbar);
	}

	@Override
	protected void createWidget() {
		getUiHandlers().<AddAnalyticsToDashboardWidgetPresenter> cast()
				.actionCreateWidget(getSelectedSeverities(),
						dateTimeIntervalWidget.getTimeIntervalType(),
						topSizeControl.getCurrentValue());
	}
	@Override
	protected int getDialogHeight() {
		return DIALOG_HEIGHT;
	}

	@Override
	protected String getDialogTitle() {
		return messages.newAnalyticsChartWidget();
	}

	/**
	 * @return set of selected {@link PerceivedSeverity} or null if all
	 *         severities are selected
	 */
	private Set<PerceivedSeverity> getSelectedSeverities() {
		Set<PerceivedSeverity> result = severityToolbarWidget
				.getSeverityToolbar().getCheckedValues();
		if (result.size() == PerceivedSeverity.values().length) {
			result = null;
		}
		return result;
	}

	@Override
	public void initialize() {
		super.initialize();
		initializeAnalyticsChartTypeComboBox();
		initializeDateTimeIntervalWidget();
		initializeSeverityToolbarWidget();
	}

	private void initializeAnalyticsChartTypeComboBox() {
		final ListStore<Integer> store = new ListStore<Integer>(
				new ModelKeyProvider<Integer>() {

					@Override
					public String getKey(final Integer item) {
						return item + "";
					}
				});
		store.add(TOP_SIZE_1);
		store.add(TOP_SIZE_2);
		store.add(TOP_SIZE_3);

		topSizeControl = new ComboBox<Integer>(store,
				new LabelProvider<Integer>() {

					@Override
					public String getLabel(final Integer item) {
						return "Top-" + item;
					}
				});
		topSizeControl.setEditable(false);
		topSizeControl.setAllowBlank(false);
		topSizeControl.setTriggerAction(TriggerAction.ALL);

		topSizeControl.setValue(DEFAULT_TOP_SIZE, false, true);
		topSizeControl.getElement().setMargins(new Margins(6, 0, 2, 6));

		final CssFloatLayoutContainer emergencyAgentsTopContainer = new CssFloatLayoutContainer();
		emergencyAgentsTopContainer.getElement().setMargins(
				new Margins(0, 0, 4, 0));
		topSizeControl.setWidth(100);

		final Label label = new Label(messages.analyticsChartType() + ":");
		label.getElement().<XElement> cast()
				.setMargins(new Margins(8, 0, 4, 5));
		emergencyAgentsTopContainer.add(label, new CssFloatData());

		emergencyAgentsTopContainer.add(topSizeControl, new CssFloatData());

		createAndAddToolbarToParentContainer(emergencyAgentsTopContainer);
	}

	private void initializeDateTimeIntervalWidget() {
		final CssFloatLayoutContainer toolbarButtonsContainer = new CssFloatLayoutContainer();
		toolbarButtonsContainer.getElement()
				.setMargins(new Margins(0, 0, 4, 0));

		final Label label = new Label(messages.timeInterval() + ":");
		label.getElement().<XElement> cast()
				.setMargins(new Margins(8, 0, 4, 5));
		toolbarButtonsContainer.add(label, new CssFloatData());

		final TextButton lastDayButton = dateTimeIntervalWidget
				.getLastDayButton();
		lastDayButton.getElement().setMargins(new Margins(6, 0, 4, 6));
		toolbarButtonsContainer.add(lastDayButton, new CssFloatData());

		final TextButton lastWeekButton = dateTimeIntervalWidget
				.getLastWeekButton();
		lastWeekButton.getElement().setMargins(new Margins(6, 0, 4, 6));
		toolbarButtonsContainer.add(lastWeekButton, new CssFloatData());

		final TextButton lastMonthButton = dateTimeIntervalWidget
				.getLastMonthButton();
		lastMonthButton.getElement().setMargins(new Margins(6, 0, 4, 6));
		toolbarButtonsContainer.add(lastMonthButton, new CssFloatData());

		createAndAddToolbarToParentContainer(toolbarButtonsContainer);

		// only first initialization of the dateTimeIntervalWidget
		dateTimeIntervalWidget.selectTimeInterval(lastMonthButton, false);
	}

	private void initializeSeverityToolbarWidget() {
		addToolbarToParentContainer(severityToolbarWidget.asWidget());
	}

	/**
	 * Validates selected interval type. Show error message if it is invalid.
	 * 
	 * @param severities
	 * @return
	 */
	private boolean isIntervalTypeValid(final Type intervalType) {
		final boolean result = (intervalType != null && (Type.DAY == intervalType
				|| Type.WEEK == intervalType || Type.MONTH == intervalType));
		if (!result) {
			AppUtils.showErrorMessage(messages.timeIntervalNotSelected());
		}
		return result;
	}

	private boolean isTopSizeValid(final Integer topSize) {
		return topSize != null
				&& (topSize.equals(TOP_SIZE_1) || topSize.equals(TOP_SIZE_2) || topSize
						.equals(TOP_SIZE_3));
	}
}
