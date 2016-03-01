package com.tecomgroup.qos.communication.message.recording;

import java.io.Serializable;

public class TriggeredRecordStart implements Serializable {
    private String time;
    private String taskKey;
    private String eventId;

    public TriggeredRecordStart() {
    }

    public TriggeredRecordStart(String time, String taskId, String eventId) {
        this.time = time;
        this.taskKey = taskId;
        this.eventId = eventId;
    }

    public String getTime() {
        return time;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public String getEventId() {
        return eventId;
    }
}
