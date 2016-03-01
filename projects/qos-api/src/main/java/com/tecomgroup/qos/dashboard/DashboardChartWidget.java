package com.tecomgroup.qos.dashboard;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.ChartType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;

/**
 * @author smyshlyaev.s
 */
public class DashboardChartWidget extends DashboardWidget
		implements
			HasUpdatableData<MChartSeries> {

	public static class ChartSeriesData implements Serializable {
		public static ChartSeriesData fromMChartSeries(
				final MChartSeries chartSeries) {
			final String chartName = chartSeries.getChartName();
			final MAgentTask task = chartSeries.getTask();
			final Long taskId = task == null ? null : task.getId();
			final MResultParameterConfiguration parameter = chartSeries
					.getParameter();
			final Long parameterId = parameter == null ? null : parameter
					.getId();
			return new ChartSeriesData(chartSeries.getId(), chartName,
					parameterId, taskId);
		}

		private static final long serialVersionUID = -6865702254793088298L;
		private Long id;
		private String chartName;
		private Long paramId;

		private Long taskId;

		public ChartSeriesData() {
			super();
		}

		public ChartSeriesData(final Long id, final String chartName,
				final Long paramId, final Long taskId) {
			this();
			this.id = id;
			this.chartName = chartName;
			this.paramId = paramId;
			this.taskId = taskId;
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			final ChartSeriesData that = (ChartSeriesData) o;

			if (chartName != null
					? !chartName.equals(that.chartName)
					: that.chartName != null) {
				return false;
			}
			if (paramId != null
					? !paramId.equals(that.paramId)
					: that.paramId != null) {
				return false;
			}
			if (taskId != null
					? !taskId.equals(that.taskId)
					: that.taskId != null) {
				return false;
			}

			return true;
		}

		public String getChartName() {
			return chartName;
		}

		public Long getId() {
			return id;
		}

		public Long getParamId() {
			return paramId;
		}

		public Long getTaskId() {
			return taskId;
		}

		@Override
		public int hashCode() {
			int result = chartName != null ? chartName.hashCode() : 0;
			result = 31 * result + (paramId != null ? paramId.hashCode() : 0);
			result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
			return result;
		}

		public void setChartName(final String chartName) {
			this.chartName = chartName;
		}

		public void setId(final Long id) {
			this.id = id;
		}

		public void setParamId(final Long paramId) {
			this.paramId = paramId;
		}

		public void setTaskId(final Long taskId) {
			this.taskId = taskId;
		}

		@Override
		public String toString() {
			return ChartSeriesData.class.getName() + "{" + "id=" + id
					+ ", chartName='" + chartName + '\'' + ", paramId="
					+ paramId + ", taskId=" + taskId + '}';
		}
	}

	private static final long serialVersionUID = 5660583924251352270L;
	private static final transient Comparator<ChartSeriesData> SERIES_COMPARATOR = new Comparator<ChartSeriesData>() {
		@Override
		public int compare(final ChartSeriesData o1, final ChartSeriesData o2) {
			final int result;
			if (o1 == o2) {
				result = 0;
			} else {
				final int nameCompare = compareNullable(o1.getChartName(),
						o2.getChartName());
				final int taskIdCompare = compareNullable(o1.getTaskId(),
						o2.getTaskId());
				final int parameterIdCompare = compareNullable(o1.getParamId(),
						o2.getParamId());

				if (nameCompare != 0) {
					result = nameCompare;
				} else if (taskIdCompare != 0) {
					result = taskIdCompare;
				} else {
					result = parameterIdCompare;
				}
			}
			return result;
		}

		private <T extends Comparable<T>> int compareNullable(final T o1,
				final T o2) {
			final int result;
			if (o1 != null && o2 != null) {
				result = o1.compareTo(o2);
			} else if (o1 == null && o2 == null) {
				result = 0;
			} else if (o1 == null) {
				result = -1;
			} else {
				result = 1;
			}
			return result;
		}
	};

	private boolean autoscalingEnabled;
	private boolean thresholdsEnabled;
	private boolean legendEnabled;
	private TimeInterval.Type intervalType;
	private String chartName;
	private ChartType chartType;
	private String timezone;
	private TimeInterval.TimeZoneType timezoneType;
	private String lineType;
	private List<ChartSeriesData> seriesData;

	@Transient
	@JsonIgnore
	private List<MChartSeries> series;

	public String getChartName() {
		return chartName;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public TimeInterval.Type getIntervalType() {
		return intervalType;
	}

	@Override
	public String getKey() {
		final StringBuilder result = new StringBuilder()
				.append(DashboardChartWidget.class.getName()).append("{")
				.append("chartName=").append(chartName)
				.append("; intervalType=").append(intervalType)
				.append("; seriesData={");
		Collections.sort(seriesData, SERIES_COMPARATOR);
		for (final ChartSeriesData chartSeries : seriesData) {
			result.append(ChartSeriesData.class.getName()).append("{")
					.append("taskId=").append(chartSeries.getTaskId())
					.append("parameterId=").append(chartSeries.getParamId())
					.append("}");
		}
		result.append("}}");
		return result.toString();
	}

	public String getLineType() {
		return lineType;
	}

	public List<MChartSeries> getSeries() {
		return series;
	}

	public List<ChartSeriesData> getSeriesData() {
		return seriesData;
	}

	public String getTimezone() {
		return timezone;
	}

	public TimeInterval.TimeZoneType getTimezoneType() {
		return timezoneType;
	}

	public boolean isAutoscalingEnabled() {
		return autoscalingEnabled;
	}

	@Override
	public boolean isEmpty() {
		return seriesData == null || seriesData.isEmpty();
	}

	public boolean isLegendEnabled() {
		return legendEnabled;
	}

	public boolean isThresholdsEnabled() {
		return thresholdsEnabled;
	}

	public void setAutoscalingEnabled(final boolean autoscalingEnabled) {
		this.autoscalingEnabled = autoscalingEnabled;
	}

	public void setChartName(final String chartName) {
		this.chartName = chartName;
	}

	public void setChartType(final ChartType chartType) {
		this.chartType = chartType;
	}

	public void setIntervalType(final TimeInterval.Type intervalType) {
		this.intervalType = intervalType;
	}

	public void setLegendEnabled(final boolean legendEnabled) {
		this.legendEnabled = legendEnabled;
	}

	public void setLineType(final String lineType) {
		this.lineType = lineType;
	}

	public void setSeries(final List<MChartSeries> series) {
		this.series = series;
	}

	public void setSeriesData(final List<ChartSeriesData> seriesData) {
		this.seriesData = seriesData;
	}

	public void setThresholdsEnabled(final boolean thresholdsEnabled) {
		this.thresholdsEnabled = thresholdsEnabled;
	}

	public void setTimezone(final String timezone) {
		this.timezone = timezone;
	}

	public void setTimezoneType(final TimeInterval.TimeZoneType timezoneType) {
		this.timezoneType = timezoneType;
	}

}
