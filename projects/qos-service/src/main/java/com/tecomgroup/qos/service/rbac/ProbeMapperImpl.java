package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Node;
import com.tecomgroup.qos.domain.rbac.Probe;
import com.tecomgroup.qos.domain.rbac.User;
import com.tecomgroup.qos.service.AgentService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class ProbeMapperImpl extends SubjectMapper<Probe> implements ProbeMapper {
    public static String PROBES_WILD_CARD = "*";

    public ProbeMapperImpl() {
        super(Probe.class);
    }

    @Autowired
    private AgentService agentService;

    private boolean isAllProbesAllowedMode() {
        return getAllSubjectKeys().size() == 1 && getAllSubjectKeys().contains(PROBES_WILD_CARD);
    }

    private Probe getProbeByName(String key) {
        List<String> agentKeysFromDB = agentService.getAllAgentKeysNoFiltering();
        if(agentKeysFromDB != null && agentKeysFromDB.contains(key)) {
            return getSubjectByName(key);
        }
        return null;
    }

    @Override
    public Probe getProbeByKey(String key) {
        if(isAllProbesAllowedMode()) {
            Probe result = getSubjectByName(PROBES_WILD_CARD);
            result.setName(key);
            return result;
        }
        return getProbeByName(key);
    }

    @Override
    public List<String> getAllProbeKeys() {
        if(isAllProbesAllowedMode()) {
            return getAllProbeKeysNotFiltered();
        } else {
            Set<String> agentKeysFromMapping = getAllSubjectKeys();
            agentKeysFromMapping.retainAll(getAllProbeKeysNotFiltered());
            return new LinkedList<>(agentKeysFromMapping);
        }
    }

    @Override
    public List<String> getAllProbeKeysNotFiltered() {
        return agentService.getAllAgentKeysNoFiltering();
    }

    @Override
    public boolean canUserManageProbe(User user, Probe probe) {
        for(Node userNode: user.getNodes()) {
            return getStructure().isParent(userNode, probe.getNodes().get(0));
        }
        return false;
    }
}
