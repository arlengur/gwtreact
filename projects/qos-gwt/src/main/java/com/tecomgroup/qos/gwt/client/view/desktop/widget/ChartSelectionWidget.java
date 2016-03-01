/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;

/**
 * Компонент для выбора графика или создания нового
 * 
 * @ticket #1736
 * @author abondin
 * 
 */
public class ChartSelectionWidget implements ClientConstants {

	public static interface ChartSelectionListener {
		void chartSelectionChanged();
	}

	private final Set<ChartSelectionListener> listeners = new HashSet<ChartSelectionWidget.ChartSelectionListener>();

	private TextField newChartField;

	private ComboBox<String> selectChartCombo;

	private Radio selectChartRadio;

	private Radio newChartRadio;

	private ToggleGroup radioGroup;

	private String selectedChart = null;

	private final QoSMessages messages;

	@Inject
	public ChartSelectionWidget(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		this.messages = messages;
		initializeUI(appearanceFactoryProvider);
		initializeListeners();
	}

	public void addChart(final String chartName) {
		if (chartName != null && !chartName.trim().isEmpty()) {
			radioGroup.setValue(selectChartRadio, true);
			if (!selectChartCombo.getStore().getAll().contains(chartName)) {
				selectChartCombo.getStore().add(chartName);
				selectChartCombo.getStore().commitChanges();
			}
			selectChartCombo.select(chartName);
			selectChartCombo.setValue(chartName, true);
			resetNewChartField(selectChartCombo.getStore().getAll());
		}
	}

	public void addListener(final ChartSelectionListener listener) {
		listeners.add(listener);
	}

	public String getChart() {
		if (selectChartRadio.getValue()) {
			return selectChartCombo.getCurrentValue();
		} else {
			return newChartField.getText().trim().isEmpty()
					? null
					: newChartField.getText().trim();
		}
	}

	/**
	 * @return the newChartField
	 */
	public TextField getNewChartField() {
		return newChartField;
	}

	/**
	 * @return the newChartRadio
	 */
	public Radio getNewChartRadio() {
		return newChartRadio;
	}

	/**
	 * @return the selectChartCombo
	 */
	public ComboBox<String> getSelectChartCombo() {
		return selectChartCombo;
	}
	/**
	 * @return the selectChartRadio
	 */
	public Radio getSelectChartRadio() {
		return selectChartRadio;
	}

	private void initializeListeners() {
		radioGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {
					@Override
					public void onValueChange(
							final ValueChangeEvent<HasValue<Boolean>> event) {
						notifySelectionChanged();
						if (event.getValue() == selectChartRadio) {
							selectChartCombo.setEnabled(true);
							newChartField.setEnabled(false);
							resetNewChartField(selectChartCombo.getStore()
									.getAll());
						} else if (event.getValue() == newChartRadio) {
							newChartField.setEnabled(true);
							selectChartCombo.setEnabled(false);
						}
					}
				});
		selectChartCombo.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(final SelectionEvent<String> event) {
				notifySelectionChanged();
			}
		});
		selectChartCombo
				.addValueChangeHandler(new ValueChangeHandler<String>() {
					@Override
					public void onValueChange(
							final ValueChangeEvent<String> event) {
						notifySelectionChanged();
					}
				});
	}

	/**
	 * @param appearanceFactoryProvider
	 */
	private void initializeUI(
			final AppearanceFactoryProvider appearanceFactoryProvider) {

		selectChartCombo = new ComboBox<String>(new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(final String item) {
						return item;
					}
				}), new LabelProvider<String>() {
			@Override
			public String getLabel(final String item) {
				return item;
			}
		}, appearanceFactoryProvider.get().triggerFieldAppearance());
		selectChartCombo.setAllowBlank(false);
		selectChartCombo.setForceSelection(true);
		selectChartCombo.setTypeAhead(true);
		selectChartCombo.setEditable(false);
		selectChartCombo.setTriggerAction(TriggerAction.ALL);
		selectChartCombo.setWidth(DEFAULT_FIELD_WIDTH);
		selectChartCombo.getStore().setAutoCommit(true);

		newChartField = new TextField();

		radioGroup = new ToggleGroup();
		selectChartRadio = new Radio();
		selectChartRadio.setBoxLabel(messages.selectChartRadio());
		newChartRadio = new Radio();
		newChartRadio.setBoxLabel(messages.newChartRadio());
		radioGroup.add(selectChartRadio);
		radioGroup.add(newChartRadio);
	}

	public void loadCharts(final Collection<String> charts) {
		selectChartCombo.getStore().clear();
		selectChartCombo.clear();
		resetNewChartField(charts);
		if (charts == null || charts.isEmpty()) {
			radioGroup.setValue(newChartRadio, true);
		} else {
			selectChartCombo.getStore().addAll(charts);
            int chartIndexToSelect = 0;
			if (selectedChart != null) {
                chartIndexToSelect = selectChartCombo.getStore().indexOf
                        (selectedChart);
			}
            selectChartCombo.select(chartIndexToSelect);
            selectChartCombo.setValue(selectChartCombo.getStore().get(chartIndexToSelect),
                    true);
		}

	}

	private void notifySelectionChanged() {
		final String chart = getChart();
		if (chart != selectedChart) {
			for (final ChartSelectionListener listener : listeners) {
				listener.chartSelectionChanged();
			}
			selectedChart = chart;
		}
	}

	public void removeListener(final ChartSelectionListener listener) {
		listeners.remove(listener);
	}

	protected void resetNewChartField(final Collection<String> charts) {
		newChartField.clear();
		int index = 1;
		boolean exist = true;
		String newChartName = messages.chartNamePrefix();
		while (exist) {
			newChartName = messages.chartNamePrefix() + " " + index;
			exist = false;
			for (final String chartName : charts) {
				if (chartName.equals(newChartName)) {
					exist = true;
					index++;
					break;
				}
			}
		}
		newChartField.setText(newChartName);
	}

	public String validate() {
		if (radioGroup.getValue() == newChartRadio) {
			final String newChartName = newChartField.getText().trim();
			if (!newChartName.isEmpty()) {
				final List<String> charts = selectChartCombo.getStore()
						.getAll();
				for (final String chart : charts) {
					if (newChartName.equals(chart)) {
						resetNewChartField(charts);
						return messages.chartRenameFail(newChartName);
					}
				}
			}
		}
		return null;
	}

}
