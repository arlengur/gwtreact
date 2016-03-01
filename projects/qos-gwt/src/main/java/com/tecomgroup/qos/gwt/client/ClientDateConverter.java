/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;

import java.util.Date;

/**
 * @author kunilov.p
 * 
 */
public class ClientDateConverter {

	public static Date convertClientDateTimeToTimeZone(
			final Date clientDateTime, final String targetTimeZone) {
		final Date convertedDateTime = convertDateToTimeZone(
				convertDateToUTCUsingTimeZone(clientDateTime,
						DateUtils.getCurrentTimeZoneAsString()), targetTimeZone);

		return convertedDateTime;
	}

	public static Date convertDateToTimeZone(final Date dateTime,
			final String timeZone) {
		Date result = dateTime;
		if (timeZone != null && !timeZone.isEmpty()) {
			result = new Date(dateTime.getTime() + getTimeZoneOffset(timeZone));
		}
		return result;
	}

	public static Date convertDateToUTCUsingTimeZone(final Date dateTime,
			final String timeZone) {
		Date result = dateTime;
		if (timeZone != null && !timeZone.isEmpty()) {
			result = new Date(dateTime.getTime() - getTimeZoneOffset(timeZone));
		}
		return result;
	}

	private static int getTimeZoneOffset(final String timeZone) {
		final TimeZoneWrapper serverTimeZone = DateUtils.getServerTimeZones()
				.get(timeZone);
		final int timeZoneOffset;
		if (serverTimeZone != null) {
			timeZoneOffset = serverTimeZone.getOffset();
		} else {
			timeZoneOffset = DateUtils.getTimeZoneOffset(timeZone)
					* TimeConstants.MILLISECONDS_PER_MINUTE;
		}
		return timeZoneOffset;
	}
}
