package com.tecomgroup.qos.communication.message.recording;

public class TriggeredRecordStop extends TriggeredRecordStart {
     public TriggeredRecordStop() {
         super();
    }

    public TriggeredRecordStop(String time, String taskId, String eventId) {
        super(time, taskId, eventId);
    }
}
