/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Embeddable
public class TimeInterval implements Serializable {

	public enum TimeZoneType {
		LOCAL, AGENT, CUSTOM
	}

	public enum Type {
		FIFTEEN_MINUTES(15*TimeConstants.MILLISECONDS_PER_MINUTE),
        HOUR(TimeConstants.MILLISECONDS_PER_HOUR),
        DAY(TimeConstants.MILLISECONDS_PER_DAY),
        DAYS(-1),
        WEEK(TimeConstants.MILLISECONDS_PER_WEEK),
        MONTH(31*TimeConstants.MILLISECONDS_PER_DAY),
        CUSTOM(-1);

        private final long millis;

        Type(final long millis) {
            this.millis = millis;
        }

        public long getMillis() {
            if(this == DAYS
               || this == CUSTOM) {
                throw new UnsupportedOperationException("Custom intervals don't have a fixed length in milliseconds");
            }
            return millis;
        }
    }

	@Deprecated
	public static TimeInterval day() {
		return new TimeInterval(Type.DAY, null, null, null, null);
	}

	@Deprecated
	public static TimeInterval days(final int unitCount) {
		return new TimeInterval(Type.DAY, unitCount);
	}

	public static TimeInterval get(final Date startDateTime,
			final Date endDateTime) {
		return new TimeInterval(Type.CUSTOM, startDateTime, endDateTime, null,
				null);
	}

	public static TimeInterval get(final Date startDateTime,
			final Date endDateTime, final String timeZone) {
		return new TimeInterval(Type.CUSTOM, startDateTime, endDateTime,
				timeZone, null);
	}

	public static TimeInterval get(final Date startDateTime,
			final Date endDateTime, final String timeZone,
			final String clientTimeZone) {
		return new TimeInterval(Type.CUSTOM, startDateTime, endDateTime,
				timeZone, clientTimeZone);
	}

	public static TimeInterval get(final TimeInterval timeInterval) {
		return new TimeInterval(timeInterval.getType(),
				timeInterval.getStartDateTime(), timeInterval.getEndDateTime(),
				timeInterval.getTimeZoneType(), timeInterval.getTimeZone(),
				timeInterval.getClientTimeZone());
	}

	public static TimeInterval get(final Type intervalType) {
		return new TimeInterval(intervalType, null, null, null, null, null);
	}

	public static TimeInterval get(final Type type, final Date startDateTime,
			final Date endDateTime, final TimeZoneType timeZoneType,
			final String timeZone, final String clientTimeZone) {
		return new TimeInterval(type, startDateTime, endDateTime, timeZoneType,
				timeZone, clientTimeZone);
	}

	public static Date getEndDate(final Date endDate, final Type intervalType) {
		Date outEndDate = endDate;
		if (intervalType != null) {
			switch (intervalType) {
				case FIFTEEN_MINUTES :
				case HOUR :
				case DAY :
				case WEEK :
				case MONTH :
					outEndDate = new Date(System.currentTimeMillis());
					break;
				case CUSTOM : {
					outEndDate = endDate;
					break;
				}
				default : {
					throw new UnsupportedOperationException("Interval type "
							+ intervalType + " not supported");
				}
			}
		}
		return outEndDate;
	}

	public static Date getStartDate(final Date startDate, final Date endDate,
			final Type intervalType) {
		Date outStartDate = startDate;
		if (endDate != null && intervalType != null) {
			switch (intervalType) {
                case FIFTEEN_MINUTES:
                case HOUR:
                case DAY:
                case WEEK:
                case MONTH: {
					outStartDate = new Date(endDate.getTime()
							- intervalType.getMillis());
					break;
				}
				case CUSTOM : {
					outStartDate = startDate;
					break;
				}
				default : {
					throw new UnsupportedOperationException("Interval type "
							+ intervalType + " not supported");
				}
			}
		}
		return outStartDate;
	}

	@Deprecated
	public static TimeInterval week() {
		return new TimeInterval(Type.WEEK, null, null, null, null);
	}

	private Date startDateTime;

	private Date endDateTime;

	@Enumerated(value = EnumType.STRING)
	private Type type;

	@Enumerated(value = EnumType.STRING)
	private TimeZoneType timeZoneType;

	@Transient
	private int unitCount;

	private String timeZone;

	private String clientTimeZone;

	/**
	 * Только для серализации
	 * 
	 * @see #get(Date, Date)
	 * @see #day()
	 * @see #week()
	 */
	@Deprecated
	public TimeInterval() {
		this(Type.DAY, null, null, null, null);
	}

	private TimeInterval(final Type type, final Date startDateTime,
			final Date endDateTime, final String timeZone,
			final String clientTimeZone) {
		super();
		this.type = type;
		this.endDateTime = endDateTime == null
				? getEndDate(endDateTime, type)
				: endDateTime;
		this.startDateTime = startDateTime == null ? getStartDate(
				startDateTime, this.endDateTime, type) : startDateTime;
		this.timeZone = timeZone;
		this.clientTimeZone = clientTimeZone;
	}

	private TimeInterval(final Type type, final Date startDateTime,
			final Date endDateTime, final TimeZoneType timeZoneType,
			final String timeZone, final String clientTimeZone) {
		this(type, startDateTime, endDateTime, timeZone, clientTimeZone);
		this.timeZoneType = timeZoneType;
	}

	private TimeInterval(final Type type, final int unitCount) {
		this.type = type;
		this.unitCount = unitCount;
	}

	/**
	 * @return the clientTimeZone
	 */
	public String getClientTimeZone() {
		return clientTimeZone;
	}

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @return the timeZoneType
	 */
	public TimeZoneType getTimeZoneType() {
		return timeZoneType;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the unitCount
	 */
	public int getUnitCount() {
		return unitCount;
	}

	/**
	 * Checks whether date is included in this interval
	 * 
	 * @param date
	 * @return
	 */
	public boolean isDateIncluded(final Date date) {
		return (startDateTime == null || (startDateTime != null && date
				.after(startDateTime)))
				&& (endDateTime == null || (endDateTime != null && date
						.before(endDateTime)));
	}

	/**
	 * Checks whether child interval is included in this interval.
	 * 
	 * @param child
	 * @param parent
	 */
	@Transient
	@JsonIgnore
	public boolean isIntervalIncluded(final TimeInterval child) {
		final Date childStart = child.getStartDateTime();
		final Date childEnd = child.getEndDateTime();
		return (childStart.after(startDateTime) || childStart
				.equals(startDateTime))
				&& (childEnd.before(endDateTime) || childEnd
						.equals(endDateTime));
	}

	@Transient
	@JsonIgnore
	public boolean isValid() {
		return (startDateTime != null) && (endDateTime != null)
				&& startDateTime.before(endDateTime);
	}

	/**
	 * @param endDateTime
	 *            the endDateTime to set
	 * @deprecated нужен только для серилизации
	 */
	@Deprecated
	public void setEndDateTime(final Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @param startDateTime
	 *            the startDateTime to set
	 * @deprecated нужен только для серилизации
	 */
	@Deprecated
	public void setStartDateTime(final Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @param type
	 *            the type to set
	 * @deprecated нужен только для серилизации
	 */
	@Deprecated
	public void setType(final Type type) {
		this.type = type;
	}

	/**
	 * @param unitCount
	 *            the unitCount to set
	 */
	@Deprecated
	public void setUnitCount(final int unitCount) {
		this.unitCount = unitCount;
	}
}
