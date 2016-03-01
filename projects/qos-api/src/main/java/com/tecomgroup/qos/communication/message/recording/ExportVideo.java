package com.tecomgroup.qos.communication.message.recording;

import java.io.Serializable;

/**
 * @author galin.a on 11.01.2016.
 */
public class ExportVideo implements Serializable {
    private String user;
    private String taskId;
    private String taskDisplayName;
    private String quality;
    private String startDate;
    private long duration;
    private boolean immediate;
    private String exportLink;

    public ExportVideo() {
    }

    public ExportVideo(String user, String taskId, String taskDisplayName, String quality, String startDate, long duration, String exportLink, boolean immediate) {
        this.user = user;
        this.taskId = taskId;
        this.taskDisplayName = taskDisplayName;
        this.quality = quality;
        this.startDate = startDate;
        this.duration = duration;
        this.exportLink = exportLink;
        this.immediate = immediate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskDisplayName() {
        return taskDisplayName;
    }

    public void setTaskDisplayName(String taskDisplayName) {
        this.taskDisplayName = taskDisplayName;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public String getExportLink() {
        return exportLink;
    }

    public void setExportLink(String exportLink) {
        this.exportLink = exportLink;
    }


}
