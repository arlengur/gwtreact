package com.tecomgroup.qos.domain.recording.data;

import com.tecomgroup.qos.domain.recording.Event;
import com.tecomgroup.qos.domain.recording.Schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uvarov.m on 14.01.2016.
 */
public class ScheduleDTO implements Serializable {
    public String name;
    public String timeZone;
    public String agentKey;
    public String taskKey;
    public List<EventDTO> eventList;

    public static ScheduleDTO fromEntity(Schedule entity) {
        ScheduleDTO dto = new ScheduleDTO();
        if(entity == null) return dto;

        dto.agentKey = entity.getAgentKey();
        dto.taskKey = entity.getTaskKey();
        dto.name = entity.getName();
        dto.timeZone = entity.getTimeZone();
        if(entity.getEventList() != null) {
            dto.eventList = new ArrayList<>();
            for (Event event : entity.getEventList()) {
                dto.eventList.add(EventDTO.fromEntity(event));
            }
        }
        return dto;
    }

    public static Schedule toEntity(ScheduleDTO dto) {
        Schedule entity = new Schedule();
        entity.setName(dto.name);
        entity.setTimeZone(dto.timeZone);
        entity.setAgentKey(dto.agentKey);
        entity.setTaskKey(dto.taskKey);
        entity.setEventList(new ArrayList<Event>());
        for(EventDTO eventDTO: dto.eventList) {
            Event event = EventDTO.toEntity(eventDTO);
            entity.getEventList().add(event);
        }
        return entity;
    }
}
