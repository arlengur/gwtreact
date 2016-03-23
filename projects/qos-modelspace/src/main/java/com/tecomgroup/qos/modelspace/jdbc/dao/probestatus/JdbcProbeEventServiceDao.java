package com.tecomgroup.qos.modelspace.jdbc.dao.probestatus;

import com.tecomgroup.qos.domain.probestatus.MEventProperty;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.domain.probestatus.MProbeEventFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class JdbcProbeEventServiceDao
        extends JdbcProbeEventDaoBase
        implements ProbeEventServiceDao{

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setResultsMapCaseInsensitive(true);
        this.initStoredProcedure();
    }

    @Override
    @Transactional
    public Long createProbeEvent(MProbeEvent event) {
        Long eventId = createEventRow(event);
        List<MEventProperty> createdProperties = createProperties(event.getPropertyList(), eventId);
        event.setPropertyList(createdProperties);
        return eventId;
    }

    private Long createEventRow(MProbeEvent event) {
        Long eventId = getNextSeqId();
        this.jdbcTemplate.update(
                "INSERT INTO mprobe_event " +
                        "(id, " +
                        "key, " +
                        "event_type, " +
                        "status, " +
                        "agent_key, " +
                        "user_login, " +
                        "timestamp, " +
                        "created_timestamp ) " +
                        " VALUES(?,?,?,?,?,?,?,?) ;",
                eventId,
                event.getKey(),
                event.getEventType(),
                event.getStatus().name(),
                event.getAgentKey(),
                event.getUserLogin(),
                event.getTimestamp(),
                event.getCreatedTimestamp());
        return eventId;
    }

    @Override
    @Transactional
    public void updateEvent(MProbeEvent event, MProbeEvent.STATUS state, Date timestamp, List<MEventProperty> newProps) {
        if(event != null) {
            updateEventRow(state, timestamp, event.getKey());
            updatePropertiesList(event.getPropertyList(), newProps, event.getId());
        }
    }

    private void updateEventRow(MProbeEvent.STATUS state, Date timestamp, String key) {
        this.jdbcTemplate.update(
                "UPDATE mprobe_event SET " +
                        " status = ?, " +
                        " timestamp = ? " +
                        " WHERE key = ? ;",
                state.name(),
                timestamp,
                key);
    }

    private List<MEventProperty> createProperties(List<MEventProperty> properties, Long parentId) {
        if(properties == null) return null;

        List<MEventProperty> result = new ArrayList<>();
        for(MEventProperty p: properties) {
            if(p.getKey() == null || p.getValue() == null) {
                continue;
            }
            if(p.getId() == null) {
                Long id = createProperty(p, parentId);
                if(id != null) {
                    p.setId(id);
                    p.setParentId(parentId);
                    result.add(p);
                }
            }
        }
        return result;
    }

    private Long createProperty(MEventProperty p,Long parentId) {
        Long pId = getNextSeqId();
            this.jdbcTemplate.update(
                    "INSERT INTO mprobe_event_properties " +
                            "(id, propertylist_id, key, value) " +
                            " VALUES(?,?,?,?);", pId, parentId, p.getKey(),p.getValue());
        return pId;
    }

    static final class ProbeEventRowMapper implements RowMapper<MProbeEvent> {
        @Override
        public MProbeEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            MProbeEvent p = new MProbeEvent();
            p.setId(rs.getLong("id"));
            p.setKey(rs.getString("key"));
            p.setAgentKey(rs.getString("agent_key"));
            p.setTimestamp(rs.getTimestamp("timestamp"));
            p.setEventType(rs.getString("event_type"));
            p.setStatus(MProbeEvent.STATUS.valueOf(rs.getString("status")));
            p.setUserLogin(rs.getString("user_login"));
            p.setCreatedTimestamp(rs.getTimestamp("created_timestamp"));
            return MProbeEventFactory.getInstance(p);
        }
    }

    private void addAgentDisplayProperty(MProbeEvent event) {
        MEventProperty agentDisplayName = new MEventProperty(
                MProbeEvent.FIELD.AGENT_DISPLAY_NAME.name(),
                getAgentDisplayNameByKey(event.getAgentKey()));

        event.getPropertyList().add(agentDisplayName);
    }

    private void fillProperties(List<MProbeEvent> result) {
        for(MProbeEvent event: result) {
            event.setPropertyList(getPropertiesByEventId(event.getId()));
            addAgentDisplayProperty(event);
        }
    }

    @Override
    @Transactional
    public List<MProbeEvent> getEventsByAgent(final String agentKey) {
        try {
            List<MProbeEvent> result = this.jdbcTemplate.query
                                (
                                        "SELECT * " +
                                        " FROM mprobe_event " +
                                        " WHERE agent_key = ? " +
                                                " ORDER BY CREATED_TIMESTAMP DESC; ",
                                    new Object[]{agentKey},
                            new ProbeEventRowMapper());
            fillProperties(result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<MEventProperty> getPropertiesByEventId(final Long parentId) {
        try {
            return this.jdbcTemplate.query(
                    "select id, key, value " +
                            " from mprobe_event_properties where propertylist_id = ? ",
                    new Object[]{parentId},
                    new RowMapper<MEventProperty>() {
                        public MEventProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
                            MEventProperty c = new MEventProperty();
                            c.setId(rs.getLong("id"));
                            c.setParentId(parentId);
                            c.setKey(rs.getString("key"));
                            c.setValue(rs.getString("value"));
                            return c;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<MProbeEvent> getEventsByUser(final String login) {
        try {
            List<MProbeEvent> result = this.jdbcTemplate.query
                    (
                            "SELECT * " +
                            " FROM mprobe_event " +
                            " WHERE user_login = ? " +
                                    " ORDER BY CREATED_TIMESTAMP DESC; ",
                            new Object[]{login},
                            new ProbeEventRowMapper());
            fillProperties(result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<MProbeEvent> getEventsByUserAndType(final String login, final String type) {
        try {
            List<MProbeEvent> result = this.jdbcTemplate.query
                    (
                            "SELECT * " +
                            " FROM mprobe_event " +
                            " WHERE user_login = ? AND event_type = ? " +
                                    " ORDER BY CREATED_TIMESTAMP DESC; ",
                            new Object[]{login, type},
                            new ProbeEventRowMapper());
            fillProperties(result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<MProbeEvent> getLastEventsByUserAndType(String login, String type) {
        try {
            List<MProbeEvent> result = this.jdbcTemplate.query
                    (
                            "SELECT DISTINCT ON (KEY) * " +
                                    " FROM mprobe_event " +
                                    " WHERE user_login = ? AND " +
                                    "       event_type = ? " +
                                    " ORDER BY KEY, CREATED_TIMESTAMP DESC; ",
                            new Object[]{login, type},
                            new ProbeEventRowMapper());
            fillProperties(result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    @Transactional
    public List<MProbeEvent> getEventsByKey(final String eventKey) {
        try {
            List<MProbeEvent> result = this.jdbcTemplate.query
                    (
                            "SELECT * " +
                                    " FROM mprobe_event " +
                                    " WHERE key = ? ",
                            new Object[]{eventKey},
                            new ProbeEventRowMapper());
            fillProperties(result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public MProbeEvent getLastEventByKey(String eventKey) {
        try {
            MProbeEvent result = this.jdbcTemplate.queryForObject
                    ("SELECT * " +
                                    " FROM mprobe_event " +
                                    " WHERE key = ? " +
                                    " ORDER BY TIMESTAMP DESC " +
                                    " LIMIT 1;",
                            new Object[]{eventKey}, new ProbeEventRowMapper());
            result.setPropertyList(getPropertiesByEventId(result.getId()));
            addAgentDisplayProperty(result);

            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public String getAgentDisplayNameByKey(String agentKey) {
        try {
            return this.jdbcTemplate.queryForObject
                    ("SELECT displayname " +
                                    " FROM magent " +
                                    " WHERE entity_key = ? AND deleted = false ;" ,
                            new Object[]{agentKey}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    @Transactional
    public List<MProbeEvent> getEventsByProperty(final String eventType, String propertyKey, String propertyValue) {
        try {
            List<MProbeEvent> result = this.jdbcTemplate.query
                    ("SELECT pe.id, pe.key, pe.agent_key, pe.timestamp, pe.event_type, pe.status, pe.user_login, pe.created_timestamp " +
                            " FROM mprobe_event pe, mprobe_event_properties pp" +
                            " WHERE " +
                            " pe.event_type = ? AND" +
                            " pe.id = pp.propertylist_id AND " +
                            " pp.key = ? AND " +
                            " pp.value = ? ;",
                            new Object[]{eventType, propertyKey, propertyValue},
                            new ProbeEventRowMapper());
            fillProperties(result);
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    @Override
    public Long removeProbeEvent(Long id) {
        deletePropertiesByParentId(id);
        deleteEvent(id);
        return id;
    }

    @Override
    @Transactional
    public void removeProbeEventByKey(String key) {
        List<MProbeEvent> events = getEventsByKey(key);
        for(MProbeEvent event: events) {
            deletePropertiesByParentId(event.getId());
            deleteEvent(event.getId());
        }
    }


    private void deleteEvent(Long id) {
        this.jdbcTemplate.update(
            "DELETE FROM mprobe_event WHERE id = ?", id);
    }

    private void deletePropertiesByParentId(Long id) {
        this.jdbcTemplate.update(
            "DELETE FROM mprobe_event_properties WHERE propertylist_id = ?", id);
    }

    private List<Long> getPropsIds(final List<MEventProperty> props) {
        List<Long> result = new ArrayList<>();
        for(MEventProperty c: props) {
            result.add(c.getId());
        }
        return result;
    }

    private void deleteProps(List<Long> propsIds) {
        for (Long id : propsIds) {
            this.jdbcTemplate.update(
                    "DELETE FROM mprobe_event_properties WHERE id = ? ;", id);
        }
    }

    private Set<String> getPropertiesKeys(List<MEventProperty> properties) {
        Set<String> result = new HashSet<>();
        for(MEventProperty p: properties) {
            result.add(p.getKey());
        }
        return result;
    }

    private void updatePropertiesList(List<MEventProperty> oldProps,
                                List<MEventProperty> newProps,
                                Long parentId) {

        Set<Long> propsToDelete = new HashSet<>();
        List<MEventProperty> propsToCreate = new ArrayList<>(newProps);

        if (newProps != null) {
              for(MEventProperty oldP: oldProps) {
                  for (MEventProperty newP : newProps) {
                      if(oldP.getKey().equals(newP.getKey())) {
                          if(oldP.getValue().equals(newP.getValue())) {
                              propsToCreate.remove(newP);
                          } else {
                              propsToDelete.add(oldP.getId());
                          }
                      }
                  }
              }
        }

        createProperties(propsToCreate, parentId);
        deleteProps(new ArrayList<Long>(propsToDelete));
    }
}
