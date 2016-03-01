package com.tecomgroup.qos.communication.message;

import java.util.UUID;

/**
 * Created by stroganov.d on 20.05.2015.
 */
public class AgentAction {
    public String uuid;
    public String user;
    public long initiationTime;

    public AgentAction() {
        uuid= UUID.randomUUID().toString();
        initiationTime=System.currentTimeMillis();
    }

    public AgentAction(String user) {
        this();
        this.user=user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getInitiationTime() {
        return initiationTime;
    }

    public void setInitiationTime(long initiationTime) {
        this.initiationTime = initiationTime;
    }
}
