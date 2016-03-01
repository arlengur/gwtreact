package com.tecomgroup.qos.service.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by stroganov.d on 17.03.2015.
 */

public class ChannelViewSessionContext implements SessionContextBean{
    public static final String TASKS_CONFIGURATION_SESSION_KEY ="TASKS_CONFIGURATION";
    public static final String CURRENT_MUSER_SESSION_KEY ="CURRENT_MUSER";
    private Map<String,Object> context=new ConcurrentHashMap();

    @Override
    public Object getContextAttribute(String key) {
       return context.get(key);
    }

    @Override
    public void setContextAttribute(String key, Object object) {
        context.put(key, object);
    }

    @Override
    public void dropTaskConfigurationContext() {
        context.remove(TASKS_CONFIGURATION_SESSION_KEY);
    }

    @Override
    public void dropContext() {
        context.clear();
    }
}
