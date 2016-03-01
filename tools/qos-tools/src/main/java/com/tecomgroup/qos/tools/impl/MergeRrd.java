/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.RrdUtil;

/**
 * The tool to merge two rrd files to new one. </br>Files must have the same
 * sampling rate, the same datasource and the same aggregation type. One of the
 * source files is primary. If primary rrd file contains NaN values, then the
 * values with the same timestamps will be used from secondary rrd file
 * 
 * @author kshnyakin.m
 * 
 */
@Component
public class MergeRrd implements QoSTool {
	@Value("${rrd.merge.source.primary}")
	private String primaryRrdPath;
	@Value("${rrd.merge.source.secondary}")
	private String secondaryRrdPath;
	@Value("${rrd.merge.target}")
	private String targetRrdPath;
	@Value("${rrd.copy.batch.size}")
	private Integer batchSize;

	private static Logger LOGGER = Logger.getLogger(MergeRrd.class);

	@Override
	public void execute() {
		try {
			Assert.isTrue(!primaryRrdPath.isEmpty(),
					"Primary file path is empty");
			Assert.isTrue(!secondaryRrdPath.isEmpty(),
					"Secondary file path is empty");
			Assert.isTrue(!targetRrdPath.isEmpty(), "Target file path is empty");
			Assert.isTrue(new File(primaryRrdPath).exists(),
					"Primary source file not exist");
			Assert.isTrue(new File(secondaryRrdPath).exists(),
					"Secondary source file not exist");
			LOGGER.info("Merge is started");
			mergeStorages(primaryRrdPath, secondaryRrdPath, targetRrdPath);
			LOGGER.info("Merge was done");
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
	}

	@Override
	public String getDescription() {
		return "Merge two rrd files to new one" + "\nSupported VM arguments:"
				+ "\n\trrd.merge.source.primary - Path of primary rrd file"
				+ "\n\trrd.merge.source.secondary - Path of secondary rrd file"
				+ "\n\trrd.merge.target - Path of target rrd file";
	}

	private void mergeRrdData(final String datasourceName,
			final RrdDb primaryRrdDb, final RrdDb secondaryRrdDb,
			final RrdDb targetRrdDb, final Long primaryDbStartTime,
			final Long secondaryDbStartTime, final int batchSize) {
		try {
			final Map<Long, Double> mergedData = new TreeMap<Long, Double>();
			final Set<Long> nanPrimaryDbTimestamps = new LinkedHashSet<Long>();
			final Long samplingRate = RrdUtil.getSamplingRate(primaryRrdDb);
			final ConsolFun aggregationType = RrdUtil.getAggregationType(
					primaryRrdDb, samplingRate);
			final Long primaryDbEndTime = RrdUtil.getArcEndTime(primaryRrdDb,
					samplingRate);

			processPrimaryDbData(datasourceName, primaryRrdDb, aggregationType,
					samplingRate, primaryDbStartTime, primaryDbEndTime,
					batchSize, mergedData, nanPrimaryDbTimestamps);

			final Long secondaryDbEndTime = RrdUtil.getArcEndTime(
					secondaryRrdDb, samplingRate);

			processSecondaryDb(datasourceName, primaryRrdDb, secondaryRrdDb,
					aggregationType, samplingRate, primaryDbStartTime,
					primaryDbEndTime, secondaryDbStartTime, secondaryDbEndTime,
					batchSize, mergedData, nanPrimaryDbTimestamps);

			if (!mergedData.isEmpty()) {
				final Sample sample = targetRrdDb.createSample();
				for (final Map.Entry<Long, Double> dataEntry : mergedData
						.entrySet()) {
					sample.setTime(dataEntry.getKey());
					sample.setValue(datasourceName, dataEntry.getValue());
					sample.update();
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
	}

	private void mergeStorages(final String primaryFilePath,
			final String secondaryFilePath, final String targetFilePath) {
		RrdDb primaryRrdDb = null;
		RrdDb secondaryRrdDb = null;
		RrdDb targetRrdDb = null;
		try {
			LOGGER.info("Opennig source storages");
			primaryRrdDb = CopyRrd.openStorage(primaryFilePath);
			secondaryRrdDb = CopyRrd.openStorage(secondaryFilePath);
			LOGGER.info("Storages was opened");
			if (validateDatabases(primaryRrdDb, secondaryRrdDb)) {
				final Long samplingRate = RrdUtil.getSamplingRate(primaryRrdDb);
				final String datasourceName = RrdUtil.getDatasourceName(
						primaryRrdDb, 0);
				final Long primaryStorageStartTime = RrdUtil
						.findDatasourceStartTime(primaryRrdDb, datasourceName,
								batchSize);
				final Long secondaryStorageStartTime = RrdUtil
						.findDatasourceStartTime(secondaryRrdDb,
								datasourceName, batchSize);
				final Long targetStorageStartTime = Math.min(
						primaryStorageStartTime, secondaryStorageStartTime);
				final Long heartbeat = RrdUtil.getDatasourceHeartbeat(
						primaryRrdDb, 0);
				final RrdDef primaryRrdDef = primaryRrdDb.getRrdDef();
				LOGGER.info("Creating target storage");
				targetRrdDb = CopyRrd.createStorage(datasourceName,
						primaryRrdDef, targetFilePath, samplingRate, heartbeat,
						2, new Date(targetStorageStartTime
								* TimeConstants.MILLISECONDS_PER_SECOND), 0,
						false);
				LOGGER.info("Target storage was created: "
						+ new File(targetFilePath).getAbsolutePath());
				LOGGER.info("Starting data merge");
				mergeRrdData(datasourceName, primaryRrdDb, secondaryRrdDb,
						targetRrdDb, primaryStorageStartTime,
						secondaryStorageStartTime, batchSize);
				LOGGER.info("Merge was finished");
			} else {
				LOGGER.error("Databases have different parameters!");
			}
		} catch (final Exception ex) {
			LOGGER.error("Merge was failed", ex);
			new ServiceException(ex);
		} finally {
			CopyRrd.releaseStorage(primaryRrdDb);
			CopyRrd.releaseStorage(secondaryRrdDb);
			CopyRrd.releaseStorage(targetRrdDb);
		}
	}

	private void processPrimaryDbBatch(final String datasourceName,
			final RrdDb primaryRrdDb, final ConsolFun aggregationType,
			final Long samplingRate, final Long startTime, final Long endTime,
			final Map<Long, Double> mergedData,
			final Set<Long> nanDataTimestamps) {
		try {
			final FetchRequest fetchRequest = primaryRrdDb.createFetchRequest(
					aggregationType, startTime, endTime, samplingRate);
			final FetchData fetchData = fetchRequest.fetchData();
			if (fetchData.getRowCount() > 0) {
				int index = 0;
				final long[] timestamps = fetchData.getTimestamps();
				final double[] values = fetchData.getValues(datasourceName);
				for (final double value : values) {
					if (!Double.isNaN(value)) {
						mergedData.put(timestamps[index], value);
					} else {
						nanDataTimestamps.add(timestamps[index]);
					}
					index++;
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("Primary DB data processing was failed", ex);
			throw new ServiceException(ex);
		}

	}

	private void processPrimaryDbData(final String datasourceName,
			final RrdDb primaryRrdDb, final ConsolFun aggregationType,
			final Long samplingRate, final Long startTime, final Long endTime,
			final int batchSize, final Map<Long, Double> resultMap,
			final Set<Long> nanDataTimestamps) {
		try {
			if (endTime - startTime > batchSize) {
				final int batchCount = (int) ((endTime - startTime) / batchSize);

				for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
					final Long batchStartTime = startTime + batchIndex
							* batchSize;
					final Long batchEndTime = startTime + (batchIndex + 1)
							* batchSize - samplingRate;
					processPrimaryDbBatch(datasourceName, primaryRrdDb,
							aggregationType, samplingRate, batchStartTime,
							batchEndTime, resultMap, nanDataTimestamps);
				}
				processPrimaryDbBatch(datasourceName, primaryRrdDb,
						aggregationType, samplingRate, startTime + batchCount
								* batchSize, endTime, resultMap,
						nanDataTimestamps);
			} else {
				processPrimaryDbBatch(datasourceName, primaryRrdDb,
						aggregationType, samplingRate, startTime, endTime,
						resultMap, nanDataTimestamps);
			}
		} catch (final Exception ex) {
			LOGGER.error("Primary DB data processing was failed", ex);
			throw new ServiceException(ex);
		}
	}

	private void processSecondaryDb(final String datasourceName,
			final RrdDb primaryRrdDb, final RrdDb secondaryRrdDb,
			final ConsolFun aggregationType, final Long samplingRate,
			final Long primaryDbStartTime, final Long primaryDbEndTime,
			final Long secondaryDbStartTime, final Long secondaryDbEndTime,
			final int batchSize, final Map<Long, Double> mergedData,
			final Set<Long> nanPrimaryDbTimestamps) {
		if (secondaryDbEndTime < primaryDbStartTime
				|| secondaryDbStartTime > primaryDbEndTime) {
			processSecondaryDbData(datasourceName, secondaryRrdDb,
					aggregationType, samplingRate, secondaryDbStartTime,
					secondaryDbEndTime, batchSize, primaryDbStartTime,
					primaryDbEndTime, mergedData, nanPrimaryDbTimestamps);
		} else {
			if (secondaryDbStartTime < primaryDbStartTime) {
				processSecondaryDbData(datasourceName, secondaryRrdDb,
						aggregationType, samplingRate, secondaryDbStartTime,
						primaryDbStartTime - samplingRate, batchSize,
						primaryDbStartTime, primaryDbEndTime, mergedData,
						nanPrimaryDbTimestamps);
			}
			if (secondaryDbEndTime > primaryDbEndTime) {
				processSecondaryDbData(datasourceName, secondaryRrdDb,
						aggregationType, samplingRate, primaryDbEndTime
								+ samplingRate, secondaryDbEndTime, batchSize,
						primaryDbStartTime, primaryDbEndTime, mergedData,
						nanPrimaryDbTimestamps);
			}
			if (!nanPrimaryDbTimestamps.isEmpty()) {
				final Long startTime = (Long) nanPrimaryDbTimestamps.toArray()[0];
				final Long endTime = (Long) nanPrimaryDbTimestamps.toArray()[nanPrimaryDbTimestamps
						.size() - 1];
				processSecondaryDbData(datasourceName, secondaryRrdDb,
						aggregationType, samplingRate, startTime, endTime,
						batchSize, primaryDbStartTime, primaryDbEndTime,
						mergedData, nanPrimaryDbTimestamps);
			}
		}
	}

	private void processSecondaryDbBatch(final String datasourceName,
			final RrdDb secondaryRrdDb, final ConsolFun aggregationType,
			final Long samplingRate, final Long startTime, final Long endTime,
			final Long primaryDbStartTime, final Long primaryDbEndTime,
			final Map<Long, Double> mergedData,
			final Set<Long> nanPrimaryDbTimestamps) {
		try {
			final FetchRequest fetchRequest = secondaryRrdDb
					.createFetchRequest(aggregationType, startTime, endTime,
							samplingRate);
			final FetchData fetchData = fetchRequest.fetchData();
			if (fetchData.getRowCount() > 0) {
				int index = 0;
				final long[] timestamps = fetchData.getTimestamps();
				final double[] values = fetchData.getValues(datasourceName);
				for (final long timestamp : timestamps) {
					if ((timestamp < primaryDbStartTime)
							|| (timestamp > primaryDbEndTime)
							|| nanPrimaryDbTimestamps.contains(timestamp)) {
						mergedData.put(timestamp, values[index]);
					}
					index++;
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
	}

	private void processSecondaryDbData(final String datasourceName,
			final RrdDb secondaryRrdDb, final ConsolFun aggregationType,
			final Long samplingRate, final Long secondaryDbStartTime,
			final Long secondaryDbEndTime, final int batchSize,
			final Long primaryDbStartTime, final Long primaryDbEndTime,
			final Map<Long, Double> mergedData,
			final Set<Long> nanPrimaryDbTimestamps) {
		try {
			if (secondaryDbEndTime - secondaryDbStartTime > batchSize) {
				final int batchCount = (int) ((secondaryDbEndTime - secondaryDbStartTime) / batchSize);

				for (int batchIndex = 0; batchIndex < batchCount; batchIndex++) {
					final Long batchStartTime = secondaryDbStartTime
							+ batchIndex * batchSize;
					final Long batchEndTime = secondaryDbStartTime
							+ (batchIndex + 1) * batchSize - samplingRate;
					processSecondaryDbBatch(datasourceName, secondaryRrdDb,
							aggregationType, samplingRate, batchStartTime,
							batchEndTime, primaryDbStartTime, primaryDbEndTime,
							mergedData, nanPrimaryDbTimestamps);
				}
				processSecondaryDbBatch(datasourceName, secondaryRrdDb,
						aggregationType, samplingRate, secondaryDbStartTime
								+ batchCount * batchSize, secondaryDbEndTime,
						primaryDbStartTime, primaryDbEndTime, mergedData,
						nanPrimaryDbTimestamps);
			} else {
				processSecondaryDbBatch(datasourceName, secondaryRrdDb,
						aggregationType, samplingRate, secondaryDbStartTime,
						secondaryDbEndTime, primaryDbStartTime,
						primaryDbEndTime, mergedData, nanPrimaryDbTimestamps);
			}
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
	}

	private boolean validateAggregationType(final RrdDb primaryRrdDb,
			final RrdDb secondaryRrdDb) {
		boolean validate = false;
		final long samplingRate = RrdUtil.getSamplingRate(primaryRrdDb);
		if (RrdUtil.getAggregationType(primaryRrdDb, samplingRate).equals(
				RrdUtil.getAggregationType(secondaryRrdDb, samplingRate))) {
			validate = true;
		}
		return validate;
	}

	private boolean validateDatabases(final RrdDb primaryRrdDb,
			final RrdDb secondaryRrdDb) {
		return validateSamplingRate(primaryRrdDb, secondaryRrdDb)
				&& validateDatasource(primaryRrdDb, secondaryRrdDb)
				&& validateAggregationType(primaryRrdDb, secondaryRrdDb);
	}

	private boolean validateDatasource(final RrdDb primaryRrdDb,
			final RrdDb secondaryRrdDb) {
		boolean validate = false;
		if ((primaryRrdDb.getDsCount() == 1)
				&& (secondaryRrdDb.getDsCount() == 1)) {
			if (RrdUtil.getDatasourceName(primaryRrdDb, 0).equals(
					RrdUtil.getDatasourceName(secondaryRrdDb, 0))) {
				validate = true;
			}
		}
		return validate;
	}

	private boolean validateSamplingRate(final RrdDb primaryRrdDb,
			final RrdDb secondaryRrdDb) {
		return RrdUtil.getSamplingRate(primaryRrdDb).equals(
				RrdUtil.getSamplingRate(secondaryRrdDb));
	}

}
