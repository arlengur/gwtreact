package com.tecomgroup.qos.communication.message;

/**
 * Created by stroganov.d on 20.05.2015.
 */
public class ConfigUpdate extends  AgentAction {
    private String configuration;


    public ConfigUpdate(String configuration) {
        super();
        this.configuration = configuration;
    }

    public ConfigUpdate(String user, String configuration) {
        super(user);
        this.configuration = configuration;
    }

    public ConfigUpdate() {
        super();
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
