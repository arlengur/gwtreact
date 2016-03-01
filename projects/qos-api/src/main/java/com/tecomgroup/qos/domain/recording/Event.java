package com.tecomgroup.qos.domain.recording;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by uvarov.m on 11.01.2016.
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "mrecording_schedule_event_list")
@XmlRootElement
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id")
    private Long id;

    @Column(name = "eventlist_id")
    private Long parentId;

    @Column(name = "start_date", nullable = false)
    private String startDateTime;

    @Column(name = "end_date")
    private String endDateTime;

    @Column(name = "comment")
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object != null && object instanceof Event)
        {
            Event o = (Event) object;
            return this.getStartDateTime().equals(o.getStartDateTime()) &&
                    this.getEndDateTime().equals(o.getEndDateTime()) &&
                    ((this.getComment() == null && o.getComment() == null) ||
                            (this.getComment() != null && this.getComment().equals(o.getComment()))) ;
        }

        return false;
    }

    public static Event copy(Event from) {
        Event a = new Event();
        a.setId(from.getId() == null? null : from.getId().longValue());
        a.setStartDateTime(from.getStartDateTime());
        a.setEndDateTime(from.getEndDateTime());
        a.setParentId(from.getParentId() == null? null : from.getParentId().longValue());
        a.setComment(from.getComment());
        return a;
    }
}
