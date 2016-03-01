package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.communication.message.recording.ExportVideo;

/**
 * Created by galin.a on 11.01.2016.
 */
@RemoteServiceRelativePath("springServices/recordingService")
public interface RecordingScheduleMediaService extends Service, RemoteService {
    void sendExportVideoMessage(String agentKey, ExportVideo exportVideo);
}
