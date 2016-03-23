package com.tecomgroup.qos.gwt.client.secutiry;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.Gatekeeper;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.MRole;

import java.util.List;

/**
 * Created by uvarov.m on 12.02.2016.
 */
public abstract class BaseGatekeeper implements Gatekeeper {

	protected final CurrentUser currentUser;
	
    public BaseGatekeeper(final CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public static boolean isPermitted(List<UISubject> page, MUser user) {
        for(MRole role: user.getRoles()) {
            if(role.isPermitted(page)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canReveal() {
        final MUser user = currentUser.getUser();
        if(user != null && (isPermitted(getPermission(), user))) {
            return true;
        }
        return false;
    }

    public abstract List<UISubject> getPermission();
}
