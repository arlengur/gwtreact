package com.tecomgroup.qos.service;

import com.tecomgroup.qos.communication.message.recording.ExportVideo;
import com.tecomgroup.qos.service.recording.RecordingMessenger;
import com.tecomgroup.qos.service.recording.RecordingSchedulerServiceImpl;

/**
 * @author galin.a on 11.01.2016.
 */
public class RecordingScheduleMediaServiceImpl extends RecordingSchedulerServiceImpl implements RecordingScheduleMediaService {
    public RecordingScheduleMediaServiceImpl(Metrics metrics, RecordingMessenger messenger) {
        super(metrics, messenger);
    }

    @Override
    public void sendExportVideoMessage(String agentKey, ExportVideo exportVideo) {
        messenger.sendExportVideoMessage(agentKey, exportVideo);
    }
}
