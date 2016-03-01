package com.tecomgroup.qos.rest.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by stroganov.d on 17.02.2015.
 */
@XmlType
@XmlRootElement
public class ChannelsStateResponse {
    public ViewConfiguration viewConfiguration;
    public final List<ChannelStateCommon> channelsState=new ArrayList<>();

    public ChannelsStateResponse() {
    }

    public ChannelsStateResponse(ViewConfiguration viewConfiguration, ChannelStateCommon[] states) {
        this.viewConfiguration = viewConfiguration;
        for (int i=0;i<states.length;i++)
        {
            channelsState.add(states[i]);
        }
    }

    public ChannelsStateResponse(ViewConfiguration viewConfiguration, Collection<ChannelStateCommon> states) {
        this.viewConfiguration = viewConfiguration;
        channelsState.addAll(states);
    }
}
