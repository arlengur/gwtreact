package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MRecordedStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author stroganov.d
 */
@XmlType(propOrder = {"channelId","logo","channelName"})
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ChannelStateHighDetailed extends ChannelStateMinorDetailed {
  
    
    /*public ChannelStateCommonHighDetailed(Long channelId, String channelName) {
        super(channelId, channelName);
    }*/


    public ChannelStateHighDetailed() {
        super();
    }

    public ChannelStateHighDetailed(SetConfiguration config) {
        super(config);
    }

    @Override
    public ParameterState createParameterState(ParameterGroup alertGroup)
    {
        ParameterStateDetailed parameterState=new ParameterStateDetailed();
        parameterState.setGroup(alertGroup);
        return parameterState;
    }


    public void addParameterState(ParameterGroup alertGroup)
    {
        ParameterStateDetailed parameterState=new ParameterStateDetailed();
        parameterState.setGroup(alertGroup);
        parameterStates.put(alertGroup,parameterState);
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}
