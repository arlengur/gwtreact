/*
 * Copyright (C) 2016 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.rest;

import com.tecomgroup.qos.domain.recording.data.*;
import com.tecomgroup.qos.domain.recording.Schedule;
import com.tecomgroup.qos.rest.data.ProbeBase;
import com.tecomgroup.qos.rest.data.QoSTaskBase;
import com.tecomgroup.qos.service.recording.RecordingSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("recording-scheduler")
@Component
public class RecordingSchedulerRESTFacade {

    @Qualifier("recordingService")
    @Autowired
    private RecordingSchedulerService service;

    @GET
    @Path("/timezone/list")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<TimeZoneDTO> getTimezonesList() {
        return service.getTimezonesList();
    }

    @GET
    @Path("/agent/list")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<ProbeBase> getAllAgents() {
        return service.getAllAgents();
    }

    @POST
    @Path("/task/list")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<QoSTaskBase> getRecordTasksForAgentList(RequestTaskDTO request) {
        return service.getRecordTasksForAgentList(request.agents);
    }

    @GET
    @Path("/{agent_key}/{task_key}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public ScheduleDTO getSchedule(
            @PathParam("agent_key") String agentKey,
            @PathParam("task_key")  String taskKey) {
        Schedule s = service.getScheduleByAgentAndTask(agentKey, taskKey);
        return ScheduleDTO.fromEntity(s);
    }

    @POST
    @Path("/create")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Long createSchedule(ScheduleDTO request) {
        return service.createSchedule(ScheduleDTO.toEntity(request));
    }

    @POST
    @Path("/create/batch")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Set<String> createBatchSchedule(ScheduleBatchDTO request) {
        return service.createSchedulesForTasks(ScheduleDTO.toEntity(request), request.taskAgentMap);
    }

    @PUT
    @Path("/update")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Long updateSchedule(ScheduleDTO request) {
        return service.updateSchedule(ScheduleDTO.toEntity(request));
    }

    @PUT
    @Path("/change-type/{agent_key}/{task_key}/{type}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Boolean changeScheduleType(@PathParam("agent_key") String agentKey,
                                   @PathParam("task_key")  String taskKey,
                                   @PathParam("type")  String type) {
        return service.changeScheduleTypeSingle(agentKey, taskKey, Schedule.Type.valueOf(type));
    }

    @PUT
    @Path("/change-type/batch")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Set<String> changeScheduleTypeBatch(ChangeTypeBatchDTO request) {
        return service.changeScheduleTypeBatch(request.taskAgentMap, Schedule.Type.valueOf(request.type));
    }
}
