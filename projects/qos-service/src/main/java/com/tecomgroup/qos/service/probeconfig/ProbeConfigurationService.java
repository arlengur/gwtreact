package com.tecomgroup.qos.service.probeconfig;

import java.io.IOException;
import java.util.List;

/**
 * Created by stroganov.d on 25.05.2015.
 */
public interface ProbeConfigurationService {
    void restartProbeHardware(String key) throws Exception;

    void restartProbeSoftware(String key) throws Exception;

    void rollback(String key) throws Exception;

    boolean updateConfiguration(String key, String configuration) throws Exception;

    void swUpdate(List<String> keys, String filename) throws Exception;

    List<String> probeSwList() throws IOException;

    String probeConfig(String key) throws IOException;
}
