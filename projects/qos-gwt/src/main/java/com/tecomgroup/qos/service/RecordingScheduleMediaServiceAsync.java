package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.communication.message.recording.ExportVideo;

public interface RecordingScheduleMediaServiceAsync {
    void sendExportVideoMessage(String agentKey, String user, String taskId,
                                String taskDisplayName, String quality,
                                String startDate, long duration,
                                String exportLink, boolean immediate, AsyncCallback<String> async);
}
