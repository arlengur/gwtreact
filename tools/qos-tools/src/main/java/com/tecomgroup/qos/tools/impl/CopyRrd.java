/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.ResultConfigurationSettings;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.RrdUtil;
import com.tecomgroup.qos.util.Utils;

/**
 * @author kunilov.p
 * 
 */
@Component
public class CopyRrd implements QoSTool, InitializingBean {
	private static void copyAggregations(final RrdDef sourceDef,
			final RrdDef targetDef) {
		final ArcDef[] sourceArchiveDefs = sourceDef.getArcDefs();
		for (int i = 0; i < sourceArchiveDefs.length; ++i) {
			targetDef.addArchive(sourceArchiveDefs[i]);
		}

	}

	public static RrdDb createStorage(final String datasourceName,
			final RrdDef sourceDef, final String filePath,
			final long samplingRate, final long heartbeat, final int version,
			final Date startDateTime, final int storedDaysCount,
			final boolean createAggregations) {
		RrdDb rrdDb = null;
		try {
			final RrdDef rrdDef = new RrdDef(filePath, RrdUtil.castTimeForRRD(
					startDateTime, samplingRate), samplingRate, version);

			rrdDef.addDatasource(datasourceName, DsType
					.valueOf(ResultConfigurationSettings.CONFIGURATION_TYPE),
					RrdUtil.getHeartbeat(heartbeat),
					ResultConfigurationSettings.MAX_VALUE,
					ResultConfigurationSettings.MIN_VALUE);

			if (createAggregations) {
				RrdUtil.createAggregations(
						rrdDef,
						ConsolFun.MIN,
						samplingRate,
						ResultConfigurationSettings.AGGREGATION_MULTIPLIED_FACTOR,
						storedDaysCount);
			} else {
				copyAggregations(sourceDef, rrdDef);
			}

			removeStorageDestinationFile(filePath);
			createStorageDestinationFile(filePath);
			rrdDb = new RrdDb(rrdDef);
		} catch (final Exception ex) {
			throw new ServiceException("Unable to create storage: " + filePath,
					ex);
		}
		return rrdDb;
	}

	public static void createStorageDestinationFile(final String location) {
		final File file = new File(location);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (final Exception ex) {
				throw new ServiceException("Unable to create new file: "
						+ location, ex);
			}
		}
	}

	public static RrdDb openStorage(final String filePath) {
		RrdDb rrdDb = null;
		try {
			rrdDb = new RrdDb(filePath);
		} catch (final Exception ex) {
			throw new ServiceException("Unable to open storage: " + filePath,
					ex);
		}
		return rrdDb;
	}

	public static void releaseStorage(final RrdDb rrdDb) {
		try {
			rrdDb.close();
		} catch (final Exception ex) {
			// ignore
		}
	}
	public static void removeStorageDestinationFile(final String location) {
		final File file = new File(location);
		if (file.exists()) {
			if (!file.delete()) {
				throw new ServiceException("Unable to remove file: " + location);
			}
		}
	}

	@Value("${rrd.copy.source}")
	private String sourceRrdPath;

	@Value("${rrd.copy.target}")
	private String targetRrdPath;

	@Value("${rrd.copy.sampling.rate}")
	private Long samplingRate;
	@Value("${rrd.copy.stored.days.count}")
	private Integer storedDaysCount;

	@Value("${rrd.copy.batch.size}")
	private Integer batchSize;

	@Value("${rrd.backend.factory}")
	private String rrdBackendFactory;

	private static Logger LOGGER = Logger.getLogger(CopyRrd.class);

	@Override
	public void afterPropertiesSet() throws Exception {
		RrdDb.setDefaultFactory(rrdBackendFactory);
	}

	private void copyStorage(final Long samplingRate,
			final String targetFilePath, final String sourceFilePath,
			final Integer storedDaysCount) {
		String sourceStorageFilePath = sourceFilePath;
		String targetStorageFilePath = targetFilePath;
		if (targetStorageFilePath == null || targetStorageFilePath.isEmpty()) {
			targetStorageFilePath = sourceStorageFilePath;
			sourceStorageFilePath += ".old";
		}

		RrdDb sourceRrdDb = null;
		RrdDb targetRrdDb = null;
		try {
			if (!sourceFilePath.equals(sourceStorageFilePath)) {
				Utils.moveFile(sourceFilePath, sourceStorageFilePath);
			}
			sourceRrdDb = openStorage(sourceStorageFilePath);
			final String datasourceName = RrdUtil.getDatasourceName(
					sourceRrdDb, 0);
			final Long sourceStorageStartTimeInSeconds = RrdUtil
					.findDatasourceStartTime(sourceRrdDb, datasourceName,
							batchSize);

			Long currentSamplingRate = samplingRate;
			if (currentSamplingRate == null) {
				currentSamplingRate = RrdUtil.getSamplingRate(sourceRrdDb);
			}

			final Long heartbeat = RrdUtil.getDatasourceHeartbeat(sourceRrdDb,
					0);
			final RrdDef sourceRrdDef = sourceRrdDb.getRrdDef();

			targetRrdDb = createStorage(datasourceName, sourceRrdDef,
					targetStorageFilePath, currentSamplingRate, heartbeat, 1,
					new Date(sourceStorageStartTimeInSeconds
							* TimeConstants.MILLISECONDS_PER_SECOND),
					storedDaysCount, true);

			RrdUtil.copyRrdData(datasourceName, sourceRrdDb, targetRrdDb,
					sourceStorageStartTimeInSeconds, batchSize);
		} catch (final Exception ex) {
			throw new ServiceException("Unable to copy RRD data from "
					+ targetFilePath + " to " + sourceFilePath, ex);
		} finally {
			releaseStorage(sourceRrdDb);
			releaseStorage(targetRrdDb);
		}
	}

	@Override
	public void execute() {
		if (sourceRrdPath == null || sourceRrdPath.isEmpty()) {
			throw new IllegalArgumentException("Source file not specified");
		}
		if (!(new File(sourceRrdPath).exists())) {
			throw new IllegalArgumentException("Source file not exists");
		}
		if (targetRrdPath != null && !targetRrdPath.isEmpty()) {
			if ((new File(targetRrdPath).exists())) {
				removeStorageDestinationFile(targetRrdPath);
			} else {
				createStorageDestinationFile(targetRrdPath);
			}
		}
		execute();
		LOGGER.info("done");
	}

	/**
	 * Copy RRD with general options
	 * 
	 * @param sourcePath
	 * @param targetPath
	 */
	public void execute(final String sourcePath, final String targetPath) {
		try {
			copyStorage(samplingRate, targetPath, sourcePath, storedDaysCount);
		} catch (final Exception ex) {
			LOGGER.error("failed", ex);
		}
	}

	@Override
	public String getDescription() {
		return "Copy rrd file";
	}

}
