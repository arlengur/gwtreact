/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.dashboard;

/**
 * @author sviyazov.a
 */
public class DashboardBitrateWidget extends DashboardWidget {

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Interval for data update requests in seconds
     */
    private int updateInterval;
    /**
     * Capacity
     */
    private int capacity;

    private String taskKey;

    @Override
    public String getKey() {
        return DashboardBitrateWidget.class.getName()
                + ": { updateInterval: "
                + updateInterval + " , capacity: "
                + capacity + " , taskKey: "
                + taskKey
                + " } ";
    }
}
