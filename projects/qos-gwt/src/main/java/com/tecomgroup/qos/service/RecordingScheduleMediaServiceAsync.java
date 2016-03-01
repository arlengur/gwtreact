package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.communication.message.recording.ExportVideo;

public interface RecordingScheduleMediaServiceAsync {
    void sendExportVideoMessage(String agentKey, ExportVideo exportVideo, AsyncCallback<Void> async);
}
