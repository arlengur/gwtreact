/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.modelspace.jdbc.dao.recording;

import com.tecomgroup.qos.domain.recording.Schedule;


import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by uvarov.m on 27.10.2015.
 */
public interface RecordingSchedulerServiceDao {

    List<Schedule> getSchedulesByAgent(final String agentKey);

    List<Map<String, String>>  getAllAgentsWithRecording();

    List<Map<String, String>>  getRecordTasksForAgentList(List<String> agents);

    Schedule getSchedule(Long id);

    Schedule getScheduleByAgentAndTask(String agentKey, String taskKey);

    Long createSchedule(Schedule schedule);

    Set<String> createScheduleForTasks(Schedule schedule, Map<String, String> taskAgentMap);

    Long updateSchedule(Schedule schedule);

    Long removeSchedule(Long id);

    String getRelatedRecordingTask(String originTaskKey);

}
