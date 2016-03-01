package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.Probe;
import com.tecomgroup.qos.domain.rbac.User;
import com.tecomgroup.qos.service.UserService;
import com.tecomgroup.qos.util.SimpleUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class AuthorizeServiceImpl implements AuthorizeService {
    private final static Logger LOGGER = Logger.getLogger(AuthorizeServiceImpl.class);

    private UserService userService;
    private UserMapper userMapper;
    private ProbeMapper probeMapper;

    public void setUserMapper(UserMapperImpl userMapper) {
        this.userMapper = userMapper;
    }
    public void setProbeMapper(ProbeMapperImpl probeMapper) {
        this.probeMapper = probeMapper;
    }
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<String> filterProbes(String login, List<String> probes) {
        List<String> result = new LinkedList<>();
        User user = userMapper.getUserByLogin(login);
        if(user == null) return result;

        for(String p: probes) {
            Probe probe = probeMapper.getProbeByKey(p);
            if(probe != null && probeMapper.canUserManageProbe(user, probe)) {
                result.add(probe.getName());
            }
        }
        return result;
    }

    @Override
    public boolean isPermittedProbes(List<String> probes) {
        if (userService.getCurrentUser() == null) {
            return false;
        }
        return SimpleUtils.isNotNullAndNotEmpty(filterProbes(userService.getCurrentUser().getLogin(), probes));
    }

    @Override
    public List<String> getProbeKeysUserCanManage() {
        MUser user = userService.getCurrentUser();
        if (user == null || user.hasRole(MUser.Role.ROLE_SUPER_ADMIN)) {
            return probeMapper.getAllProbeKeysNotFiltered();
        }
        return filterProbes(user.getLogin(), probeMapper.getAllProbeKeys());
    }

    @Override
    public boolean isSubordinate(String userLogin) {
        MUser user = userService.getCurrentUser();
        if (user == null || user.hasRole(MUser.Role.ROLE_SUPER_ADMIN)) {
            return true;
        }

        return userMapper.isSubordinate(user.getLogin(), userLogin);
    }
}
