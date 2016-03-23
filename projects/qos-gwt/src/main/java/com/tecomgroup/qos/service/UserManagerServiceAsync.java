/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.MRole;

/**
 * @author meleshin.o
 * 
 */
public interface UserManagerServiceAsync {
	void disableUsers(Set<String> userKeys, AsyncCallback<Void> callback);

	void enableUsers(Set<String> userKeys, AsyncCallback<Void> callback);

	void getAllUsersNotFiltered(AsyncCallback<List<MUser>> callback);

	void getAllUsers(AsyncCallback<List<MUser>> callback);

	void getUsers(Criterion criterion, Order order, Integer startPosition,
			Integer size, AsyncCallback<List<MUser>> callback);

	void getUsersCount(Criterion criterion, AsyncCallback<Long> callback);

	void saveOrUpdateUser(MUser user, boolean updatePassword,
			AsyncCallback<MUser> callback);

	void getAllRoles(AsyncCallback<List<MRole>> callback);

	void logoutUsers(List<MUser> users, AsyncCallback<Void> callback);
}
