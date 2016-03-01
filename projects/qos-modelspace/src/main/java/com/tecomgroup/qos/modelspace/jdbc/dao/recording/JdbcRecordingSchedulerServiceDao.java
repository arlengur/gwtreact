package com.tecomgroup.qos.modelspace.jdbc.dao.recording;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.recording.Event;
import com.tecomgroup.qos.domain.recording.Schedule;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by uvarov.m on 12.01.2016.
 */
public class JdbcRecordingSchedulerServiceDao
        extends JdbcRecordingSchedulerDaoBase
        implements RecordingSchedulerServiceDao{

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.initStoredProcedure();
    }

    @Override
    public List<Map<String, String>> getAllAgentsWithRecording() {
        try {
            final List<Map<String, String>> agents = new ArrayList<>();

            this.jdbcTemplate.query(
                    " SELECT DISTINCT ON (magent.entity_key) " +
                            " magent.entity_key as key, " +
                            " magent.displayname as name, " +
                            " magent.gisposition_latitude as lat, " +
                            " magent.gisposition_longitude as lon " +
                            " FROM magenttask, mmediaagentmodule, magent " +
                            " WHERE mmediaagentmodule.displayname in ('TSRecorder','MediaRecorder') and " +
                            "      magenttask.parent_id = mmediaagentmodule.id AND " +
                            "      magent.id = mmediaagentmodule.parent_id AND " +
                            "      magent.deleted = FALSE AND " +
                            "      magenttask.deleted  = FALSE AND " +
                            "      magenttask.disabled = FALSE " +
                            " ORDER BY magent.entity_key ;",
                    new Object[]{},
                    new RowMapper<Boolean>() {
                        public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Map<String, String> p = new HashMap<String, String>();
                            p.put("key", rs.getString("key"));
                            p.put("lat", rs.getString("lat"));
                            p.put("lon", rs.getString("lon"));
                            p.put("name", rs.getString("name"));
                            agents.add(p);
                            return true;
                        }
                    });
            return agents;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<Map<String, String>> getRecordTasksForAgentList(List<String> agents) {
        try {
            final List<Map<String, String>> tasks = new ArrayList<>();
            Map<String, List<String>> parameters = Collections.singletonMap("names", agents);

            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new
                    NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());

            namedParameterJdbcTemplate.query(
                    " SELECT DISTINCT ON (all_record_tasks.agent_key, all_record_tasks.task_key) " +
                            "  *,  " +
                            "  exists( " +
                            "      SELECT 1" +
                            "      FROM mrecording_schedule as mrs " +
                            "      WHERE all_record_tasks.task_key = mrs.task_key and " +
                            "            all_record_tasks.agent_key = mrs.agent_key) as schedule_exists, " +
                            "  exists( " +
                            "      SELECT 1 " +
                            "      FROM mrecording_schedule as mrs, mrecording_schedule_event_list mrsel " +
                            "      WHERE all_record_tasks.task_key = mrs.task_key and " +
                            "            all_record_tasks.agent_key = mrs.agent_key AND " +
                            "            mrsel.eventlist_id = mrs.id) as timetable_exists," +
                            "  exists (select 1 " +
                            "          from mproperty, magenttask, magenttask_mproperty " +
                            "          where " +
                            "            magenttask.entity_key = all_record_tasks.task_key AND " +
                            "            mproperty.id = magenttask_mproperty.properties_id AND " +
                            "            magenttask_mproperty.magenttask_id = magenttask.id AND " +
                            "            mproperty.name = 'round_robin' AND mproperty.value = 'true') as round_robin_enabled " +
                            " FROM " +
                            "(SELECT magent.entity_key as agent_key, " +
                            "       magent.timezone as time_zone, " +
                            "       magenttask.entity_key as task_key, " +
                            "       magenttask.displayname as task_name, " +
                            "       mmediaagentmodule.displayname as module, " +
                            "       magent.displayname as agent_name " +
                            " FROM magenttask, mmediaagentmodule, magent " +
                            " WHERE mmediaagentmodule.displayname in ('TSRecorder','MediaRecorder') and " +
                            "      magenttask.parent_id = mmediaagentmodule.id AND " +
                            "      magent.id = mmediaagentmodule.parent_id AND " +
                            "      magenttask.deleted  = FALSE AND " +
                            "      magenttask.disabled = FALSE AND " +
                            "      magent.entity_key IN ( :names )) as all_record_tasks " +
                            " ORDER BY all_record_tasks.agent_key, all_record_tasks.task_key; ",
                    parameters,
                    new RowMapper<Boolean>() {
                        public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Map<String, String> p = new HashMap<String, String>();
                            p.put("agent_key", rs.getString("agent_key"));
                            p.put("agent_name", rs.getString("agent_name"));

                            p.put("task_key", rs.getString("task_key"));
                            p.put("task_name", rs.getString("task_name"));
                            p.put("module", rs.getString("module"));
                            p.put("time_zone", rs.getString("time_zone"));

                            if(rs.getBoolean("schedule_exists") && rs.getBoolean("timetable_exists")) {
                                p.put("schedule_state",Schedule.Type.SCHEDULED.name());
                            } else if(rs.getBoolean("schedule_exists") && !rs.getBoolean("timetable_exists")) {
                                p.put("schedule_state",Schedule.Type.READY_TO_RUN.name());
                            } else {
                                p.put("schedule_state",Schedule.Type.CYCLIC.name());
                            }

                            p.put("round_robin", rs.getBoolean("round_robin_enabled")? "enabled": "disabled");
                            tasks.add(p);
                            return true;
                        }
                    });
            return tasks;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Schedule getSchedule(final Long id) {
        try {
            Schedule result = this.jdbcTemplate.queryForObject
                    ("select name, " +
                                    "  agent_key, " +
                                    "  task_key,  " +
                                    "  time_zone" +
                                    " from mrecording_schedule  where id = ?",
                            new Object[]{id},
                            new RowMapper<Schedule>() {
                                public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    Schedule p = new Schedule();
                                    p.setId(id);
                                    p.setName(rs.getString("name"));
                                    p.setAgentKey(rs.getString("agent_key"));
                                    p.setTaskKey(rs.getString("task_key"));
                                    p.setTimeZone(rs.getString("time_zone"));
                                    return p;
                                }
                            });
            result.setEventList(getEventsByScheduleId(id));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Schedule> getSchedulesByAgent(final String agentKey) {
        try {
            List<Schedule> result = this.jdbcTemplate.query
                                (" select sch.id, " +
                                    "  sch.task_key, " +
                                    "  sch.name, " +
                                    "  sch.time_zone " +
                                    " from mrecording_schedule as sch, magenttask as task " +
                                    " where sch.agent_key = ? and " +
                                    "       sch.task_key = task.entity_key and " +
                                    "       task.deleted = FALSE and " +
                                    "       task.disabled = FALSE; ;",
                                    new Object[]{agentKey},
                            new RowMapper<Schedule>() {
                                public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    Schedule p = new Schedule();
                                    p.setId(rs.getLong("id"));
                                    p.setName(rs.getString("name"));
                                    p.setTimeZone(rs.getString("time_zone"));
                                    p.setAgentKey(agentKey);
                                    p.setTaskKey(rs.getString("task_key"));
                                    return p;
                                }
                            });
            for(Schedule schedule: result) {
                schedule.setEventList(getEventsByScheduleId(schedule.getId()));
            }
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Schedule getScheduleByAgentAndTask(final String agentKey, final String taskKey) {
        try {
            Schedule result = this.jdbcTemplate.queryForObject
                            ("select id," +
                                    " name, " +
                                    " time_zone" +
                                    " from mrecording_schedule  " +
                                            "where agent_key = ? " +
                                            " and task_key = ? ;",
                            new Object[]{agentKey, taskKey},
                            new RowMapper<Schedule>() {
                                public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    Schedule p = new Schedule();
                                    p.setId(rs.getLong("id"));
                                    p.setName(rs.getString("name"));
                                    p.setTimeZone(rs.getString("time_zone"));
                                    p.setAgentKey(agentKey);
                                    p.setTaskKey(taskKey);
                                    return p;
                                }
                            });
            result.setEventList(getEventsByScheduleId(result.getId()));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Long updateSchedule(Schedule schedule) {
        Schedule found = getScheduleByAgentAndTask(schedule.getAgentKey(), schedule.getTaskKey());
        if(found != null) {
            return updateScheduleRow(schedule, found);
        }
        return null;
    }

    private Long updateScheduleRow(Schedule schedule, Schedule found){
        if(schedule.equals(found)) return found.getId();

        schedule.setId(found.getId());
        updateEventList(found.getEventList(), schedule.getEventList(), schedule.getId());

        Long scheduleId = updateScheduleRow(schedule);

        return scheduleId;
    }

    @Override
    @Transactional
    public Long createSchedule(Schedule schedule) {
        //Update schedule if already exists
        Long updatedScheduleId = updateSchedule(schedule);
        if (updatedScheduleId != null) {
            return updatedScheduleId;
        }

        //Create schedule
        Long scheduleId = createScheduleRow(schedule);
        List<Event> createdEvents = createEvents(schedule.getEventList(), scheduleId);
        schedule.setEventList(createdEvents);
        return scheduleId;
    }

    @Override
    @Transactional
    public Set<String> createScheduleForTasks(Schedule schedule, Map<String, String> taskAgentMap) {
        Set<String> result = new HashSet<>();

        for (Map.Entry<String, String> entry : taskAgentMap.entrySet()) {
            Schedule copy = Schedule.copy(schedule);
            copy.setTaskKey(entry.getKey());
            copy.setAgentKey(entry.getValue());

            //Update schedule if already exists
            Long updatedScheduleId = updateSchedule(copy);

            if (updatedScheduleId == null) {
                //Create schedule
                Long scheduleId = createScheduleRow(copy);
                createEventsNoResult(copy.getEventList(), scheduleId);
            }

            result.add(copy.getAgentKey());
        }
        return result;
    }

    @Override
    @Transactional
    public Long removeSchedule(Long id) {
        deleteEventsByParentId(id);
        deleteScheduleId(id);
        return id;
    }

    @Override
    public String getRelatedRecordingTask(String originTaskKey) {
        try {
            return this.jdbcTemplate.queryForObject
                    ("select mproperty.value " +
                                    " from mproperty, magenttask, magenttask_mproperty " +
                                    " where " +
                                    " magenttask.entity_key = ? AND " +
                                    " mproperty.id = magenttask_mproperty.properties_id AND " +
                                    " magenttask_mproperty.magenttask_id = magenttask.id AND " +
                                    " mproperty.name = ? ;",
                            new Object[]{originTaskKey, MAgentTask.RELEATED_RECORDING_TASK_PROPERTY_NAME},
                            String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Long createScheduleRow(Schedule schedule) {
            Long scheduleId = getNextSeqId();
            try {
                this.jdbcTemplate.update(
                        "INSERT INTO mrecording_schedule " +
                                "(id, " +
                                "name, " +
                                "time_zone, " +
                                "agent_key, " +
                                "task_key ) " +
                                " VALUES(?,?,?,?,?) ;",
                        scheduleId,
                        schedule.getName(),
                        schedule.getTimeZone(),
                        schedule.getAgentKey(),
                        schedule.getTaskKey());
                return scheduleId;
            } catch (EmptyResultDataAccessException e) {
                throw new RuntimeException(e);
            }
    }

    private Long updateScheduleRow(Schedule schedule) {
        try {
            this.jdbcTemplate.update(
                    "UPDATE mrecording_schedule SET " +
                            " name = ?, " +
                            " time_zone = ? " +
                            " WHERE id = ? ; ",
                    schedule.getName(),
                    schedule.getTimeZone(),
                    schedule.getId());
            return  schedule.getId();
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Event> getEventsByScheduleId(final Long parentId) {
        try {
            return this.jdbcTemplate.query(
                    "select id, start_date, end_date, comment " +
                            " from mrecording_schedule_event_list where eventlist_id = ? ",
                    new Object[]{parentId},
                    new RowMapper<Event>() {
                        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
                            Event c = new Event();
                            c.setId(rs.getLong("id"));
                            c.setParentId(parentId);
                            c.setStartDateTime(rs.getString("start_date"));
                            c.setEndDateTime(rs.getString("end_date"));
                            c.setComment(rs.getString("comment"));
                            return c;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void createEventsNoResult(List<Event> events, Long parentId) {
        if(events == null) return;

        for(Event event: events) {
            if(event.getId() == null) {
                createEvent(event, parentId);
            }
        }
    }

    public List<Event> createEvents(List<Event> events, Long parentId) {
        if(events == null) return null;

        List<Event> result = new ArrayList<>();
        for(Event event: events) {
            if(event.getId() == null) {
                Long id = createEvent(event, parentId);
                if(id != null) {
                    event.setId(id);
                    event.setParentId(parentId);
                    result.add(event);
                }
            }
        }
        return result;
    }

    public List<Long> getEventsIds(final List<Event> events) {
        List<Long> result = new ArrayList<>();
        for(Event c: events) {
            result.add(c.getId());
        }
        return result;
    }

    public void updateEventList(List<Event> oldEvents,
                                    List<Event> newEvents,
                                    Long parentId) {

        Set<Long> eventsToDelete = new HashSet<>(getEventsIds(oldEvents));
        List<Event> eventToCreate = new ArrayList<>();

        // TODO use Set and retain, compare etc.
        if (newEvents != null) {
            for (Event event : newEvents) {
                if (event.getId() == null) {
                    eventToCreate.add(event);
                    continue;
                }
                if (eventsToDelete.contains(event.getId())) {
                    eventsToDelete.remove(event.getId());
                }
            }
        }
        createEvents(eventToCreate, parentId);
        deleteEvents(eventsToDelete);
    }

    public Long createEvent(Event event,Long parentId) {
        Long eventId = getNextSeqId();
        try {
            this.jdbcTemplate.update(
                    "INSERT INTO mrecording_schedule_event_list " +
                            "(id, eventlist_id, start_date, end_date, comment) " +
                            " VALUES(?,?,?,?,?);", eventId, parentId, event.getStartDateTime(),
                    event.getEndDateTime(), event.getComment());
            return eventId;
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEvents(Set<Long> eventsIds) {
        try {
            for(Long id: eventsIds) {
                this.jdbcTemplate.update(
                        "DELETE FROM mrecording_schedule_event_list WHERE id = ?", id);
            }

        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteScheduleId(Long id) {
        try {
            this.jdbcTemplate.update(
                    "DELETE FROM mrecording_schedule WHERE id = ?", id);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEventsByParentId(Long id) {
        try {
            this.jdbcTemplate.update(
                    "DELETE FROM mrecording_schedule_event_list WHERE eventlist_id = ?", id);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
