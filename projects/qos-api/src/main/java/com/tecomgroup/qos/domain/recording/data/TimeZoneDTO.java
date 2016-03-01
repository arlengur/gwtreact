package com.tecomgroup.qos.domain.recording.data;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;


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
