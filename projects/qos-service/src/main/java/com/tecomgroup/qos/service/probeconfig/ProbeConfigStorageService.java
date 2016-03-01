package com.tecomgroup.qos.service.probeconfig;

import java.io.IOException;
import java.util.List;

/**
 * Created by uvarov.m on 21.01.2016.
 */
public interface ProbeConfigStorageService {
    void uploadProbeConfiguration(String xml, String schema, String agentKey) throws IOException;

    boolean validateProbeConfig(String configuration,String agentKey);

    String getSwFtpUrl(String filename);

    List<String> getSwList () throws IOException;

    String downloadProbeConfiguration (String key) throws IOException;
}
