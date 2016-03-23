package com.tecomgroup.qos.service;

import com.tecomgroup.qos.communication.message.recording.ExportVideo;
import com.tecomgroup.qos.domain.probestatus.MEventProperty;
import com.tecomgroup.qos.domain.probestatus.MExportVideoEvent;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.service.recording.RecordingMessenger;
import com.tecomgroup.qos.service.recording.RecordingSchedulerServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author galin.a on 11.01.2016.
 */
public class RecordingScheduleMediaServiceImpl extends RecordingSchedulerServiceImpl implements RecordingScheduleMediaService {

    public RecordingScheduleMediaServiceImpl(Metrics metrics, RecordingMessenger messenger) {
        super(metrics, messenger);
    }

    @Override
    public String sendExportVideoMessage(String agentKey, String user, String taskId,
                                         String taskDisplayName, String quality,
                                         String startDate, long duration,
                                         String exportLink, boolean immediate) {
        ExportVideo exportVideo = new ExportVideo(user, taskId,
                taskDisplayName, quality,
                startDate, duration, exportLink, immediate);
        messenger.sendExportVideoMessage(agentKey, exportVideo);
        return exportVideo.getUuid();
    }
}
