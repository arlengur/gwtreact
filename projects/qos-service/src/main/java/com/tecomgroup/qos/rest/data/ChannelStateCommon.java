package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType;

/**
 *
 * @author stroganov.d
 */
public abstract class ChannelStateCommon {
    protected long channelId;
    protected String logo;
    protected String channelName;
    protected Long startDate;
    protected Long endDate;
    //protected String defaultStream;

    protected SetConfiguration configuration;

    public ChannelStateCommon() {
    super();
    }

    public ChannelStateCommon(SetConfiguration setConfiguration) {

        this.channelId = setConfiguration.id;
        this.channelName = setConfiguration.name;
        //this.defaultStream=channelConfiguration.streamURL;
        this.logo= setConfiguration.logo;
        this.configuration = setConfiguration;
    }

    public abstract AlertReport updateState(ParameterGroup parameterGroup, MAlertReport report);

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String ChannelName) {
        this.channelName = ChannelName;
    }

    public void setOtherSeverity(MAlertType.PerceivedSeverity otherSeverities) {

    }
/*
   public String getDefaultStream() {
        return defaultStream;
    }

    public void setDefaultStream(String defaultStream) {
        this.defaultStream = defaultStream;
    }*/

    public SetConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SetConfiguration configuration) {
        this.configuration = configuration;
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
