package com.tecomgroup.qos.service.recording;

import com.codahale.metrics.Timer;
import com.tecomgroup.qos.communication.message.recording.ExportVideo;
import com.tecomgroup.qos.communication.message.recording.TriggeredRecordStart;
import com.tecomgroup.qos.communication.message.recording.TriggeredRecordStop;
import com.tecomgroup.qos.communication.message.recording.TimetableConfigUpdate;
import com.tecomgroup.qos.domain.recording.Schedule;
import com.tecomgroup.qos.service.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by uvarov.m on 16.01.2016.
 */
public class RecordingMessengerImpl implements RecordingMessenger{
    private static final Logger log = LoggerFactory.getLogger(RecordingMessengerImpl.class);

    private String serviceExchangeName;
    private String queuePrefix;
    private AmqpTemplate amqpTemplate;
    private MessageConverter messageConverter;

    private Timer sendRecordingScheduleMessageTimer;
    private Timer sendExportVideoMessageTimer;
    private Timer sendTriggeredRecordingStartTimer;
    private Timer sendTriggeredRecordingStopTimer;

    public RecordingMessengerImpl(Metrics metrics) {
        sendRecordingScheduleMessageTimer = metrics.timer(
                RecordingMessengerImpl.class, "send-recording-schedule-message");
        sendExportVideoMessageTimer = metrics.timer(
                RecordingMessengerImpl.class, "send-export-video-message");
        sendTriggeredRecordingStartTimer = metrics.timer(
                RecordingMessengerImpl.class, "send-recording-start-message");
        sendTriggeredRecordingStopTimer = metrics.timer(
                RecordingMessengerImpl.class, "send-recording-stop-message");
    }

    public String getServiceExchangeName() {
        return serviceExchangeName;
    }

    public String getQueuePrefix() {
        return queuePrefix;
    }

    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setServiceExchangeName(String serviceExchangeName) {
        this.serviceExchangeName = serviceExchangeName;
    }

    public void setQueuePrefix(String queuePrefix) {
        this.queuePrefix = queuePrefix;
    }

    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }


    public void sendRecordingScheduleMessageForAgent(String agentKey, List<Schedule> scheduleList) {
        TimetableConfigUpdate scheduleDTO = TimetableConfigUpdate.fromEntity(scheduleList);
        sendRecordingScheduleMessage(agentKey, scheduleDTO);
    }

    public void sendRecordingScheduleMessage(Schedule scheduleEntity) {
        List<String> tasks = new ArrayList<>();
        tasks.add(scheduleEntity.getTaskKey());
        TimetableConfigUpdate scheduleDTO = TimetableConfigUpdate.fromEntity(scheduleEntity, tasks);
        sendRecordingScheduleMessage(scheduleEntity.getAgentKey(), scheduleDTO);
    }

    public void sendRecordingScheduleMessage(Schedule scheduleEntity, Map<String, String> taskAgentMap) {
        Map<String, TimetableConfigUpdate> schedules = TimetableConfigUpdate.fromEntity(scheduleEntity, taskAgentMap);
        for(Map.Entry<String, TimetableConfigUpdate> entry: schedules.entrySet()) {
            // Sent individual message for each agent
            sendRecordingScheduleMessage(entry.getKey(), entry.getValue());
        }
    }

    private void sendRecordingScheduleMessage(String agentKey, TimetableConfigUpdate schedule) {
        final Timer.Context timer = sendRecordingScheduleMessageTimer.time();
        try {
            getAmqpTemplate().convertAndSend(getServiceExchangeName(), getQueuePrefix() + agentKey, schedule);
            log.info("sendRecordingScheduleMessage [{} {}] -> ok", agentKey, schedule);
        } finally {
            timer.stop();
        }
    }

    @Override
    public void sendExportVideoMessage(String agentKey, ExportVideo exportVideo) {
            final Timer.Context timer = sendExportVideoMessageTimer.time();
            try {
                getAmqpTemplate().convertAndSend(getServiceExchangeName(), getQueuePrefix() + agentKey, exportVideo);
                log.info("sendExportVideoMessage [{} {}] -> ok", agentKey, exportVideo);
            } finally {
                timer.stop();
            }
    }

    @Override
    public void sendTriggeredRecordingStart(String agentKey, TriggeredRecordStart start) {
        final Timer.Context timer = sendTriggeredRecordingStartTimer.time();
        try {
            getAmqpTemplate().convertAndSend(getServiceExchangeName(), getQueuePrefix() + agentKey, start);
            log.info("sendTriggeredRecordingStart [{} {}] -> ok", agentKey, start);
        } finally {
            timer.stop();
        }
    }

    @Override
    public void sendTriggeredRecordingEnd(String agentKey, TriggeredRecordStop stop) {
        final Timer.Context timer = sendTriggeredRecordingStopTimer.time();
        try {
            getAmqpTemplate().convertAndSend(getServiceExchangeName(), getQueuePrefix() + agentKey, stop);
            log.info("sendTriggeredRecordingEnd [{} {}] -> ok", agentKey, stop);
        } finally {
            timer.stop();
        }
    }
}
