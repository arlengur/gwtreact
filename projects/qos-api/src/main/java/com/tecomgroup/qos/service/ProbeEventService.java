package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.communication.request.AgentActionStatus;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;

import java.util.List;

/**
 * Created by uvarov.m on 16.01.2016.
 */
@RemoteServiceRelativePath("springServices/probeEventService")
public interface ProbeEventService extends Service,  RemoteService {

    public Long createProbeEvent(MProbeEvent event);

    public void removeProbeEventByKey(String key);

    public List<MProbeEvent> getLastEventsByUserAndType(final String login, final String type);
}
