package com.tecomgroup.qos.service.recording;

import com.tecomgroup.qos.domain.recording.data.TimeZoneDTO;
import com.tecomgroup.qos.rest.data.ProbeBase;
import com.tecomgroup.qos.domain.recording.Schedule;
import com.tecomgroup.qos.rest.data.QoSTaskBase;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by uvarov.m on 11.01.2016.
 */
public interface RecordingSchedulerService {
    List<TimeZoneDTO> getTimezonesList();
    List<ProbeBase> getAllAgents();
    List<QoSTaskBase> getRecordTasksForAgentList(List<String> agentKeys);

    Schedule getScheduleByAgentAndTask(String agentKey, String taskKey);
    Long createSchedule(Schedule schedule);
    Long updateSchedule(Schedule schedule);

    Set<String> createSchedulesForTasks(Schedule schedule, Map<String, String> keyMap);
    Boolean changeScheduleTypeSingle(String agentKey, String taskKey, Schedule.Type type);
    Set<String> changeScheduleTypeBatch(Map<String, String> keyMap, Schedule.Type type);
}
