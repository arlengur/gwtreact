package com.tecomgroup.qos.rest.data;

/**
 * Created by uvarov.m on 13.01.2016.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class QoSTaskBase {
    public long id;
    public String name;
    public String entityKey;
    public String agentKey;
    public String agentName;
    public String moduleName;
    public String timeZone;
    public String scheduleState;
    public String roundRobin;

    public static QoSTaskBase populateTaskInfo(Map<String, String> prop) {
        QoSTaskBase p = new QoSTaskBase();
        p.agentKey = prop.get("agent_key");
        p.agentName = prop.get("agent_name");
        p.entityKey = prop.get("task_key");
        p.name = prop.get("task_name");
        p.moduleName = prop.get("module");
        p.timeZone = prop.get("time_zone");
        p.scheduleState = prop.get("schedule_state");
        p.roundRobin = prop.get("round_robin");
        return p;
    }
}
