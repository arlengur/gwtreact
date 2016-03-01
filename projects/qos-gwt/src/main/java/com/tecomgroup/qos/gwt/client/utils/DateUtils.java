/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ivlev.e
 *
 */
public class DateUtils {

	static {
		final DateTimeFormatInfo info = LocaleInfo.getCurrentLocale()
				.getDateTimeFormatInfo();

		DATE_TIME_FORMAT = info.dateTimeMedium(info.timeFormat(),
				info.dateFormat());
		SHORT_TIME_FORMATTER = DateTimeFormat.getFormat(info.timeFormatShort());
	}

	public static final NumberFormat TWO_DIGEST_FORMAT = NumberFormat
			.getFormat("00");
	public static final String DATE_TIME_FORMAT;
	public static final String JS_RU_DATE_TIME_FORMAT = "%A, %b %e, %Y %H:%M:%S";
	public static final String JS_RU_SHORT_TIME_FORMAT = "%H:%M:%S";
	public static final String JS_RU_FULL_TIME_FORMAT = "%H:%M:%S";

	public static final String JS_EN_DATE_TIME_FORMAT = "%A, %b %e, %Y %I:%M:%S %p";
	public static final String JS_EN_SHORT_TIME_FORMAT = "%I:%M %p";
	public static final String JS_EN_FULL_TIME_FORMAT = "%I:%M:%S %p";

	public static final DateTimeFormat DATE_TIME_FORMATTER = DateTimeFormat
			.getFormat(DATE_TIME_FORMAT);

	public static final DateTimeFormat SHORT_TIME_FORMATTER;

	private static Map<String, TimeZoneWrapper> serverTimeZones;

	private static Map<String, TimeZone> clientTimeZones;

	public static TimeZoneWrapper createLocalTimeZone() {
		return new TimeZoneWrapper(DateUtils.getCurrentTimeZoneAsString(),
				AppUtils.getMessages().timeLocal(),
				DateUtils.getCurrentTimeZoneOffset()
						* TimeConstants.MILLISECONDS_PER_MINUTE);
	}

	public static String formatDuration(Long duration,
			final QoSMessages messages) {
		final Long days = duration / TimeConstants.MILLISECONDS_PER_DAY;
		duration = duration % TimeConstants.MILLISECONDS_PER_DAY;
		final Long hours = duration / TimeConstants.MILLISECONDS_PER_HOUR;
		duration = duration % TimeConstants.MILLISECONDS_PER_HOUR;
		final Long minutes = duration / TimeConstants.MILLISECONDS_PER_MINUTE;
		duration = duration % TimeConstants.MILLISECONDS_PER_MINUTE;
		final Long seconds = duration / TimeConstants.MILLISECONDS_PER_SECOND;
		final StringBuilder builder = new StringBuilder();
		builder.append(messages.days(days.intValue()));
		builder.append(" ");

		// doesn't display 00:00:00 if the duration is equal to number of days
		if (days == 0
				|| (days != 0 && (hours != 0 || minutes != 0 || seconds != 0))) {
			builder.append(TWO_DIGEST_FORMAT.format(hours));
			builder.append(":");
			builder.append(TWO_DIGEST_FORMAT.format(minutes));
			builder.append(":");
			builder.append(TWO_DIGEST_FORMAT.format(seconds));
		}

		return builder.toString();
	}

	/**
	 * Gets client time zones as unmodifiable map.
	 *
	 * @return
	 */
	public static Map<String, TimeZone> getClientTimeZones() {
		return clientTimeZones;
	}

	/**
	 * @return local client time zone.
	 */
	public static TimeZone getCurrentTimeZone() {
		return TimeZone.createTimeZone(-DateUtils.getCurrentTimeZoneOffset());
	}

	/**
	 * @return local client time zone name.
	 */
	public static String getCurrentTimeZoneAsString() {
		return getCurrentTimeZone().getID();
	}

	/**
	 *
	 * @return offset of the local time zone in minutes
	 */
	public static native int getCurrentTimeZoneOffset() /*-{
		return -(new Date().getTimezoneOffset());
	}-*/;

	public static String getJsDateTimeFormat() {
		final String localeName = getLocale();
		String result;
		if (localeName == "ru" || localeName == "ru_RU") {
			result = JS_RU_DATE_TIME_FORMAT;
		} else {
			result = JS_EN_DATE_TIME_FORMAT;
		}

		return result;
	}

	public static String getJsFullTimeFormat() {
		final String localeName = getLocale();
		String result;
		if (localeName == "ru" || localeName == "ru_RU") {
			result = JS_RU_FULL_TIME_FORMAT;
		} else {
			result = JS_EN_FULL_TIME_FORMAT;
		}

		return result;
	}

	public static String getJsShortTimeFormat() {
		final String localeName = getLocale();
		String result;
		if (localeName == "ru" || localeName == "ru_RU") {
			result = JS_RU_SHORT_TIME_FORMAT;
		} else {
			result = JS_EN_SHORT_TIME_FORMAT;
		}

		return result;
	}

	public static String getLocale() {
		return LocaleInfo.getCurrentLocale().getLocaleName();
	}

	/**
	 * Gets server time zones as unmodifiable map.
	 *
	 * @return
	 */
	public static Map<String, TimeZoneWrapper> getServerTimeZones() {
		return serverTimeZones;
	}

	/**
	 * Gets time zone offset in minutes.
	 *
	 * @param timeZone
	 * @return
	 */
	public static native int getTimeZoneOffset(String timeZone) /*-{
		var dt = new $wnd.timezoneJS.Date(new Date(), timeZone);
		return -dt.getTimezoneOffset();
	}-*/;

	public static void initializeClientTimeZones(
			final List<TimeZoneWrapper> serverTimeZones) {
		final Map<String, TimeZone> clientTimeZones = new LinkedHashMap<String, TimeZone>();
		// add local time zone
		final TimeZone localTimeZone = TimeZone.createTimeZone(-DateUtils
				.getCurrentTimeZoneOffset());
		clientTimeZones.put(DateUtils.getCurrentTimeZoneAsString(),
				localTimeZone);
		// add server time zones
		for (final TimeZoneWrapper serverTimeZone : serverTimeZones) {
			final TimeZone timeZoneObject = TimeZone
					.createTimeZone(-(serverTimeZone.getOffset() / TimeConstants.MILLISECONDS_PER_MINUTE));
			if (timeZoneObject != null) {
				clientTimeZones.put(serverTimeZone.getTimeZoneId(),
						timeZoneObject);
			}
		}

		DateUtils.clientTimeZones = Collections
				.<String, TimeZone> unmodifiableMap(clientTimeZones);
	}

	public static void initializeServerTimeZones(
			final List<TimeZoneWrapper> serverTimeZones) {
		final Map<String, TimeZoneWrapper> serverTimeZonesMap = new LinkedHashMap<String, TimeZoneWrapper>();
		for (final TimeZoneWrapper serverTimeZone : serverTimeZones) {
			serverTimeZonesMap.put(serverTimeZone.getTimeZoneId(),
					serverTimeZone);
		}

		DateUtils.serverTimeZones = Collections
				.unmodifiableMap(serverTimeZonesMap);
	}
}
