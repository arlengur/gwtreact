package com.tecomgroup.qos.communication.message;

/**
 * Created by stroganov.d on 23.06.2015.
 */
public class SwUpgrade extends AgentAction{
    private String sw_location;

    public SwUpgrade(String user, String sw_location) {
        super(user);
        this.sw_location = sw_location;
    }

    public String getSw_location() {
        return sw_location;
    }

    public void setSw_location(String sw_location) {
        this.sw_location = sw_location;
    }
}