package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Node;
import com.tecomgroup.qos.domain.rbac.User;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class UserMapperImpl extends SubjectMapper<User> implements UserMapper{
    public UserMapperImpl() {
        super(User.class);
    }

    public boolean isSubordinate(User boss, User subordinate) {
        if(boss.equals(subordinate)) return true;

        for(Node bossNode: boss.getNodes()) {
            for(Node subNode: subordinate.getNodes()) {
                if (getStructure().isParent(bossNode, subNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSubordinate(String bossLogin, String subordinateLogin) {
        User boss = getUserByLogin(bossLogin);
        User subordinate = getUserByLogin(subordinateLogin);
        if(boss != null && subordinate != null) {
            return isSubordinate(boss, subordinate);
        }
        return false;
    }

    public User getUserByLogin(String login) {
        return getSubjectByName(login);
    }
}
