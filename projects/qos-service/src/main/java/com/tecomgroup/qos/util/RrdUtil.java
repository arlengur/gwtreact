package com.tecomgroup.qos.util;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.Datasource;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

import com.tecomgroup.qos.ResultConfigurationSettings;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.exception.ServiceException;

public class RrdUtil {

	private static Logger LOGGER = Logger.getLogger(RrdUtil.class);

	private static final String DEFAULT_DATASOURCE = "datasource";

	public static final Double START_OF_DATA = Double.NEGATIVE_INFINITY;

	public static final Double END_OF_DATA = Double.POSITIVE_INFINITY;

	/**
	 * Calculates aggregation factor satisfying the following condition
	 * aggregationBase ^ x <= dividend < aggregationBase ^ (x+1)
	 * 
	 * @param dividend
	 * @param aggregationBase
	 * @return x, so aggregationBase ^ x <= dividend < aggregationBase ^ (x+1)
	 */
	public static long calcAggregationFactor(final double dividend,
			final int aggregationBase) {
		int resultFactor = 0;
		double currentDividend = dividend;
		while (currentDividend >= aggregationBase) {
			currentDividend = Math.floor(currentDividend / aggregationBase);
			resultFactor++;
		}
		return resultFactor;
	}

	/**
	 * Calculates aggregation step using
	 * {@link #calcAggregationFactor(double, int)}. Aggragation step is result
	 * of power-mode function with aggregationBase.
	 * 
	 * @param dividend
	 * @param aggregationBase
	 * @return aggregationStep = aggregationBase ^ aggregationFactor
	 */
	public static long calcAggregationStep(final double dividend,
			final int aggregationBase) {
		final long aggregationFactor = calcAggregationFactor(dividend,
				aggregationBase);
		return (long) Math.pow(aggregationBase, aggregationFactor);
	}

	public static double calcXff(final int storedDaysCount,
			final int aggregationBase) {
		final long maxAggregationStep = RrdUtil.calcAggregationStep(
				storedDaysCount * TimeConstants.SECONDS_PER_DAY,
				aggregationBase);

		return (maxAggregationStep - 1) / (double) maxAggregationStep;
	}

	public static Long castTimeForRRD(final Date time, final Long shiftTime) {
		final Date actualTime = time == null ? new Date() : time;
		final Long seconds = (actualTime.getTime() + 500L) / 1000L;
		return shiftTime == null ? seconds : (seconds - shiftTime);
	}

	public static Long castTimeToSamplingRate(final Long seconds,
			final Long samplingRate) {

		return seconds - (seconds % samplingRate);
	}

	private static void copyRrdBatch(final String datasourceName,
			final RrdDb sourceRrdDb, final RrdDb targetRrdDb,
			final ConsolFun aggregationType, final Long aggregationStep,
			final Long startTime, final Long endTime) {
		try {
			final FetchRequest fetchRequest = sourceRrdDb.createFetchRequest(
					aggregationType, startTime, endTime, aggregationStep);
			final FetchData fetchData = fetchRequest.fetchData();
			if (fetchData.getRowCount() > 0) {
				final Map<Long, Double> data = preProcessData(fetchData,
						DEFAULT_DATASOURCE);
				if (data != null && !data.isEmpty()) {
					final Sample sample = targetRrdDb.createSample();
					for (final Map.Entry<Long, Double> dataEntry : data
							.entrySet()) {
						sample.setTime(dataEntry.getKey());
						sample.setValue(DEFAULT_DATASOURCE, dataEntry.getValue());
						sample.update();
					}
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
	}

	public static void copyRrdData(final String datasourceName,
			final RrdDb sourceRrdDb, final RrdDb targetRrdDb,
			final Long startTime, final int batchSize) {
		try {
			final Long samplingRate = RrdUtil.getSamplingRate(sourceRrdDb);
			final ConsolFun aggregationType = RrdUtil.getAggregationType(
					sourceRrdDb, samplingRate);
			final Long endTime = RrdUtil.getArcEndTime(sourceRrdDb,
					samplingRate);

			if (endTime - startTime > batchSize) {
				final int batchCount = (int) ((endTime - startTime) / batchSize);

				for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
					final Long batchStartTime = startTime + batchIndex
							* batchSize;
					final Long batchEndTime = startTime + (batchIndex + 1)
							* batchSize - samplingRate;
					RrdUtil.copyRrdBatch(DEFAULT_DATASOURCE, sourceRrdDb,
							targetRrdDb, aggregationType, samplingRate,
							batchStartTime, batchEndTime);
				}
				RrdUtil.copyRrdBatch(DEFAULT_DATASOURCE, sourceRrdDb, targetRrdDb,
						aggregationType, samplingRate, startTime + batchCount
								* batchSize, endTime);
			} else {
				RrdUtil.copyRrdBatch(DEFAULT_DATASOURCE, sourceRrdDb, targetRrdDb,
						aggregationType, samplingRate, startTime, endTime);
			}
		} catch (final Exception ex) {
			throw new ServiceException("Unable to get data for oldRrdDb "
					+ sourceRrdDb.getPath(), ex);
		}
	}

	public static void createAggregations(final RrdDef rrdDef,
			final ConsolFun archiveAggregationType, final long samplingRate,
			final int aggregationBase, final int storedDaysCount) {
		long division = storedDaysCount * TimeConstants.SECONDS_PER_DAY;
		final int delimiter = aggregationBase;
		int iterationIndex = 0;
		final double xff = RrdUtil.calcXff(storedDaysCount, aggregationBase);
		while (division > delimiter) {
			final int steps = (int) Math.pow(delimiter, iterationIndex);
			rrdDef.addArchive(archiveAggregationType, xff, steps,
					(int) (Math.ceil(division / (double) samplingRate + 1)));
			iterationIndex++;
			division /= delimiter;
		}
	}

	/**
	 * @return standard results file name without extension
	 */
	public static String createExportResultsFileName(
			final String startDateTime, final String endDateTime,
			final String timeZone, final boolean rawData) {
		String currentTimeZone = "";
		if (SimpleUtils.validateTimeZone(timeZone)) {
			currentTimeZone = " " + timeZone;
		}
		String rawDataString = "";
		if (rawData) {
			rawDataString = "raw data ";
		}
		return "Export results " + rawDataString + "(" + startDateTime + " - "
				+ endDateTime + currentTimeZone + ")";
	}

	public static Long findDatasourceStartTime(final RrdDb rrdDb,
			final String datasourceName, final int batchSize) {
		Long dataStartTime = null;
		try {
			final Long samplingRate = RrdUtil.getSamplingRate(rrdDb);
			final ConsolFun aggregationType = getAggregationType(rrdDb,
					samplingRate);
			final Long startTime = RrdUtil.getArcStartTime(rrdDb, samplingRate);
			final Long endTime = RrdUtil.getArcEndTime(rrdDb, samplingRate);

			if (endTime - startTime > batchSize) {
				final int batchCount = (int) ((endTime - startTime) / batchSize);

				for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
					final Long batchStartTime = startTime + batchIndex
							* batchSize;
					final Long batchEndTime = startTime + (batchIndex + 1)
							* batchSize - samplingRate;
					dataStartTime = RrdUtil.findDatasourceStartTime(
							DEFAULT_DATASOURCE, rrdDb, aggregationType,
							samplingRate, batchStartTime, batchEndTime);
					if (dataStartTime != null) {
						break;
					}
				}
				if (dataStartTime == null) {
					dataStartTime = RrdUtil.findDatasourceStartTime(
							DEFAULT_DATASOURCE, rrdDb, aggregationType,
							samplingRate, startTime + batchCount * batchSize,
							endTime);
				}
			} else {
				dataStartTime = RrdUtil.findDatasourceStartTime(DEFAULT_DATASOURCE,
						rrdDb, aggregationType, samplingRate, startTime,
						endTime);
			}

			if (dataStartTime == null) {
				dataStartTime = startTime;
			}
		} catch (final Exception ex) {
			throw new ServiceException(
					"Unable to find data startTime for rrdDb "
							+ rrdDb.getPath(), ex);
		}
		return dataStartTime;
	}

	private static Long findDatasourceStartTime(final String datasourceName,
			final RrdDb rrdDb, final ConsolFun aggregationType,
			final Long aggregationStep, final Long startTime, final Long endTime) {
		Long dataStartTime = null;
		try {
			final FetchRequest fetchRequest = rrdDb.createFetchRequest(
					aggregationType, startTime, endTime, aggregationStep);
			final FetchData fetchData = fetchRequest.fetchData();
			if (fetchData.getRowCount() > 0) {
				final double[] values = fetchRequest.fetchData().getValues(
						DEFAULT_DATASOURCE);
				int index = 0;
				for (final double value : values) {
					if (!Double.isNaN(value)) {
						dataStartTime = fetchData.getTimestamps()[index];
						break;
					}
					index++;
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
		return dataStartTime;
	}

	private static Long getAggregationStep(final RrdDb rrdDb, final int arcIndex) {
		Long aggregationStep = null;
		try {
			aggregationStep = rrdDb.getArchive(arcIndex).getArcStep();
		} catch (final Exception ex) {
			LOGGER.error("Unable to get aggregationStep for arcIndex = "
					+ arcIndex + " for rrdDb " + rrdDb.getPath(), ex);
		}
		return aggregationStep;
	}

	private static ConsolFun getAggregationType(final RrdDb rrdDb,
			final int arcIndex) {
		ConsolFun aggregationType = null;
		try {
			aggregationType = rrdDb.getArchive(arcIndex).getConsolFun();
		} catch (final Exception ex) {
			LOGGER.error("Unable to get aggregationStep for arcIndex = "
					+ arcIndex + " for rrdDb " + rrdDb.getPath(), ex);
		}
		return aggregationType;
	}

	public static ConsolFun getAggregationType(final RrdDb rrdDb,
			final Long aggregationStep) {
		ConsolFun aggregationType = null;
		for (int arcIndex = 0; arcIndex < rrdDb.getArcCount(); arcIndex++) {
			final Long arcStep = RrdUtil.getAggregationStep(rrdDb, arcIndex);
			if (arcStep.equals(aggregationStep)) {
				aggregationType = RrdUtil.getAggregationType(rrdDb, arcIndex);
				break;
			}
		}
		return aggregationType;
	}

	public static Long getArcEndTime(final RrdDb rrdDb,
			final Long aggregationStep) {
		Long endTime = null;
		try {
			for (int arcIndex = 0; arcIndex < rrdDb.getArcCount(); arcIndex++) {
				final Long arcStep = RrdUtil
						.getAggregationStep(rrdDb, arcIndex);
				if (arcStep.equals(aggregationStep)) {
					endTime = rrdDb.getArchive(arcIndex).getEndTime();
					break;
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("Unable to get endTime for aggregationStep: "
					+ aggregationStep + " for rrdDB " + rrdDb.getPath(), ex);
		}
		return endTime;
	}

	public static Long getArcStartTime(final RrdDb rrdDb,
			final Long aggregationStep) {
		Long startTime = null;
		try {
			for (int arcIndex = 0; arcIndex < rrdDb.getArcCount(); arcIndex++) {
				final Long arcStep = RrdUtil
						.getAggregationStep(rrdDb, arcIndex);
				if (arcStep.equals(aggregationStep)) {
					startTime = rrdDb.getArchive(arcIndex).getStartTime();
					break;
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("Unable to get endTime for aggregationStep: "
					+ aggregationStep + " for rrdDB " + rrdDb.getPath(), ex);
		}
		return startTime;
	}

	public static Long getDatasourceHeartbeat(final RrdDb rrdDb,
			final int datasourceIndex) {
		Long datasourceHeartbeat = null;
		try {
			final Datasource datasource = rrdDb.getDatasource(datasourceIndex);
			if (datasource != null) {
				datasourceHeartbeat = datasource.getHeartbeat();
			}
		} catch (final Exception ex) {
			LOGGER.error("Unable to get heartBeat for rrdDb "
					+ rrdDb.getPath());
		}
		return datasourceHeartbeat;
	}

	public static String getDatasourceName(final RrdDb rrdDb,
			final int datasourceIndex) {
		String datasourceName = null;
		try {
			final Datasource datasource = rrdDb.getDatasource(datasourceIndex);
			if (datasource != null) {
				datasourceName = datasource.getName();
			}
		} catch (final Exception ex) {
			LOGGER.error("Unable to get datasource name for rrdDb "
					+ rrdDb.getPath());
		}
		return datasourceName;
	}

	public static long getHeartbeat(final long heartbeat) {
		return heartbeat >= ResultConfigurationSettings.MIN_HEARTBEAT
				? heartbeat
				: ResultConfigurationSettings.MIN_HEARTBEAT;
	}

	public static Long getSamplingRate(final RrdDb rrdDb) {
		Long samplingRate = null;
		try {
			samplingRate = rrdDb.getHeader().getStep();
		} catch (final Exception ex) {
			LOGGER.error("Unable to get samplingRate for rrdDb "
					+ rrdDb.getPath());
		}
		return samplingRate;
	}

	private static Map<Long, Double> preProcessData(final FetchData fetchData,
			final String datasourceName) {
		Map<Long, Double> data = null;
		int index = 0;
		final double[] values = fetchData.getValues(DEFAULT_DATASOURCE);
		long[] timestamps = null;
		for (final double value : values) {
			if (!Double.isNaN(value)) {
				if (timestamps == null) {
					timestamps = fetchData.getTimestamps();
				}
				if (data == null) {
					data = new TreeMap<Long, Double>();
				}
				data.put(timestamps[index], value);
			}
			index++;
		}
		return data;
	}

}
