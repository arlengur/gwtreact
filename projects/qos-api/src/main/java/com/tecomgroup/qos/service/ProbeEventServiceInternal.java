package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.communication.request.AgentActionStatus;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;

import java.util.List;

/**
 * Created by uvarov.m on 16.01.2016.
 */

public interface ProbeEventServiceInternal extends Service,  RemoteService {

    public Long removeProbeEvent(Long id);

    public List<MProbeEvent> getEventsByKey(final String eventKey);

    public List<MProbeEvent> getEventsByAgent(final String agentKey);

    public List<MProbeEvent> getEventsByUser(final String login);

    public List<MProbeEvent> getEventsByProperty(String eventType, String propertyKey, String propertyValue);

    public List<MProbeEvent> getEventsByUserAndType(final String login, final String type);

    public void updateEvent(AgentActionStatus status);
}
