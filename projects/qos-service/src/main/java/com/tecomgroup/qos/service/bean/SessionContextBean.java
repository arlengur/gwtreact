package com.tecomgroup.qos.service.bean;

/**
 * Created by stroganov.d on 17.03.2015.
 */
public interface SessionContextBean {

    public Object getContextAttribute(String key);
    public void setContextAttribute(String key,Object object);
    public void dropTaskConfigurationContext();
    public void dropContext();
}
