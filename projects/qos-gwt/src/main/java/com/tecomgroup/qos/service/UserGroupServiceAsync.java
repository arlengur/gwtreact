/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.domain.MUserGroup;

/**
 * @author ivlev.e
 * 
 */
public interface UserGroupServiceAsync {

	void doesGroupExist(String name, AsyncCallback<Boolean> callback);

	void getAllGroups(AsyncCallback<Collection<MUserGroup>> callback);

	void getGroupByName(String name, AsyncCallback<MUserGroup> callback);

	void getGroups(Criterion criterion,
			AsyncCallback<Collection<MUserGroup>> callback);

	void removeGroup(String name, AsyncCallback<Void> callback);

	void removeGroups(Collection<String> names, AsyncCallback<Void> callback);

	void saveOrUpdateGroup(MUserGroup group, AsyncCallback<MUserGroup> callback);

}
