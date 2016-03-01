package com.tecomgroup.qos.service.recording;

import com.tecomgroup.qos.communication.message.recording.ExportVideo;
import com.tecomgroup.qos.communication.message.recording.TriggeredRecordStart;
import com.tecomgroup.qos.communication.message.recording.TriggeredRecordStop;
import com.tecomgroup.qos.domain.recording.Schedule;

import java.util.List;
import java.util.Map;

/**
 * Created by uvarov.m on 16.01.2016.
 */
public interface RecordingMessenger {

    void sendRecordingScheduleMessageForAgent(String agentKey, List<Schedule> scheduleList);

    void sendRecordingScheduleMessage(Schedule scheduleEntity);

    void sendRecordingScheduleMessage(Schedule schedule, Map<String, String> taskAgentMap);

    void sendExportVideoMessage(String agentKey, ExportVideo exportVideo);

    void sendTriggeredRecordingStart(String agentKey, TriggeredRecordStart start);

    void sendTriggeredRecordingEnd(String agentKey, TriggeredRecordStop stop);

}
