package com.tecomgroup.qos.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.service.ResultService;
import com.tecomgroup.qos.service.TaskService;
import com.tecomgroup.qos.util.SimpleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.Map.Entry;

@Path("widgets")
@Component
public class Widgets {
    @Autowired
    private ResultService resultService;
    @Autowired
    private TaskService taskService;

    public static final String AUDIO_BITRATE = "audioBitrate";
    public static final String DATA_BITRATE = "dataBitrate";
    public static final String VIDEO_BITRATE = "videoBitrate";
    public static final Set<String> BITRATE_PARAMETERS =
            new ImmutableSet.Builder<String>().add(AUDIO_BITRATE, DATA_BITRATE, VIDEO_BITRATE).build();

    @GET
    @Path("bitrate")
    @Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
    public List<Map> getBitrateWidgetData(@QueryParam("taskKey") String taskKey) {
        MAgentTask task = taskService.getTaskByKey(taskKey);

        final Map<String, Collection<?>> taskParameters = new HashMap<>();
        taskParameters.put(taskKey, null);

        List<Map<String, Object>> allResults =
                resultService.getLastResults(taskParameters, 0l, 1l, OrderType.DESC);

        // Most of the code below is required to overcome inefficient API
        // see http://redmine.qligent.ru/issues/6613
        final Map<String, String> storageKeyToProgramName = new HashMap<>();
        final Map<String, String> storageKeyToParamName = new HashMap<>();
        for (final MResultParameterConfiguration parameter : task.getResultConfiguration().getParameterConfigurations()) {
            String paramName = parameter.getName();
            if (BITRATE_PARAMETERS.contains(paramName)) {
                final String taskStorageKey = parameter.getParameterIdentifier().createTaskStorageKey(task.getKey());
                String programName = parameter.getProperty("programName").getValue();
                storageKeyToProgramName.put(taskStorageKey, programName);
                storageKeyToParamName.put(taskStorageKey, paramName);
            }
        }
        final Set<String> bitrateKeys = storageKeyToProgramName.keySet();

        final Map<String, Double> storageKeyToValue = new HashMap<>();
        for (final Map<String, Object> taskResults : allResults) {
            for (final Entry<String, Object> taskResultEntry : taskResults.entrySet()) {
                final String propertyName = taskResultEntry.getKey();
                if(bitrateKeys.contains(propertyName)) {
                    storageKeyToValue.put(propertyName, (Double) taskResultEntry.getValue());
                }
            }
        }

        final Map<String, Map<String, Double>> bitratesByProgram = new HashMap<>();
        for (String storageKey : bitrateKeys) {
            String programName = storageKeyToProgramName.get(storageKey);
            String paramName = storageKeyToParamName.get(storageKey);
            Double value = storageKeyToValue.get(storageKey);
            SimpleUtils.updateNestedMap(bitratesByProgram, programName, paramName, value);
        }

        List<Map> list = new ArrayList<>();
        for (Entry<String, Map<String, Double>> entry : bitratesByProgram.entrySet()) {
            final Map<String, Double> bitrates = entry.getValue();
            List<Map> bitratesConverted = new ArrayList<>();
            bitratesConverted.add(
                    new ImmutableMap.Builder<>()
                            .put("id", "audio")
                            .put("value", bitrates.get(AUDIO_BITRATE))
                            .build());
            bitratesConverted.add(
                    new ImmutableMap.Builder<>()
                            .put("id", "data")
                            .put("value", bitrates.get(DATA_BITRATE))
                            .build());
            bitratesConverted.add(
                    new ImmutableMap.Builder<>()
                            .put("id", "video")
                            .put("value", bitrates.get(VIDEO_BITRATE))
                            .build());
            Map channelBitrates = new HashMap();
            channelBitrates.put("id", entry.getKey());
            channelBitrates.put("data", bitratesConverted);
            list.add(channelBitrates);
        }
        return list;
    }
}
