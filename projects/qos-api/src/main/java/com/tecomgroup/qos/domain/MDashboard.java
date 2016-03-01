/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget;

/**
 * 
 * Domain class to represent Widget Dashboard. (Home Page of the Q'ligent Vision
 * application)
 * 
 * @author abondin
 * 
 */
@Entity
public class MDashboard extends MAbstractEntity {

	private static final long serialVersionUID = 7336918412262349592L;

	@Column(unique = true)
	private String username;

	private int columnNumber = 3;

	private int rowNumber = 2;

	@JsonIgnore
	@Column(length = 50240)
	private String serializedWidgets;

	@Transient
	private Map<String, DashboardWidget> widgets = new LinkedHashMap<String, DashboardWidget>();
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * Add widget to dashboard
	 * 
	 * @param widget
	 * @return false if dashboard already contains that widget
	 */
	public boolean addWidget(final DashboardWidget widget) {
		return widgets.put(widget.getKey(), widget) == null;
	}

	public Map<String, DashboardChartWidget> getChartWidgets() {
		final Map<String, DashboardChartWidget> result = new HashMap<String, DashboardChartWidget>();
		for (final DashboardWidget dashboardWidget : widgets.values()) {
			if (dashboardWidget instanceof DashboardChartWidget) {
				final DashboardChartWidget chartWidget = (DashboardChartWidget) dashboardWidget;
				result.put(chartWidget.getKey(), chartWidget);
			}
		}
		return result;
	}

	public Map<String, DashboardChartWidget> getChartWidgetsByChartName(
			final String chartName) {
		final Map<String, DashboardChartWidget> result = new HashMap<String, DashboardChartWidget>();
		for (final DashboardWidget dashboardWidget : widgets.values()) {
			if (dashboardWidget instanceof DashboardChartWidget) {
				final DashboardChartWidget chartWidget = (DashboardChartWidget) dashboardWidget;
				if (chartName.equals(chartWidget.getChartName())) {
					result.put(chartWidget.getKey(), chartWidget);
				}
			}
		}
		return result;
	}

	/**
	 * @return the columnNumber
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	/**
	 * @return the rowNumber
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * <b>For internal modelspace usage only!</b>
	 * 
	 * @return the serializedWidgets
	 */
	public String getSerializedWidgets() {
		return serializedWidgets;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the widgets
	 */
	public Map<String, DashboardWidget> getWidgets() {
		return widgets;
	}

	/**
	 * Checks whether dashboard contains widget.
	 * 
	 * @param widget
	 *            the widget to check.
	 * @return true if dashboard contains widget otherwise false.
	 */
	public boolean hasWidget(final DashboardWidget widget) {
		return widget != null && widgets.containsKey(widget.getKey());
	}

	/**
	 * Checks whether dashboard contains widget by its key.
	 * 
	 * @param widgetKey
	 *            the widget to check.
	 * @return true if dashboard contains widget otherwise false.
	 */
	public boolean hasWidget(final String widgetKey) {
		return widgets.containsKey(widgetKey);
	}

	/**
	 * @return
	 */
	public DashboardWidget removeWidget(final String widgetKey) {
		return widgets.remove(widgetKey);
	}

	/**
	 * @param columnNumber
	 *            the columnNumber to set
	 */
	public void setColumnNumber(final int columnNumber) {
		this.columnNumber = columnNumber;
	}

	/**
	 * @param rowNumber
	 *            the rowNumber to set
	 */
	public void setRowNumber(final int rowNumber) {
		this.rowNumber = rowNumber;
	}

	/**
	 * <b>For internal modelspace usage only!</b>
	 * 
	 * @param serializedWidgets
	 *            the serializedWidgets to set
	 */
	public void setSerializedWidgets(final String serializedWidgets) {
		this.serializedWidgets = serializedWidgets;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(final String username) {
		this.username = username;
	}

	/**
	 * @param widgets
	 *            the widgets to set
	 */
	public void setWidgets(final Map<String, DashboardWidget> widgets) {
		this.widgets = widgets;
	}

	/**
	 * Updates widget in dashboard.
	 * 
	 * @param widget
	 */
	public void updateWidget(final DashboardWidget widget) {
		widgets.put(widget.getKey(), widget);
	}

	/**
	 * Updates widget and its key in the dashboard
	 * 
	 * @param oldKey - widget's key before its state changing
	 * @param widget
	 */
	public void updateWidgetByKey(final String oldKey,
                                  final DashboardWidget widget) {
		final Map<String, DashboardWidget> newWidgets = new LinkedHashMap<String, DashboardWidget>();
		for (final Map.Entry<String, DashboardWidget> entry : widgets
				.entrySet()) {
			if (entry.getKey().equals(oldKey)) {
				newWidgets.put(widget.getKey(), widget);
			} else {
				newWidgets.put(entry.getKey(), entry.getValue());
			}
		}
		widgets = newWidgets;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
