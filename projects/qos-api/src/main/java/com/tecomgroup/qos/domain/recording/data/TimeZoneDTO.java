package com.tecomgroup.qos.domain.recording.data;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by uvarov.m on 22.01.2016.
 */
public class TimeZoneDTO implements Comparable{
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("ZZ");
    private static DateTimeFormatter dateTimeFormatterNoColon = DateTimeFormat.forPattern("Z");

    public String id;
    public String description;
    public String offset;

    public TimeZoneDTO(String id, String offset, String description) {
        this.id = id;
        this.offset = offset;
        this.description = description;
    }

    public static TimeZoneDTO fromJodaTimeZone(DateTimeZone timeZone) {
        String offset = dateTimeFormatter.withZone(timeZone).print(0);
        StringBuffer description = new StringBuffer("UTC(")
                .append(offset)
                .append(") ")
                .append(timeZone.getID());
        return new TimeZoneDTO(timeZone.getID(), dateTimeFormatterNoColon.withZone(timeZone).print(0), description.toString());
    }

    private static String getTimeZoneOffsetString(TimeZone timeZone, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);

        String result = sdf.format(new Date());
        // WA for  "if the offset value from GMT is 0, "Z" is produced."
        if("Z".equals(result)) {
            return "+00:00";
        }
        return result;
    }

    public static TimeZoneDTO fromTimeZone(TimeZone timeZone) {
        String offset = getTimeZoneOffsetString(timeZone, "Z");
        StringBuffer description = new StringBuffer("UTC(")
                .append(getTimeZoneOffsetString(timeZone, "XXX"))
                .append(") ")
                .append(timeZone.getID());

        return new TimeZoneDTO(timeZone.getID(), offset, description.toString());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TimeZoneDTO) {
            TimeZoneDTO o1 = (TimeZoneDTO) o;
            return this.id.equals(o1.id);
        }

        return false;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof TimeZoneDTO) {
            return this.id.compareTo(((TimeZoneDTO) o).id);
        }
        return -1;
    }

    public static Comparator<TimeZoneDTO> TimeZoneComparator = new Comparator<TimeZoneDTO>() {
        @Override
        public int compare(TimeZoneDTO o1, TimeZoneDTO o2) {
            return o1.offset.compareTo(o2.offset);
        }
    };

    public static List<TimeZoneDTO> getSorted(Set<TimeZoneDTO> unsorted) {
        List<TimeZoneDTO> list = new LinkedList<>(unsorted);
        Collections.sort(list, TimeZoneComparator);
        return list;
    }
}
