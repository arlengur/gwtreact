package com.tecomgroup.qos.communication.message.recording;

import com.tecomgroup.qos.domain.recording.Schedule;
import com.tecomgroup.qos.domain.recording.xml.Config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;


@SuppressWarnings("ALL")
public class TimetableConfigUpdate implements Serializable {
    private String configuration;

    public TimetableConfigUpdate() {
    }

    public TimetableConfigUpdate(Config configEntity) {
        this.configuration = toXmlString(configEntity);
    }

    private static String toXmlString(Config config) {
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(Config.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(config, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    private static Map<String, List<String>> invertTaskAgentMap(Map<String, String> taskAgentMap) {
        Map<String, List<String>> agentTaskMap = new HashMap<>();
        for(Map.Entry<String, String> entry: taskAgentMap.entrySet()){
            String agentKey = entry.getValue();
            if(agentTaskMap.containsKey(agentKey)) {
                agentTaskMap.get(agentKey).add(entry.getKey());
            } else {
                List<String> taskList = new ArrayList<>();
                taskList.add(entry.getKey());
                agentTaskMap.put(agentKey, taskList);
            }
        }
        return  agentTaskMap;
    }

    public static TimetableConfigUpdate fromEntity(List<Schedule> scheduleList) {
        return new TimetableConfigUpdate(Config.fromEntities(scheduleList));
    }

    public static Map<String, TimetableConfigUpdate> fromEntity(Schedule schedule, Map<String, String> taskAgentMap) {
        Map<String, List<String>> agentTaskMap = invertTaskAgentMap(taskAgentMap);

        Map<String, TimetableConfigUpdate> result = new HashMap<>();

        Set<String> agentKeys = agentTaskMap.keySet();
        for(String agent: agentKeys) {
            TimetableConfigUpdate schedDTO = fromEntity(schedule, agentTaskMap.get(agent));
            result.put(agent, schedDTO);
        }
        return result;
    }

    public static TimetableConfigUpdate fromEntity(Schedule schedule, List<String> taskKeys) {
        return new TimetableConfigUpdate(Config.fromEntity(schedule, taskKeys));
    }
}
