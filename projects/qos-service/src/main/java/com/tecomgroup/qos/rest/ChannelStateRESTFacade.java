package com.tecomgroup.qos.rest;

import com.tecomgroup.qos.rest.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 *
 * @author stroganov.d
 */
@Path("channel/state")
@Component
public class ChannelStateRESTFacade {
    private static final String SESSION_PROBE_CONFIG_KEY="SESSION_PROBE_CONFIG";

    @Qualifier("channelViewService")
    @Autowired
    private ChannelService channelViewService;
    
    public ChannelStateRESTFacade() {
       // service=new ChannelService();
    }


    @GET
    @Path("channels")
    @Produces({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    public ChannelsStateResponse getChannelsSates(
            @DefaultValue("false")
            @QueryParam("includesconfiguration")
            Boolean includesConfiguration) {
        ChannelsStateResponse response=channelViewService.getCommonChannelsSates(includesConfiguration);
        return response;
    }
    
    @GET
    @Path("{channelId}/details")
    @Produces({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    @Consumes
    public ChannelStateCommon getChannelDetailedSate(
            @PathParam("channelId")
            Long channelId) {
        ChannelStateCommon state=channelViewService.getDetailedChannelSate(channelId);
        return state;
    }

    @GET
    @Path("params")
    @Produces({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    public Map<String,ParameterGroup> getChannelsSates() {
        return channelViewService.getParametersGroupConfig();
    }

}
