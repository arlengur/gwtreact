package com.tecomgroup.qos.domain.probestatus;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.tecomgroup.qos.util.SimpleUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by uvarov.m on 01.03.2016.
 */

@Entity
@Table(name = "mprobe_event")
public class MProbeEvent implements IsSerializable {

    public enum STATUS {QUEUED, IN_PROGRESS, SCHEDULED, OK, FAILED, TIMEOUT}
    public enum FIELD {AGENT_DISPLAY_NAME}

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @JsonIgnore
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private STATUS status;

    @Column(name = "user_login")
    private String userLogin;

    @ElementCollection
    @CollectionTable(name = "mprobe_event_properties", joinColumns = @JoinColumn(name = "id"))
    protected List<MEventProperty> propertyList;

    @Column(name = "agent_key")
    public String agentKey;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @Column(name = "created_timestamp", nullable = false)
    private Date createdTimestamp;

    private transient MPropertyMap map;

    public MProbeEvent() {
    }

    public MProbeEvent(String eventType) {
        this.eventType = eventType;
    }

    public MProbeEvent(MProbeEvent from, boolean copyProperties) {
        setId(from.getId());
        setKey(from.getKey());
        setEventType(from.getEventType());
        setStatus(from.getStatus());
        setTimestamp(from.getTimestamp());
        setCreatedTimestamp(from.getCreatedTimestamp());
        setAgentKey(from.getAgentKey());
        setUserLogin(from.getUserLogin());
        setCreatedTimestamp(from.getCreatedTimestamp());
        setPropertyList(new ArrayList<MEventProperty>());

        if(copyProperties) {
            if (SimpleUtils.isNotNullAndNotEmpty(from.getPropertyList())) {
                for (MEventProperty p : from.getPropertyList()) {
                    getPropertyList().add(MEventProperty.copy(p));
                }
            }
        }
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getAgentKey() {
        return agentKey;
    }

    public void setAgentKey(String agentKey) {
        this.agentKey = agentKey;
    }

    public List<MEventProperty> getPropertyList() {
        return propertyList;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setPropertyList(List<MEventProperty> propertyList) {
        this.propertyList = propertyList;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof MProbeEvent) {
            MProbeEvent o = (MProbeEvent) object;
            return (this.getKey() == null && o.getKey() == null) || (this.getKey() != null && this.getKey().equals(o.getKey())) &&
                    (this.getEventType() == null && o.getEventType() == null) || (this.getEventType() != null && this.getEventType().equals(o.getEventType())) &&
                    (this.getAgentKey() == null && o.getAgentKey() == null) || (this.getAgentKey() != null && this.getAgentKey().equals(o.getAgentKey())) &&
                    (this.getStatus() == null && o.getStatus() == null) || (this.getStatus() != null && this.getStatus().equals(o.getStatus())) &&
                    (this.getUserLogin() == null && o.getUserLogin() == null) || (this.getUserLogin() != null && this.getUserLogin().equals(o.getUserLogin())) &&
                    ((this.getPropertyList() == null && o.getPropertyList() == null) ||
                            (this.getPropertyList() != null &&
                                    o.getPropertyList() != null &&
                                    this.getPropertyList().containsAll(o.getPropertyList()) &&
                                    o.getPropertyList().containsAll(this.getPropertyList())));
        }

        return false;
    }

    public MEventProperty getProperty(String key) {
        initMap();
        return map.getProperty(key);
    }

    public String getPropertyValue(String key) {
        initMap();
        return map.getPropertyValue(key);
    }

    private synchronized void initMap(){
        if(map == null) {
            map = new MPropertyMap(propertyList);
        }
    }

    public interface EventType {
        String getEventClassName();
    }

    public enum BaseEventType implements EventType {
        EXPORT_VIDEO(MExportVideoEvent.class.getName());

        private final String className;

        private BaseEventType(final String className) {
            this.className = className;
        }

        @Override
        public String getEventClassName() {
            return className;
        }
    }
}
