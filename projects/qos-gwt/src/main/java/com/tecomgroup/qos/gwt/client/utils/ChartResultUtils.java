/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import java.util.*;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.tecomgroup.qos.ChartType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages.DefaultFormattedResultMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.ChartSettings;
import com.tecomgroup.qos.gwt.client.view.desktop.ChartToolbar.LineType;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class ChartResultUtils {

	public static enum Axis {
		Y, X
	}

	private static boolean chartParametersInitialized = false;

	/**
	 * Apply threshold to value and return label of assigned color
	 * 
	 * @param value
	 * @param criticalThreshold
	 * @param thresholdType
	 * @return - CRITICAL, WARNING or NORMAL
	 */
	private static native String applyCriticalThresholdToValue(double value,
			double criticalThreshold, String thresholdType) /*-{
		return $wnd.qosThresholdModule.applyThresholdToValue(value, null,
				criticalThreshold, thresholdType);
	}-*/;

	/**
	 * Apply threshold to value and return label of assigned color
	 * 
	 * @param value
	 * @param warningThreshold
	 * @param criticalThreshold
	 * @param thresholdType
	 * @return - CRITICAL, WARNING or NORMAL
	 */
	private static native String applyThresholdToValue(double value,
			double warningThreshold, double criticalThreshold,
			String thresholdType) /*-{
		return $wnd.qosThresholdModule.applyThresholdToValue(value,
				warningThreshold, criticalThreshold, thresholdType);
	}-*/;

	/**
	 * Apply threshold to value and return label of assigned color
	 * 
	 * @param threshold
	 * @param value
	 * @return - CRITICAL, WARNING or NORMAL
	 */
	public static String applyThresholdToValue(
			final MParameterThreshold threshold, final Double value) {
		String style = "";

		if (threshold != null) {
			final Double warningThreshold = threshold.getWarningLevel();
			final Double criticalThreshold = threshold.getCriticalLevel();
			final ThresholdType thresholdType = threshold.getType();

			if (value != null && !value.equals(Double.NaN)) {
				final double doubleValue = value.doubleValue();
				final String thresholdTypeValue = thresholdType.toString();
				if (warningThreshold != null && criticalThreshold != null) {
					style = ChartResultUtils
							.applyThresholdToValue(doubleValue,
									warningThreshold.doubleValue(),
									criticalThreshold.doubleValue(),
									thresholdTypeValue);
				} else if (criticalThreshold != null) {
					style = ChartResultUtils.applyCriticalThresholdToValue(
							doubleValue, criticalThreshold.doubleValue(),
							thresholdTypeValue);
				} else if (warningThreshold != null) {
					style = ChartResultUtils.applyWarningThresholdToValue(
							doubleValue, warningThreshold.doubleValue(),
							thresholdTypeValue);
				}
			}
		}
		return style;
	}

	/**
	 * Apply threshold to value and return label of assigned color
	 * 
	 * @param value
	 * @param warningThreshold
	 * @param thresholdType
	 * @return - CRITICAL, WARNING or NORMAL
	 */
	private static native String applyWarningThresholdToValue(double value,
			double warningThreshold, String thresholdType) /*-{
		return $wnd.qosThresholdModule.applyThresholdToValue(value,
				warningThreshold, null, thresholdType);
	}-*/;

	public static native void changeTypeBoolChart(String chartName,
			String oldType, String newType) /*-{
		$wnd.qosChartModule.changeTypeBoolChart(chartName, oldType, newType);
	}-*/;

	public static native void changeTypeMultipleSeriesChart(String chartName,
			String oldType, String newType) /*-{
		$wnd.qosChartModule.changeTypeMultipleSeriesChart(chartName, oldType,
				newType);
	}-*/;

	public static native void changeTypeSingleSeriesChart(String chartName,
			String oldType, String newType) /*-{
		$wnd.qosChartModule.changeTypeSingleSeriesChart(chartName, oldType,
				newType);
	}-*/;

	public static native void clearAll() /*-{
		$wnd.qosChartModule.clearAll();
	}-*/;

	public static native void setTimezoneOffset(int timezoneOffset) /*-{
        $wnd.qosChartModule.setTimezoneOffset(timezoneOffset);
    }-*/;

	public static native void createBoolChart(String chartName,
			String chartType, JsArray<JavaScriptObject> seriesData,
			double startDate, double endDate, String xTitle, String yTitle,
			int height, String divElementId,
			boolean isCaptionsEnabled, boolean isZoomEnabled,
			boolean isMouseTrackingEnabled, boolean isLegendEnabled) /*-{

		$wnd.qosChartModule.createBoolChart(chartName, chartType, seriesData,
				startDate, endDate, xTitle, yTitle, height,
				divElementId, isCaptionsEnabled, isZoomEnabled,
				isMouseTrackingEnabled, isLegendEnabled);
	}-*/;

	public static void createChart(final ChartSettings settings,
			final int height, final String timeMessage) {
		JsArray<JavaScriptObject> dataArray;
		MResultParameterConfiguration parameter;
		ChartResultUtils.setTimezoneOffset(-settings.getTimeZoneOffset());
		switch (settings.getChartType()) {
			case LEVEL_SINGLE :
			case PERCENTAGE_SINGLE :
				final MChartSeries series = settings.getSeries().get(0);
				parameter = series.getParameter();
				ChartResultUtils.createSingleSeriesChart(settings.getChartName(),
						settings.getLineType(),
						series.getTask().getKey(),
						parameter.getName(),
						ConfigurationUtil.propertiesToString(parameter.getProperties(), true),
						parameter.getParsedDisplayFormat(),
						series.getKey(),
						settings.getStartDate().getTime(),
						settings.getEndDate().getTime(),
						parameter.getThreshold().getCriticalLevel(),
						parameter.getThreshold().getWarningLevel(),
						parameter.getThreshold().getType().toString(),
						timeMessage,
						getYTitle(parameter.getUnits()),
						height,
						settings.getDivElementId(),
						settings.isCaptionsEnabled(),
						settings.isAutoscalingEnabled(),
						settings.isZoomEnabled(),
						settings.isMouseTrackingEnabled(),
						settings.isLegendEnabled());
				break;
			case LEVEL_MULTIPLE :
			case COUNTER :
			case PERCENTAGE_MULTIPLE :

				parameter = settings.getSeries().get(0).getParameter();

				dataArray = createJsSeriesData(settings.getSeries());

				ChartResultUtils.createChart(
						settings.getChartType().toString(),
						settings.getChartName(),
						settings.getLineType(),
						dataArray,
						settings.getStartDate().getTime(),
						settings.getEndDate().getTime(),
						timeMessage,
						getYTitle(parameter.getUnits()),
						height,
						settings.getDivElementId(),
						settings.isThresholdsEnabled(),
						settings.isCaptionsEnabled(),
						settings.isAutoscalingEnabled(),
						settings.isZoomEnabled(),
						settings.isMouseTrackingEnabled(),
						settings.isLegendEnabled());
				break;
			case BOOL :
				parameter = settings.getSeries().get(0).getParameter();

				dataArray = createJsSeriesData(settings.getSeries());

				ChartResultUtils.createBoolChart(
						settings.getChartName(),
						settings.getLineType(),
						dataArray,
						settings.getStartDate().getTime(),
						settings.getEndDate().getTime(),
						timeMessage,
						getYTitleForBooleanParameter(parameter.getUnits(),	settings.getSeries().size() == 1),
						height,
						settings.getDivElementId(),
						settings.isCaptionsEnabled(),
						settings.isZoomEnabled(),
						settings.isMouseTrackingEnabled(),
						settings.isLegendEnabled());
				break;
			case UNSUPPORTED :
				break;
		}
	}

	public static native void createChart(String chartType, String chartName,
			String lineType, JsArray<JavaScriptObject> seriesData,
			double startDate, double endDate, String xTitle, String yTitle,
			int height, String divElementId,
			boolean isThresholdsEnabled, boolean isCaptionsEnabled,
			boolean autoscaling, boolean isZoomEnabled,
			boolean isMouseTrackingEnabled, boolean isLegendEnabled) /*-{

		$wnd.qosChartModule.createChart(chartType, chartName, lineType,
				seriesData, startDate, endDate, xTitle,
				yTitle, height, divElementId, isThresholdsEnabled,
				isCaptionsEnabled, autoscaling, isZoomEnabled,
				isMouseTrackingEnabled, isLegendEnabled);
	}-*/;

	public static native JsArray<JavaScriptObject> createJsArray() /*-{
		return [];
	}-*/;

	public static native JsArrayString createJsArrayString() /*-{
		return [];
	}-*/;

	public static JsArray<JavaScriptObject> createJsSeriesData(
			final List<MChartSeries> series) {
		final JsArray<JavaScriptObject> dataArray = ChartResultUtils
				.createJsArray();

		for (final MChartSeries javaSeries : series) {
			final JSONObject jsSeries = new JSONObject();
			jsSeries.put("agentName", new JSONString(javaSeries.getAgent()
					.getDisplayName()));
			jsSeries.put("taskKey", new JSONString(javaSeries.getTask()
					.getKey()));
			jsSeries.put("taskDisplayName", new JSONString(javaSeries.getTask()
					.getDisplayName()));
			jsSeries.put("paramName", new JSONString(javaSeries.getParameter()
					.getName()));
			jsSeries.put(
					"paramProperties",
					new JSONString(ConfigurationUtil.propertiesToString(
							javaSeries.getParameter().getProperties(), true)));
			jsSeries.put("parameterDisplayFormat", new JSONString(javaSeries
					.getParameter().getParsedDisplayFormat()));
			jsSeries.put("seriesKey", new JSONString(javaSeries.getKey()));
			// In general thresholds are optional
			final Double warningThreshold = javaSeries.getParameter()
					.getThreshold().getWarningLevel();
			if (warningThreshold != null) {
				jsSeries.put("warningThreshold", new JSONNumber(
						warningThreshold));
			}
			final Double criticalThreshold = javaSeries.getParameter()
					.getThreshold().getCriticalLevel();
			if (criticalThreshold != null) {
				jsSeries.put("criticalThreshold", new JSONNumber(
						criticalThreshold));
			}
			final ThresholdType thresholdType = javaSeries.getParameter()
					.getThreshold().getType();
			if (thresholdType != null) {
				jsSeries.put("thresholdType",
						new JSONString(thresholdType.toString()));
			}
			dataArray.push(jsSeries.getJavaScriptObject());
		}
		return dataArray;
	}

	public static native void createSingleSeriesChart(String chartName, String lineType,
			String taskKey, String paramName, String paramProperties,
			String parameterDisplayFormat, String seriesKey, double startDate,
			double endDate, Double criticalThreshold,
			Double warningThreshold, String thresholdType, String xTitle,
			String yTitle, int height, String divElementId,
			boolean isCaptionsEnabled, boolean autoscaling,
			boolean isZoomEnabled, boolean isMouseTrackingEnabled,
			boolean isLegendEnabled) /*-{

		$wnd.qosChartModule.createSingleSeriesChart(chartName, lineType, taskKey,
				paramName, paramProperties, parameterDisplayFormat, seriesKey,
				startDate, endDate, criticalThreshold, warningThreshold, thresholdType, xTitle,
				yTitle, height, divElementId, isCaptionsEnabled, autoscaling,
				isZoomEnabled, isMouseTrackingEnabled, isLegendEnabled);
	}-*/;

	public static native void disableCaptions(String chartName) /*-{
		$wnd.qosChartModule.disableCaptions(chartName);
	}-*/;

	private static native void doExportChart(String chartName,
			String chartDisplayName) /*-{
		$wnd.qosChartModule.exportChart(chartName, chartDisplayName);
	}-*/;

	public static native void enableThresholdLines(String chartName) /*-{
		$wnd.qosThresholdModule.enableThresholdLines(chartName);
	}-*/;

	public static void exportChart(final String chartName,
			final String timeZoneName) {
		final Double[] extremes = getExtremes(chartName, Axis.X);
		final DateTimeFormat format = DateUtils.DATE_TIME_FORMATTER;
		final Date startDate = new Date(extremes[0].longValue());
		final Date endDate = new Date(extremes[1].longValue());
		final String chartDisplayName = chartName + " ("
				+ format.format(startDate) + " - " + format.format(endDate)
				+ " " + timeZoneName + ")";
		doExportChart(chartName, chartDisplayName);
	}

	/**
	 * 
	 * @param allSeries
	 * @param chartName
	 * @return
	 */
	public static List<MChartSeries> findSeriesByChartName(
			final Collection<MChartSeries> allSeries, final String chartName) {
		final List<MChartSeries> list = new ArrayList<MChartSeries>();
		for (final MChartSeries series : allSeries) {
			if (series.getChartName().equals(chartName)) {
				list.add(series);
			}
		}
		return list;
	}

	/**
	 * Find series from
	 * 
	 * @param allSeries
	 * @param key
	 * @return
	 */
	public static MChartSeries findSeriesByKey(
			final Collection<MChartSeries> allSeries, final String key) {
		for (final MChartSeries series : allSeries) {
			if (series.getUniqueKey().equals(key)) {
				return series;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param allSeries
	 * @return
	 */
	public static Set<String> getAllChartNames(
			final Collection<MChartSeries> allSeries) {
		final Set<String> names = new HashSet<String>();
		for (final MChartSeries chartSeries : allSeries) {
			names.add(chartSeries.getChartName());
		}
		return names;
	}

	public static String getDefaultLineType(final ChartType chartType) {
		String result;
		switch (chartType) {
			case BOOL :
				result = LineType.STEP.getName();
				break;
			case LEVEL_SINGLE :
			case PERCENTAGE_SINGLE :
				result = LineType.COLUMN.getName();
				break;
			case LEVEL_MULTIPLE :
			case PERCENTAGE_MULTIPLE :
			case COUNTER :
				result = LineType.LINE.getName();
				break;
			default :
				result = LineType.LINE.getName();
				break;
		}
		return result.toLowerCase();
	}

	public static Double[] getExtremes(final String chartName, final Axis axis) {
		final JsArrayNumber nativeArray = getExtremes(chartName,
				axis.toString());
		final Double[] extremes = new Double[nativeArray.length()];
		for (int i = 0; i < nativeArray.length(); i++) {
			extremes[i] = nativeArray.get(i);
		}
		return extremes;
	}

	private static native JsArrayNumber getExtremes(String chartName,
			String axisName) /*-{
		return $wnd.qosChartModule.getExtremes(chartName, axisName);
	}-*/;

	public static JsArrayString getLocalMonths(final QoSMessages messages) {
		final JsArrayString months = ChartResultUtils.createJsArrayString();
		months.push(messages.january());
		months.push(messages.february());
		months.push(messages.march());
		months.push(messages.april());
		months.push(messages.may());
		months.push(messages.june());
		months.push(messages.july());
		months.push(messages.august());
		months.push(messages.september());
		months.push(messages.october());
		months.push(messages.november());
		months.push(messages.december());
		return months;
	}

	public static JsArrayString getLocalMonthsShort(final QoSMessages messages) {
		final JsArrayString months = ChartResultUtils.createJsArrayString();
		months.push(messages.januaryShort());
		months.push(messages.februaryShort());
		months.push(messages.marchShort());
		months.push(messages.aprilShort());
		months.push(messages.mayShort());
		months.push(messages.juneShort());
		months.push(messages.julyShort());
		months.push(messages.augustShort());
		months.push(messages.septemberShort());
		months.push(messages.octoberShort());
		months.push(messages.novemberShort());
		months.push(messages.decemberShort());
		return months;
	}

	public static JsArrayString getLocalWeekdays(final QoSMessages messages) {
		final JsArrayString days = ChartResultUtils.createJsArrayString();
		days.push(messages.sunday());
		days.push(messages.monday());
		days.push(messages.tuesday());
		days.push(messages.wednesday());
		days.push(messages.thursday());
		days.push(messages.friday());
		days.push(messages.saturday());
		return days;
	}

	public static String[] getVisibleSeriesKeys(final String chartName) {
		final JsArrayString nativeArray = getVisibleSeriesKeysNative(chartName);
		final String[] keys = new String[nativeArray.length()];
		for (int i = 0; i < nativeArray.length(); i++) {
			keys[i] = nativeArray.get(i);
		}
		return keys;
	}

	private static native JsArrayString getVisibleSeriesKeysNative(
			String chartName) /*-{
		return $wnd.qosChartModule.getVisibleSeriesKeys(chartName);
	}-*/;

	private static String getYTitle(final String units) {
		String title;
		if (SimpleUtils.isNotNullAndNotEmpty(units)) {
			title = units;
		} else {
			// if units are empty, use whitespace that is considered as not
			// whitespace character in Highcharts
			title = "\u2003";
		}
		return title;
	}

	private static String getYTitleForBooleanParameter(final String units,
			final boolean singleSeries) {
		String title;
		if (singleSeries && SimpleUtils.isNotNullAndNotEmpty(units)) {
			title = units;
		} else {
			title = AppUtils.getMessages().bool();
		}
		return title;
	}

	public static Map<String, List<MChartSeries>> groupByChartName(
			final Collection<MChartSeries> allChartSeries) {
		final Map<String, List<MChartSeries>> map = new TreeMap<String, List<MChartSeries>>();
		for (final MChartSeries series : allChartSeries) {
			final String chartName = series.getChartName();
			List<MChartSeries> list = map.get(chartName);
			if (list == null) {
				list = new ArrayList<MChartSeries>();
				map.put(chartName, list);
			}
			list.add(series);
		}
		return map;
	}

	public static List<String> getTimeZones(
			final Collection<MChartSeries> allChartSeries) {
		final List<String> timeZones = new ArrayList<String>();
		for (final MChartSeries series : allChartSeries) {
			String timeZone = series.getAgent().getTimeZone();
			if (!timeZones.contains(timeZone)) {
				timeZones.add(timeZone);
			}
		}
		return timeZones;
	}

	public static void initGeneralChartParameters(final QoSMessages messages) {
		if (!chartParametersInitialized) {
			final FormattedResultMessages resultMessages = new DefaultFormattedResultMessages(
					messages);
			final String decimalSeparator = LocaleInfo.getCurrentLocale()
					.getNumberConstants().decimalSeparator();
			final String groupingSeparator = LocaleInfo.getCurrentLocale()
					.getNumberConstants().groupingSeparator();
			ChartResultUtils.setLocalMessages(
					ChartResultUtils.getLocalMonths(messages),
					ChartResultUtils.getLocalMonthsShort(messages),
					ChartResultUtils.getLocalWeekdays(messages),
					messages.loading(), resultMessages.yes(),
					resultMessages.no());

			ChartResultUtils.setLocalDateTimeFormats(
					DateUtils.getJsDateTimeFormat(),
					DateUtils.getJsShortTimeFormat(),
					DateUtils.getJsFullTimeFormat());
			ChartResultUtils.setLocalNumberSeparators(decimalSeparator,
					groupingSeparator);
			ChartResultUtils.setMinTimeInterval(new Long(
					TimeConstants.MIN_TIME_INTERVAL_IN_CHART).doubleValue());
			chartParametersInitialized = true;
		}
	}

	public static boolean isUnitCompatible(final MChartSeries comparableSeries,
			final Collection<MChartSeries> allSeries,
			final boolean compareByType) {
		if (allSeries.isEmpty()) {
			return true;
		}
		boolean isTypesCompartible = true;
		for (final MChartSeries series : allSeries) {
			if (compareByType) {
				isTypesCompartible = series.getParameter().getType()
						.equals(comparableSeries.getParameter().getType());
			}
			return isTypesCompartible
					&& series.getParameter().getUnits()
							.equals(comparableSeries.getParameter().getUnits());
		}

		return false;
	}

	public static native void manageAutoscaling(String chartName,
			boolean singleSeriesChart, boolean autoscalingEnabled) /*-{
		$wnd.qosThresholdModule.manageAutoscaling(chartName, singleSeriesChart,
				autoscalingEnabled);
	}-*/;

	public static native void manageCaptions(String chartName,
			boolean captionsEnabled, boolean thresholdsEnabled) /*-{
		$wnd.qosChartModule.manageCaptions(chartName, captionsEnabled,
				thresholdsEnabled);
	}-*/;

	public static native void manageChartThresholdLines(String chartName,
			boolean thresholdsEnabled) /*-{
		$wnd.qosThresholdModule.manageChartThresholdLines(chartName,
				thresholdsEnabled);
	}-*/;

	public static native void removeChart(String chartName) /*-{
		$wnd.qosChartModule.clearIfExists(chartName);
	}-*/;

	public static native boolean removeSeries(String chartName, String seriesKey) /*-{
		return $wnd.qosChartModule.removeSeries(chartName, seriesKey);
	}-*/;

	public static native void renameChart(String oldName, String newName) /*-{
		$wnd.qosChartModule.renameChart(oldName, newName);
	}-*/;

	public static ChartType resolveChartType(
			final Collection<MChartSeries> series) {
		ChartType resultType = ChartType.UNSUPPORTED;;
		final int seriesSize = series.size();
		if (seriesSize != 0) {
			final ParameterType type = series.iterator().next().getParameter()
					.getType();

			switch (type) {
				case COUNTER :
					resultType = ChartType.COUNTER;
					break;
				case PERCENTAGE :
					resultType = (seriesSize == 1)
							? ChartType.PERCENTAGE_SINGLE
							: ChartType.PERCENTAGE_MULTIPLE;
					break;
				case BOOL :
					resultType = ChartType.BOOL;
					break;
				case LEVEL :
					resultType = (seriesSize == 1)
							? ChartType.LEVEL_SINGLE
							: ChartType.LEVEL_MULTIPLE;
					break;
				default :
					break;
			}
		}
		return resultType;
	}

	private static native void setLocalDateTimeFormats(
			final String dateTimeFormat, final String shortTimeFormat,
			final String fullTimeFormat)/*-{
		$wnd.Highcharts.dateFormats = {
			dateTimeFormat : dateTimeFormat,
			shortTimeFormat : shortTimeFormat,
			fullTimeFormat : fullTimeFormat
		};
	}-*/;

	private static native void setLocalMessages(JsArrayString months,
			JsArrayString monthsShort, JsArrayString weekdays, String loading,
			String yesMessage, String noMessage) /*-{
		$wnd.Highcharts.qosMessages = {
			yesMessage : yesMessage,
			noMessage : noMessage
		};
		$wnd.Highcharts.setOptions({
			lang : {
				months : months,
				weekdays : weekdays,
				shortMonths : monthsShort,
				loading : loading
			}
		});
	}-*/;

	private static native void setLocalNumberSeparators(
			final String decimalSeparator, final String groupingSeparator) /*-{
		$wnd.Highcharts.setOptions({
			lang : {
				decimalPoint : decimalSeparator,
				thousandsSep : groupingSeparator
			}
		});

	}-*/;

	private static native void setMinTimeInterval(double milliseconds) /*-{
		$wnd.qosChartModule.setMinTimeInterval(milliseconds);
	}-*/;

	public static native void setMouseTrackingEnabled(String chartName,
			boolean mouseTrackingEnabled) /*-{
		$wnd.qosChartModule.setMouseTrackingEnabled(chartName,
				mouseTrackingEnabled);
	}-*/;

	public static native void setSize(String chartName, int width, int height) /*-{
		$wnd.qosChartModule.setSize(chartName, width, height);
	}-*/;

	public static native void undoZoom(String chartName) /*-{
		$wnd.qosChartModule.undoZoom(chartName);
	}-*/;

	public static native void zoomChart(String chartName, double startDate,
			double endDate, String timeZone, String timeZoneType) /*-{
		$wnd.qosChartModule.zoomChart(chartName, startDate, endDate, timeZone,
				timeZoneType);
	}-*/;
}
