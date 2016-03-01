/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.alert;

import java.util.Date;
import java.util.List;

import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.ChartType;

/**
 * Settings is used for creation of a chart
 * 
 * @author ivlev.e
 */
public final class ChartSettings {

	public static final class Builder {

		private String chartName;

		private ChartType chartType;

		private List<MChartSeries> series;

		private Date startDate;

		private Date endDate;

		private int timeZoneOffset;

		private String divElementId;

		private String lineType;

		private boolean autoscaling = true;

		private boolean thresholds = true;

		private boolean captions = false;

		private boolean mouseTracking = true;

		private boolean zoom = true;

        private boolean legend = true;

        public Builder() {
		}

		public Builder autoscaling(final boolean autoscaling) {
			this.autoscaling = autoscaling;
			return this;
		}

		public ChartSettings build() {
			final ChartSettings settings = new ChartSettings();
			settings.chartName = chartName;
			settings.chartType = chartType;
			settings.series = series;
			settings.startDate = startDate;
			settings.endDate = endDate;
			settings.timeZoneOffset = timeZoneOffset;
			settings.divElementId = divElementId;
			settings.lineType = lineType;
			settings.autoscaling = autoscaling;
			settings.thresholds = thresholds;
			settings.captions = captions;
			settings.mouseTracking = mouseTracking;
			settings.zoom = zoom;
            settings.legend = legend;
			return settings;
		}

		public Builder captions(final boolean captions) {
			this.captions = captions;
			return this;
		}

		public Builder chartName(final String chartName) {
			this.chartName = chartName;
			return this;
		}

		public Builder chartType(final ChartType chartType) {
			this.chartType = chartType;
			return this;
		}

		public Builder divElementId(final String divElementId) {
			this.divElementId = divElementId;
			return this;
		}

		public Builder endDate(final Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder endDate(final long endDate) {
			this.endDate = new Date(endDate);
			return this;
		}

		public Builder lineType(final String lineType) {
			this.lineType = lineType;
			return this;
		}

		public Builder mouseTracking(final boolean mouseTracking) {
			this.mouseTracking = mouseTracking;
			return this;
		}

		public Builder series(final List<MChartSeries> series) {
			this.series = series;
			return this;
		}

		public Builder startDate(final Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder startDate(final long startDate) {
			this.startDate = new Date(startDate);
			return this;
		}

		public Builder thresholds(final boolean thresholds) {
			this.thresholds = thresholds;
			return this;
		}

		public Builder timeZoneOffset(final int timeZoneOffset) {
			this.timeZoneOffset = timeZoneOffset;
			return this;
		}

		public Builder zoom(final boolean zoom) {
			this.zoom = zoom;
			return this;
		}

        public Builder legend(final boolean legend) {
            this.legend = legend;
            return this;
        }
	}

	private String chartName;

	private ChartType chartType;

	private List<MChartSeries> series;

	private Date startDate;

	private Date endDate;

	private int timeZoneOffset;

	private String divElementId;

	private String lineType;

	private boolean autoscaling;

	private boolean thresholds;

	private boolean captions;

	private boolean mouseTracking;

	private boolean zoom;

    private boolean legend;

	private ChartSettings() {
	}

	public String getChartName() {
		return chartName;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public String getDivElementId() {
		return divElementId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getLineType() {
		return lineType;
	}

	public List<MChartSeries> getSeries() {
		return series;
	}

	public Date getStartDate() {
		return startDate;
	}

	public int getTimeZoneOffset() {
		return timeZoneOffset;
	}

	public boolean isAutoscalingEnabled() {
		return autoscaling;
	}

	public boolean isCaptionsEnabled() {
		return captions;
	}

	public boolean isMouseTrackingEnabled() {
		return mouseTracking;
	}

	public boolean isThresholdsEnabled() {
		return thresholds;
	}

	public boolean isZoomEnabled() {
		return zoom;
	}

    public boolean isLegendEnabled() {
        return legend;
    }
}
