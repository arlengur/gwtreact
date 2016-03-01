package com.tecomgroup.qos.rest;

import com.tecomgroup.qos.exception.DuplicateException;
import com.tecomgroup.qos.rest.data.FilePath;
import com.tecomgroup.qos.rest.data.Probe;
import com.tecomgroup.qos.rest.data.SetConfiguration;
import com.tecomgroup.qos.rest.data.ViewConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.List;
import java.util.Set;


/**
 *
 * @author stroganov.d
 */
@Path("channel/config")
@Component
public class ChannelConfigurationRESTFacade {

    @Qualifier("channelViewService")
    @Autowired
    private ChannelService channelViewService;

    public ChannelConfigurationRESTFacade() {
        //service = new ChannelService();
    }

    @javax.ws.rs.core.Context
    ServletContext context;

    @POST
    @Produces({MediaType.TEXT_PLAIN + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    public Response createChannelConfiguration(SetConfiguration channelConfig) {
        try {
            Long channelID = channelViewService.createUserChannel(channelConfig);
            return Response.status(Response.Status.CREATED)// 201
                           .entity("A new Channel has been created")
                           .header("ChannelID", channelID).build();
        } catch (DuplicateException ex) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Channel name already exist")
                           .build();
        }
    }

    @PUT
    @Produces({MediaType.TEXT_PLAIN + "; charset=UTF-8"})
    @Consumes({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    public Response updateChannelsConfiguration(SetConfiguration channelConfig) {
         Long channelID = channelViewService.updateChannel(channelConfig);
        return Response
                .status(Response.Status.OK)
                // 200
                .entity("A channel has been updated")
                .header("ChannelID", channelID).build();
    }


    @GET
    @Path("user")
    @Produces({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    public UserDetails getCurrentUser() {
        return channelViewService.getCurrentUser();
    }

    @GET
    @Path("pages")
    @Produces({MediaType.APPLICATION_JSON+"; charset=UTF-8"})
    public Set<String> getPagesToNavigate() {
        Set<String> result = channelViewService.getPagesToNavigate();
        return result;
    }

    @DELETE
    @Path("{id}")
    @Produces({MediaType.TEXT_HTML+"; charset=UTF-8"})
    public Response deleteChannelConfiguration(@PathParam("id") Long id) {
        channelViewService.deleteChannelById(id);
        return Response.status(Response.Status.NO_CONTENT)// 204
                .entity("Channels successfully removed from database").build();
    }

    @GET
    @Path("{setId}")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public SetConfiguration getSetConfiguration(@PathParam("setId") Long channelId) {
        return channelViewService.getChannelById(channelId);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public SetConfiguration[] getAllSetsConfiguration() {
        return channelViewService.getAllUserSets();
    }

    @GET
    @Path("view")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public ViewConfiguration getSetConfiguration() {
        return channelViewService.getUserViewConfig();
    }

    @GET
    @Path("probes")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Probe[] getProbesConfiguration() {
        return channelViewService.getProbesConfig(true);
    }

    @POST
    @Path("img/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public FilePath uploadFile(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition)
            {
                return channelViewService.fileUpload(fileInputStream,
                        fileDisposition.getFileName(),
                        context.getRealPath(File.separator));
            }

    @PUT
    @Path("favourite/{id}")
    @Produces({MediaType.TEXT_HTML+"; charset=UTF-8"})
    public Response setChannelFavourite(@PathParam("id") Long id,
                                        @HeaderParam("isFavourite") boolean isFavourite) {

        channelViewService.setChannelFavourite(id, isFavourite);
        return Response
                .status(Response.Status.OK)
                        // 200
                .entity("A channel has been updated")
                .header("ChannelID", id).build();
    }

}
