/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.modelspace.jdbc.dao.probestatus;

import com.tecomgroup.qos.domain.probestatus.MEventProperty;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.domain.recording.Schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by uvarov.m on 27.10.2015.
 */
public interface ProbeEventServiceDao {

    Long createProbeEvent(MProbeEvent event);

    void updateEvent(MProbeEvent event, MProbeEvent.STATUS state, Date timestamp, List<MEventProperty> newProps);

    Long removeProbeEvent(Long id);

    void removeProbeEventByKey(String key);

    List<MProbeEvent> getEventsByKey(final String eventKey);

    MProbeEvent getLastEventByKey(final String eventKey);

    List<MProbeEvent> getEventsByAgent(final String agentKey);

    List<MProbeEvent> getEventsByUser(final String login);

    List<MProbeEvent> getEventsByProperty(String eventType, String propertyKey, String propertyValue);

    List<MProbeEvent> getEventsByUserAndType(final String login, final String type);

    List<MProbeEvent> getLastEventsByUserAndType(final String login, final String type);
}
