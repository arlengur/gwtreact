package com.tecomgroup.qos.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.exception.SourceNotFoundException;
import com.tecomgroup.qos.service.DefaultSystemComponentStatisticService;
import com.tecomgroup.qos.service.probeconfig.ProbeConfigurationService;
import com.tecomgroup.qos.util.AuditLogger;

/**
 * Created by stroganov.d on 22.05.2015.
 */
@Path("probe-config")
@Component
public class ProbeConfigurationRESTFacade {

    private final static Logger LOGGER = Logger
            .getLogger(ProbeConfigurationRESTFacade.class);
    @Context
    UriInfo uri;

    @Qualifier("probeConfigurationService")
    @Autowired
    private ProbeConfigurationService probeConfigService;

    @Autowired
    private DefaultSystemComponentStatisticService statisticService;

    @GET
    @Path("stats")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public Map<String, AgentStatistic> getProbesStatistic() {
        return statisticService.getAgentsStatistic();
    }

    @POST
    @Path("/restart-sw")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<String> restartProbeSoftware(@QueryParam("keys") List<String> keys) {
        for(String key : keys) {
            try {
                probeConfigService.restartProbeSoftware(key);
            } catch (Exception e) {
                //@TODO process errors
                LOGGER.error("Unable to sent restart to probe : " + keys, e);
                AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.NOK,"Unable to sent software restart to probe: {} ", key);
            }
        }
        AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK,"Software restart of probes: {} ", Arrays.toString(keys.toArray(new String[keys.size()])));
        return keys;
    }

    @POST
    @Path("/restart-hw")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<String> restartProbeHardware(@QueryParam("keys") List<String> keys) {
        for(String key : keys) {
            try {
                probeConfigService.restartProbeHardware(key);
                AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK,"Hardware restart of probes: {} ", Arrays.toString(keys.toArray(new String[keys.size()])));
            } catch (Exception e) {
                //@TODO process errors
                LOGGER.error("Unable to sent restart to probe : " + key, e);
                AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.NOK,"Unable to sent hardware restart to probes: {} ", Arrays.toString(keys.toArray(new String[keys.size()])));
            }
        }
        return keys;
    }

    @POST
    @Path("/rollback")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<String> rollbackProbeConfiguration(@QueryParam("keys") List<String> keys) {
        for(String key : keys) {
            try {
                probeConfigService.rollback(key);
                AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK,"Rollback configuration for probes: {} ", Arrays.toString(keys.toArray(new String[keys.size()])));
            } catch (Exception e) {
                //@TODO process errors
                LOGGER.error("Unable to rollback configuration for probes : " + key, e);
                AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.NOK,"Unable to rollback configuration for probes: {} ", Arrays.toString(keys.toArray(new String[keys.size()])));
            }
        }
        return keys;
    }

    @POST
    @Path("/update-sw")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<String> swUpdate(@QueryParam("keys") List<String> keys,
                                 @QueryParam("sw-fileName") String fileName) throws Exception {
        try {
            probeConfigService.swUpdate(keys, fileName);
            AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK,"Software update for probes: {} , file : {}  ", Arrays.toString(keys.toArray(new String[keys.size()])),fileName);
        } catch (Exception e) {
            LOGGER.error("Unable to update SW", e);
            AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.NOK,"Unable to software update for probes: {} , file : {}  ", Arrays.toString(keys.toArray(new String[keys.size()])),fileName);
            throw e;
        }
        return keys;
    }

    @POST
    @Path("/update_config")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<String> updateProbeConfiguration(@QueryParam("keys") List<String> keys,
                                                 @FormDataParam("file") InputStream fileInputStream,
                                                 @FormDataParam("file") FormDataContentDisposition fileDisposition) {
        StringBuilderWriter writer = null;
        String configuration = "";
        try {
            writer = new StringBuilderWriter();
            IOUtils.copy(fileInputStream, writer, "UTF-8");
            configuration = writer.toString();
        } catch (Exception e) {
            LOGGER.error("Unable to read configuration stream", e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(writer);
        }
        List<String> failedKeys = new ArrayList<>();
        for(String key : keys) {
            try {
               boolean result = probeConfigService.updateConfiguration(key, configuration);
               if(!result) {
                   failedKeys.add(key);
               }
            } catch (Exception e) {
                failedKeys.add(key);
                //@TODO process errors
                LOGGER.error("Unable to update probe : " + key, e);
            }
        }
        AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK,"Update configuration for probes: {} , failed updates for : {}  ", Arrays.toString(keys.toArray(new String[keys.size()])),Arrays.toString(failedKeys.toArray(new String[failedKeys.size()])));
        if(failedKeys.isEmpty()) {
            return keys;
        } else {
            return failedKeys;
        }
    }

    @GET
    @Path("/probe-sw-list")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<String> probeSwList() throws IOException {
        return probeConfigService.probeSwList();
    }

    @GET
    @Path("/download_config")
    @Produces({MediaType.APPLICATION_XML + "; charset=UTF-8"})
    public Response downloadProbeConfiguration(@QueryParam("key") String key) {
        try {
            String configurationXml = probeConfigService.probeConfig(key);
            return Response.status(Response.Status.OK)
                    .entity(configurationXml)
                    .build();
        } catch (SourceNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Probe configuration not found.")
                    .build();
        } catch (IOException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Probe configuration unavailable.")
                    .build();
        }
    }
}
