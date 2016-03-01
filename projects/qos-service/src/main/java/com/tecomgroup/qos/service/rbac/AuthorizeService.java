package com.tecomgroup.qos.service.rbac;

import java.util.List;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public interface AuthorizeService {
    public List<String> filterProbes(String login, List<String> probes);
    public boolean isPermittedProbes(List<String> probes);
    public List<String> getProbeKeysUserCanManage();

    public boolean isSubordinate(String user);
}
