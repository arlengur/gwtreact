package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.User;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public interface UserMapper {
    public boolean isSubordinate(String bossLogin, String subordinateLogin);
    public boolean isSubordinate(User boss, User subordinate);
    public User getUserByLogin(String login);
}
