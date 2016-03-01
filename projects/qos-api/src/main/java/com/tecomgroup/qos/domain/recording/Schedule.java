package com.tecomgroup.qos.domain.recording;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by uvarov.m on 11.01.2016.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "mrecording_schedule")
@XmlRootElement
public class Schedule {

    public enum Type{CYCLIC, SCHEDULED, READY_TO_RUN}

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    private String name;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "agent_key")
    private String agentKey;

    @Column(name="task_key")
    private String taskKey;

    @ElementCollection
    @CollectionTable(name="mrecording_schedule_event_list", joinColumns=@JoinColumn(name="id"))
    private List<Event> eventList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public String getAgentKey() {
        return agentKey;
    }

    public void setAgentKey(String agentKey) {
        this.agentKey = agentKey;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object != null && object instanceof Schedule)
        {
            Schedule o = (Schedule) object;

            return  ((this.getName() == null && o.getName() == null) || (this.getName() != null && this.getName().equals(o.getName()))) &&
                    ((this.getTimeZone() == null && o.getTimeZone() == null) || (this.getTimeZone() != null && this.getTimeZone().equals(o.getTimeZone()))) &&
                    ((this.getAgentKey() == null && o.getAgentKey() == null) || (this.getAgentKey() != null && this.getAgentKey().equals(o.getAgentKey()))) &&
                    ((this.getTaskKey() == null && o.getTaskKey() == null) || (this.getTaskKey() != null && this.getTaskKey().equals(o.getTaskKey()))) &&
                    ((this.getEventList() == null && o.getEventList() == null) ||
                            (this.getEventList() != null &&
                             o.getEventList() != null &&
                                    this.getEventList().containsAll(o.getEventList()) &&
                                    o.getEventList().containsAll(this.getEventList())));
        }

        return false;
    }

    public static Schedule copy(Schedule from) {
        Schedule a = new Schedule();
        a.setId(from.getId() == null? null : from.getId().longValue());
        a.setName(from.getName());
        a.setAgentKey(from.getAgentKey());
        a.setTaskKey(from.getTaskKey());
        a.setTimeZone(from.getTimeZone());
        a.setEventList(new ArrayList<Event>());
        for(Event fromEvent: from.getEventList()) {
            a.getEventList().add(Event.copy(fromEvent));
        }
        return a;
    }
}
