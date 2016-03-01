package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Probe;
import com.tecomgroup.qos.domain.rbac.User;

import java.util.List;
import java.util.Set;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public interface ProbeMapper {
    public Probe getProbeByKey(String key);
    public List<String> getAllProbeKeys();
    public List<String> getAllProbeKeysNotFiltered();
    public boolean canUserManageProbe(User user, Probe probe);
}
