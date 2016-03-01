package com.tecomgroup.qos.gwt.client.secutiry;

import com.tecomgroup.qos.domain.MUser;

/**
 * Created by uvarov.m on 12.02.2016.
 */
public class BaseGatekeeper {

    public static boolean isPermittedPage(MUser.Page page, MUser user) {
        for(MUser.Role role: user.getRoles()) {
            if(role.isPermittedPage(page)) {
                return true;
            }
        }
        return false;
    }
}
