package com.tecomgroup.qos.domain.recording.data;

import com.tecomgroup.qos.domain.recording.Event;

import java.io.Serializable;

/**
 * Created by uvarov.m on 14.01.2016.
 */
public class EventDTO implements Serializable {
    public Long order;
    public String begin;
    public String end;
    public String comment;

    public static EventDTO fromEntity(Event entity) {
        EventDTO dto = new EventDTO();
        dto.begin = entity.getStartDateTime();
        dto.end = entity.getEndDateTime();
        dto.order = 0L;
        dto.comment = entity.getComment();
        return dto;
    }

    public static Event toEntity(EventDTO dto) {
        Event entity = new Event();
        entity.setStartDateTime(dto.begin);
        entity.setEndDateTime(dto.end);
        entity.setComment(dto.comment);
        return entity;
    }
}
