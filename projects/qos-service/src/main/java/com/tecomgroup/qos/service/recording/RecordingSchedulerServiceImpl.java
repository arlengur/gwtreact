package com.tecomgroup.qos.service.recording;

import com.codahale.metrics.Timer;

import com.tecomgroup.qos.communication.message.recording.TriggeredRecordStart;
import com.tecomgroup.qos.communication.message.recording.TriggeredRecordStop;
import com.tecomgroup.qos.domain.recording.data.TimeZoneDTO;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.rest.data.ProbeBase;
import com.tecomgroup.qos.domain.recording.Schedule;
import com.tecomgroup.qos.modelspace.jdbc.dao.recording.RecordingSchedulerServiceDao;
import com.tecomgroup.qos.rest.data.QoSTaskBase;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.Metrics;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by uvarov.m on 13.01.2016.
 */
public class RecordingSchedulerServiceImpl extends AbstractService implements RecordingSchedulerService, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(RecordingSchedulerServiceImpl.class);

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");

    @Autowired
    private RecordingSchedulerServiceDao provider;

    @Autowired
    AuthorizeService authorizeService;

    protected RecordingMessenger messenger;

    private Timer getTimezonesListTimer;
    private Timer getAllAgentsTimer;
    private Timer getTasksForAgentList;
    private Timer getScheduleTimer;
    private Timer createScheduleTimer;
    private Timer updateScheduleTimer;
    private Timer deleteScheduleTimer;
    private Timer createSchedulesForTasksTimer;
    private Timer changeScheduleTypeTimer;
    private Timer getScheduleByAgentAndTaskTimer;


    public RecordingSchedulerServiceImpl(Metrics metrics, final RecordingMessenger messenger) {
        this.messenger = messenger;

        getTimezonesListTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "get-timezones-list");
        getAllAgentsTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "get-all-agents-with-recording");
        getTasksForAgentList = metrics.timer(
                RecordingSchedulerService.class, "get-tasks-for-agent-list");
        getScheduleTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "get-schedule");
        getScheduleByAgentAndTaskTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "get-schedule-by-agent-task");
        createScheduleTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "create-schedule");
        updateScheduleTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "update-schedule");
        deleteScheduleTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "delete-schedule");

        createSchedulesForTasksTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "create-schedule-for-tasks");
        changeScheduleTypeTimer = metrics.timer(
                RecordingSchedulerServiceImpl.class, "change-schedule-type");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        internalEventBroadcaster.subscribe(new QoSEventListener() {
            @Override
            public void onServerEvent(final AbstractEvent event) {
                processAlertReportEvent((AlertReportEvent) event);
            }
        }, new QoSEventFilter() {
            @Override
            public boolean accept(final AbstractEvent event) {
                return event instanceof AlertReportEvent;
            }
        });
    }

    private void processAlertReportEvent(final AlertReportEvent reportEvent) {
        if(reportEvent.getEventType().equals(AbstractEvent.EventType.CREATE)) {
            messenger.sendTriggeredRecordingStart(reportEvent.getAgentKey(),new TriggeredRecordStart(
                    dateFormatter.print(reportEvent.getDateTime().withZone(DateTimeZone.UTC)),
                    reportEvent.getTaskKey(),
                    reportEvent.getReportId().toString()));
        } else {
            messenger.sendTriggeredRecordingEnd(reportEvent.getAgentKey(), new TriggeredRecordStop(
                    dateFormatter.print(reportEvent.getDateTime().withZone(DateTimeZone.UTC)),
                    reportEvent.getTaskKey(),
                    reportEvent.getReportId().toString()));
        }
    }

    @Override
    public List<TimeZoneDTO> getTimezonesList() {
        final Timer.Context timer = getTimezonesListTimer.time();
        try {
            Set<TimeZoneDTO> result = new TreeSet<>();
            Set<String> zoneIds = DateTimeZone.getAvailableIDs();
            for (String zoneId : zoneIds) {
                TimeZoneDTO dto = TimeZoneDTO.fromJodaTimeZone(DateTimeZone.forID(zoneId));
                result.add(dto);
            }

            return TimeZoneDTO.getSorted(result);
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<ProbeBase> getAllAgents() {
        final Timer.Context timer = getAllAgentsTimer.time();
        List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();

        try {
            List<Map<String, String>> agentAttributes = provider.getAllAgentsWithRecording();
            List<ProbeBase> result = new ArrayList<>();
            for (Map<String, String> agentAttr : agentAttributes) {
                ProbeBase probDTO = ProbeBase.populateProbeInfo(agentAttr);
                if(agentKeys.contains(probDTO.entityKey)) {
                    result.add(probDTO);
                }
            }
            Collections.sort(result, ProbeBase.ProbeComparator);
            log.info("getAllAgents ->  {} found", result.size());
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<QoSTaskBase> getRecordTasksForAgentList(List<String> agentKeys) {
        final Timer.Context timer = getTasksForAgentList.time();
        try {
            List<Map<String, String>> taskAttrs = provider.getRecordTasksForAgentList(agentKeys);
            List<QoSTaskBase> result = new ArrayList<>();
            for (Map<String, String> atr : taskAttrs) {
                result.add(QoSTaskBase.populateTaskInfo(atr));
            }
            log.info("getRecordTasksForAgentList ->  {} found", result.size());
            return result;
        } finally {
            timer.stop();
        }
    }

    public Schedule getSchedule(Long id) {
        final Timer.Context timer = getScheduleTimer.time();
        try {
            Schedule result = provider.getSchedule(id);
            log.info("getSchedule id [{}] -> found", id);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public Schedule getScheduleByAgentAndTask(String agentKey, String taskKey) {
        final Timer.Context timer = getScheduleByAgentAndTaskTimer.time();
        try {
            Schedule result = provider.getScheduleByAgentAndTask(agentKey, taskKey);
            if(result != null) {
                log.info("getScheduleByAgentAndTask [{}, {}] -> {} found", agentKey, taskKey, result.getId());
            } else {
                log.error("getScheduleByAgentAndTask [{}, {}] -> not found", agentKey, taskKey);
            }
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public Long createSchedule(Schedule schedule) {
        final Timer.Context timer = createScheduleTimer.time();
        try {
            Long result = provider.createSchedule(schedule);
            log.info("createSchedule id [{}] -> created", result);

            messenger.sendRecordingScheduleMessageForAgent(
                    schedule.getAgentKey(),
                    provider.getSchedulesByAgent(schedule.getAgentKey()));
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public Long updateSchedule(Schedule schedule) {
        final Timer.Context timer = updateScheduleTimer.time();
        try {
            Long result = provider.updateSchedule(schedule);
            log.info("updateSchedule id [{}] -> updated", result);

            messenger.sendRecordingScheduleMessageForAgent(
                    schedule.getAgentKey(),
                    provider.getSchedulesByAgent(schedule.getAgentKey()));
            return result;
        } finally {
            timer.stop();
        }
    }

    public Long deleteSchedule(Long id) {
        final Timer.Context timer = deleteScheduleTimer.time();
        try {
            Long result = provider.removeSchedule(id);
            log.info("deleteSchedule  [{}] -> deleted", result);
            return result;
        } finally {
            timer.stop();
        }
    }

    private void multiAgentMessageSend(Map<String, String> taskAgentMap) {
        Set<String> agentKeys = new HashSet<>(taskAgentMap.values());
        for(String agentKey: agentKeys) {
            messenger.sendRecordingScheduleMessageForAgent(
                    agentKey,
                    provider.getSchedulesByAgent(agentKey));
        }
    }

    @Override
    public Set<String> createSchedulesForTasks(Schedule schedule, Map<String, String> taskAgentMap) {
        final Timer.Context timer = createSchedulesForTasksTimer.time();
        try {
            Set<String> result = provider.createScheduleForTasks(schedule, taskAgentMap);
            multiAgentMessageSend(taskAgentMap);
            log.info("createSchedulesForTasks  [{}, {}]-> created ", schedule.getName(), taskAgentMap.size());
            return result;
        } finally {
            timer.stop();
        }
    }


    @Override
    public Set<String> changeScheduleTypeBatch(Map<String, String> taskAgentMap, Schedule.Type type) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, String> entry : taskAgentMap.entrySet()) {
            changeScheduleType(entry.getValue(), entry.getKey(), type);
            result.add(entry.getValue());
        }
        multiAgentMessageSend(taskAgentMap);
        return result;
    }

    @Override
    public Boolean changeScheduleTypeSingle(String agentKey, String taskKey, Schedule.Type type) {
        Boolean result = changeScheduleType(agentKey, taskKey, type);
        messenger.sendRecordingScheduleMessageForAgent(
                agentKey,
                provider.getSchedulesByAgent(agentKey));
        return result;
    }

    private Boolean changeScheduleType(String agentKey, String taskKey, Schedule.Type type) {
        Schedule schedule = provider.getScheduleByAgentAndTask(agentKey, taskKey);
        if(schedule == null) {
            if(Schedule.Type.READY_TO_RUN.equals(type)) {
                Schedule readySchedule = new Schedule();
                readySchedule.setAgentKey(agentKey);
                readySchedule.setTaskKey(taskKey);
                provider.createSchedule(readySchedule);
            }
            return true;
        } else {
            return changeExistScheduleType(schedule, type);
        }
    }

    private Boolean changeExistScheduleType(Schedule schedule, Schedule.Type type) {
        final Timer.Context timer = changeScheduleTypeTimer.time();
        try {
            if(Schedule.Type.SCHEDULED.equals(type)) return true;

            if(Schedule.Type.CYCLIC.equals(type)) {
                Long removedId = provider.removeSchedule(schedule.getId());
                log.info("changeScheduleType schedule removed -> {} ok", removedId);
            } else if (Schedule.Type.READY_TO_RUN.equals(type)) {
                if (schedule.getEventList() == null || schedule.getEventList().isEmpty()) {
                    return true;
                }
                schedule.setEventList(null);
                Long removedId = provider.updateSchedule(schedule);
                log.info("changeScheduleType schedule timetable removed -> {} ok", removedId);
            }

            log.info("changeScheduleType Schedule type changed to [{},{}]-> {} ok", schedule.getId(), type.name());

            return true;
        } finally {
            timer.stop();
        }
    }

}
