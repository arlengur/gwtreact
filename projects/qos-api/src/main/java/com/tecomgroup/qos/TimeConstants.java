/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

/**
 * @author ivlev.e
 * 
 */
public interface TimeConstants {

	public static final short DAYS_PER_WEEK = 7;

	public static final short HOURS_PER_DAY = 24;

	public static final short MINUTES_PER_HOUR = 60;

	public static final short SECONDS_PER_MINUTE = 60;

	public static final short MILLISECONDS_PER_SECOND = 1000;

	public static final int MILLISECONDS_PER_MINUTE = MILLISECONDS_PER_SECOND
			* SECONDS_PER_MINUTE;

	public static final long MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR
			* SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;

	public static final long MILLISECONDS_PER_DAY = MILLISECONDS_PER_HOUR
			* HOURS_PER_DAY;

	public static final long MILLISECONDS_PER_WEEK = MILLISECONDS_PER_DAY
			* DAYS_PER_WEEK;

	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE
			* MINUTES_PER_HOUR;

	public static final long SECONDS_PER_DAY = SECONDS_PER_MINUTE
			* MINUTES_PER_HOUR * HOURS_PER_DAY;

	public static final long MIN_TIME_INTERVAL_IN_CHART = 5 * MILLISECONDS_PER_MINUTE;

	public static final int FILTER_UPDATE_DELAY = 2 * MILLISECONDS_PER_SECOND;
}
