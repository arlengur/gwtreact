/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

/**
 * @author abondin
 * 
 */
public class ColorConstants {

	public static String getAlertCleared() {
		return "#d3d3d3";
	}

	public static String getDashboardChartLegendBackgroundColor() {
		return "#FFF";
	}

	public static String getSeverityCritical() {
		return "#FF0000";
	}

	public static String getSeverityCriticalRgb() {
		return hex2Rgb(getSeverityCritical());
	}

	public static String getSeverityIndeterminate() {
		return "#000000";
	}

	public static String getSeverityIndeterminateRgb() {
		return hex2Rgb(getSeverityIndeterminate());
	}
	public static String getSeverityMajor() {
		return "#FF6600";
	}

	public static String getSeverityMajorRgb() {
		return hex2Rgb(getSeverityMajor());
	}
	public static String getSeverityMinor() {
		return "#998237";
	}
	public static String getSeverityMinorRgb() {
		return hex2Rgb(getSeverityMinor());
	}
	public static String getSeverityNone() {
		return "#00FF00";
	}
	public static String getSeverityNoneRgb() {
		return hex2Rgb(getSeverityNone());
	}
	public static String getSeverityNotice() {
		return "#B3B3B3";
	}
	public static String getSeverityNoticeRgb() {
		return hex2Rgb(getSeverityNotice());
	}
	public static String getSeverityWarning() {
		return "#D3A200";
	}
	public static String getSeverityWarningRgb() {
		return hex2Rgb(getSeverityWarning());
	}

	public static String hex2Rgb(final String colorStr) {
		return "" + Integer.valueOf(colorStr.substring(1, 3), 16) + ","
				+ Integer.valueOf(colorStr.substring(3, 5), 16) + ","
				+ Integer.valueOf(colorStr.substring(5, 7), 16);
	}
}
