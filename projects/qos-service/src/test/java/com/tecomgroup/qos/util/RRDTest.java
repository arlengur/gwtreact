/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.util;

import java.io.File;

import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;

/**
 * @author kunilov.p
 * 
 */
public class RRDTest {

	private static String STORAGE_HOME = "./target/rrdHome/";

	private static final long HOURS_PER_DAY = 24;

	private static final long MINUTES_PER_HOUR = 60;

	private static final long SECONDS_PER_MINUTE = 60;

	private static final long SECONDS_PER_DAY = SECONDS_PER_MINUTE
			* MINUTES_PER_HOUR * HOURS_PER_DAY;

	private static final String CONFIGURATION_TYPE = "GAUGE";

	private static final Double MAX_VALUE = Double.NaN;

	private static final Double MIN_VALUE = Double.NaN;

	private static final String RRD_EXTENSION = ".rrd";

	private static long castDateTimeForRRD(final long dateTime,
			final long samplingRate) {
		Long seconds = (dateTime + 500L) / 1000L;
		seconds = (seconds / samplingRate) * samplingRate;
		return seconds;
	}

	private RrdDb rrdDb;
	// //////////////////////////////////////////////////////////
	// Settings to configure rrdDb
	// //////////////////////////////////////////////////////////
	private final long samplingRate = 1;
	private final long heartbeat = 2 * samplingRate;
	private final long startDateTime = 1369748048000l;
	private final short version = 1;
	private final double xff = 0.999d;
	private final String datasourceName = "test";
	private String rrdLocation = STORAGE_HOME + datasourceName + RRD_EXTENSION;
	// /////////////////////////////////////////////////////////
	// Settings to configure data to write
	// //////////////////////////////////////////////////////////
	private long data = 1;
	private final long dataCount = 10;
	private final long dataRate = 3;
	// /////////////////////////////////////////////////////////
	private final short aggregationMultipliedFactor = 2;
	private final short storedDaysCount = 30;

	private void createAggregations(final RrdDef rrdDef,
			final ConsolFun archiveAggregationType, final Long samplingRate) {
		long division = storedDaysCount * SECONDS_PER_DAY;
		final int delimiter = aggregationMultipliedFactor;
		int iterationIndex = 0;
		while (division > delimiter) {
			final int steps = (int) Math.pow(delimiter, iterationIndex);
			rrdDef.addArchive(archiveAggregationType, xff, steps,
					(int) (Math.ceil(division / (double) samplingRate + 1)));
			iterationIndex++;
			division /= delimiter;
		}
	}

	private void createFile(final String location) throws Exception {
		final File file = new File(location);
		if (!file.exists()) {
			(new File(STORAGE_HOME)).mkdirs();
			file.createNewFile();
		}
	}

	private void createStorage() throws Exception {
		rrdLocation = new File(rrdLocation).getCanonicalPath();
		try {
			final RrdDef rrdDef = new RrdDef(rrdLocation, castDateTimeForRRD(
					startDateTime, samplingRate), samplingRate, version);

			rrdDef.addDatasource(datasourceName,
					DsType.valueOf(CONFIGURATION_TYPE), heartbeat, MAX_VALUE,
					MIN_VALUE);

			createAggregations(rrdDef, ConsolFun.MIN, samplingRate);

			removeFile(rrdLocation);
			createFile(rrdLocation);
			rrdDb = new RrdDb(rrdDef);
		} catch (final Exception ex) {
			throw new Exception("Unable to create storage: " + rrdLocation, ex);
		}
	}

	private void releaseStorage() {
		try {
			rrdDb.close();
		} catch (final Exception ex) {
			// ignore
		}
	}

	private void removeFile(final String location) throws Exception {
		final File file = new File(location);
		if (file.exists()) {
			file.delete();
		}
	}

	// @Before
	public void setUp() throws Exception {
		RrdDb.setDefaultFactory("FILE");
		createStorage();
	}

	// @After
	public void tearDown() {
		releaseStorage();
	}

	// @Test
	public void testWriteToRRD() throws Exception {
		System.out.println("rrdtool "
				+ rrdDb.getRrdDef().dump().replace("--version " + version, ""));

		final long castedStartDateTime = castDateTimeForRRD(startDateTime,
				samplingRate);
		for (int index = 1; index <= dataCount; index += dataRate) {
			try {
				final Sample sample = rrdDb.createSample();
				sample.setTime(castedStartDateTime + samplingRate * index);
				sample.setValue(datasourceName, data++);
				System.out.println("rrdtool " + sample.dump());
				sample.update();
			} catch (final Exception ex) {
				throw new Exception("Unable to write to rrd: " + rrdLocation,
						ex);
			}
		}
	}
}
