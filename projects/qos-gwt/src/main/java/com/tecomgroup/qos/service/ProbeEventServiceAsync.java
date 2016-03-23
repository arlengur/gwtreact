package com.tecomgroup.qos.service;

/**
 * Created by uvarov.m on 04.03.2016.
 */

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import java.util.List;

public interface ProbeEventServiceAsync {

    public void createProbeEvent(MProbeEvent event, AsyncCallback<Long> async);

    public void removeProbeEventByKey(String key, AsyncCallback<Void> async);

    public void getLastEventsByUserAndType(final String login, final String type, AsyncCallback<List<MProbeEvent>> async);
}
