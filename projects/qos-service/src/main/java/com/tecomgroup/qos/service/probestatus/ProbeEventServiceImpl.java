package com.tecomgroup.qos.service.probestatus;

import com.codahale.metrics.Timer;
import com.tecomgroup.qos.communication.request.AgentActionStatus;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AgentActionStatusEvent;
import com.tecomgroup.qos.modelspace.jdbc.dao.probestatus.ProbeEventServiceDao;
import com.tecomgroup.qos.service.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by uvarov.m on 16.01.2016.
 */
@MetricsDomain(name = "com.qligent.vision.probe.events")
public class ProbeEventServiceImpl  extends AbstractService implements ProbeEventService, ProbeEventServiceInternal {
    private static final Logger log = LoggerFactory.getLogger(ProbeEventServiceImpl.class);

    @Autowired
    ProbeEventServiceDao provider;

    private Timer createProbeEventTimer;
    private Timer removeProbeEventTimer;
    private Timer removeProbeEventByKeyTimer;
    private Timer getEventsByKeyTimer;
    private Timer getEventsByAgentTimer;
    private Timer getEventsByUserTimer;
    private Timer getEventsByUserAndTypeTimer;
    private Timer getEventsByPropertyTimer;
    private Timer getUpdateEventTimer;
    private Timer getLastEventsByUserAndTypeTimer;

    public ProbeEventServiceImpl(Metrics metrics) {
        createProbeEventTimer = metrics.timer(
                ProbeEventServiceImpl.class, "createProbeEventTimer");
        removeProbeEventTimer = metrics.timer(
                ProbeEventServiceImpl.class, "removeProbeEventTimer");
        removeProbeEventByKeyTimer = metrics.timer(
                ProbeEventServiceImpl.class, "removeProbeEventByKeyTimer");
        getEventsByKeyTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getEventsByKeyTimer");
        getEventsByAgentTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getEventsByAgentTimer");
        getEventsByUserTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getEventsByUserTimer");
        getEventsByUserAndTypeTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getEventsByUserAndTypeTimer");
        getEventsByPropertyTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getEventsByPropertyTimer");
        getUpdateEventTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getLastEventByKeyTimer");
        getLastEventsByUserAndTypeTimer = metrics.timer(
                ProbeEventServiceImpl.class, "getLastEventsByUserAndTypeTimer");
    }

    @Transactional(readOnly = false)
    @Override
    public Long createProbeEvent(MProbeEvent event) {
        final Timer.Context timer = createProbeEventTimer.time();
        try {
            Long result = provider.createProbeEvent(event);
            log.info("createProbeEvent [{} {} {}] -> ok {}", event.getKey(), event.getAgentKey(), event.getEventType(), result);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public Long removeProbeEvent(Long id) {
        final Timer.Context timer = removeProbeEventTimer.time();
        try {
            Long result = provider.removeProbeEvent(id);
            log.info("removeProbeEvent [{} ] -> ok", id);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public void removeProbeEventByKey(String key) {
        final Timer.Context timer = removeProbeEventByKeyTimer.time();
        try {
            provider.removeProbeEventByKey(key);
            log.info("removeProbeEventByKey [{}] -> ok", key);
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<MProbeEvent> getEventsByKey(String eventKey) {
        final Timer.Context timer = getEventsByKeyTimer.time();
        try {
            List<MProbeEvent> result = provider.getEventsByKey(eventKey);
            log.info("getEventsByKey [{}] -> ok {}", eventKey, result.size());
            return result;
        } finally {
            timer.stop();
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void updateEvent(AgentActionStatus actionResponse) {
        final Timer.Context timer = getUpdateEventTimer.time();
        try {
            MProbeEvent lastEvent = provider.getLastEventByKey(actionResponse.getUuid());
            if(lastEvent != null) {
                DateTime updateDateTime = new DateTime(actionResponse.getDateTime());
                DateTime eventLastUpdateDateTime = new DateTime(lastEvent.getTimestamp());

                if(updateDateTime.isAfter(eventLastUpdateDateTime)) {
                    provider.updateEvent(lastEvent,
                            actionResponse.getStatus(),
                            actionResponse.getDateTime(),
                            actionResponse.getProperties());

                    notifyUserTaskUpdate(actionResponse.getUuid());
                    log.info("updateEvent [{}] -> ok",
                            actionResponse.getUuid());

                } else {
                    log.warn("updateEvent [{} {} {}] -> ignore expired update",
                            actionResponse.getUuid(),
                            actionResponse.getStatus(),
                            actionResponse.getDateTime());
                }

            } else {
                log.error("updateEvent [{}] -> not found error",
                        actionResponse.getUuid());
            }
        } finally {
            timer.stop();
        }
    }

    private void checkAndMarkEventsTimeout(List<MProbeEvent> events) {
        for(MProbeEvent event: events) {
            markEventTimeout(event);
        }
    }

    private void markEventTimeout(MProbeEvent event) {
        // Links to download get expired, so mark them unavailable
        if(event.getStatus() == MProbeEvent.STATUS.OK) {
            DateTime expirationDateTime = new DateTime(event.getTimestamp()).plusDays(7);
            DateTime currentDateTime = new DateTime();
            if (currentDateTime.isAfter(expirationDateTime)) {
                event.setStatus(MProbeEvent.STATUS.TIMEOUT);
                log.debug("markEventTimeout [{} {} {}] -> marked timeout", event.getKey(), event.getAgentKey(), event.getUserLogin());
            }
        }
    }

    @Override
    public List<MProbeEvent> getEventsByAgent(String agentKey) {
        final Timer.Context timer = getEventsByAgentTimer.time();
        try {
            List<MProbeEvent> result = provider.getEventsByAgent(agentKey);
            log.info("getEventsByAgent [{}] -> ok", agentKey);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<MProbeEvent> getEventsByUser(String login) {
        final Timer.Context timer = getEventsByUserTimer.time();
        try {
            List<MProbeEvent> result = provider.getEventsByUser(login);
            log.info("getEventsByUser [{}] -> ok", login);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<MProbeEvent> getEventsByUserAndType(String login, String type) {
        final Timer.Context timer = getEventsByUserAndTypeTimer.time();
        try {
            List<MProbeEvent> result = provider.getEventsByUserAndType(login, type);
            log.info("getEventsByUserAndType [{} {}] -> ok", login, type);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<MProbeEvent> getLastEventsByUserAndType(String login, String type) {
        final Timer.Context timer = getLastEventsByUserAndTypeTimer.time();
        try {
            List<MProbeEvent> result = provider.getLastEventsByUserAndType(login, type);
            checkAndMarkEventsTimeout(result);
            log.info("getLastEventsByUserAndType [{} {}] -> ok", login, type);
            return result;
        } finally {
            timer.stop();
        }
    }

    @Override
    public List<MProbeEvent> getEventsByProperty(String eventType, String propertyKey, String propertyValue) {
        final Timer.Context timer = getEventsByPropertyTimer.time();
        try {
            List<MProbeEvent> result = provider.getEventsByProperty(eventType, propertyKey, propertyValue);
            log.info("getEventsByProperty [{} {} {}] -> ok", eventType, propertyKey, propertyValue);
            return result;
        } finally {
            timer.stop();
        }
    }

    private void notifyUserTaskUpdate(String uuid) {
        MProbeEvent updatedEvent = provider.getLastEventByKey(uuid);
        eventBroadcastDispatcher.broadcast(Arrays
                .asList(new AgentActionStatusEvent(AbstractEvent.EventType.UPDATE, updatedEvent)));
    }
}
