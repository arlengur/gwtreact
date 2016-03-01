package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MLiveStream;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType(propOrder = {"id","name","logo","groups","streams"})
@JsonPropertyOrder({"id","name","logo","groups","streams"})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SetConfiguration {
    public final List<Probe> probes=new ArrayList<>();
    public Long id;
    public String name;
    public Map<String,String> streams=new HashMap<>();
    public String logo;
    public Set<ParameterGroup> groups;
    public boolean isChannel;
    public Long interval;
    public boolean isFavourite;

    public SetConfiguration() {
    }

    public Set<String> retriveSourceKeys()
    {
        Set<String> keysSet=new HashSet<String>();
        for(Probe probe : probes)
        {
            for(QoSTask task : probe.tasks)
            {
                keysSet.add(task.entityKey);
            }
        }
        return keysSet;
    }

    public Set<String> retrieveTaskKeys()
    {
        Set<String> keys=new HashSet<>();
        for (Probe probe : probes) {
            for (QoSTask task : probe.tasks) {
                keys.add(task.entityKey);
            }
        }
        return keys;
    }

    public void addStream(MLiveStream mstream)
    {
        String url=mstream.getUrl();
        if(url != null && url.startsWith("rtmpt:"))
        {
            streams.put(QoSTask.RTMP_PROTOCOL_URL_KEY,url);
        }else if(url != null && url.endsWith(".m3u8"))
        {
            streams.put(QoSTask.HLS_PROTOCOL_URL_KEY,url);
        }
    }

    public void addStreams(List<MLiveStream> mstreams){
        for (MLiveStream stream : mstreams){
            addStream(stream);
        }
    }
}
