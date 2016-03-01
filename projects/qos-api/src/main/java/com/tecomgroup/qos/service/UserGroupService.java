/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.exception.QOSException;

/**
 * A service to manipulate {@link MUserGroup} objects.
 * 
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/userGroupService")
public interface UserGroupService extends Service, RemoteService {

	/**
	 * Checks whether {@link MUserGroup} with provided name exists.
	 * 
	 * @param name
	 * @return true if {@link MUserGroup} exists otherwise false.
	 * @throws QOSException
	 */
	boolean doesGroupExist(String name) throws QOSException;

	/**
	 * Gets the collection of all {@link MUserGroup}.
	 * 
	 * @return the collection of all {@link MUserGroup} or empty if there is no
	 *         groups.
	 * @throws QOSException
	 */
	Collection<MUserGroup> getAllGroups() throws QOSException;

	/**
	 * Gets {@link MUserGroup} by provided name.
	 * 
	 * @param name
	 * @return {@link MUserGroup} or null if there is no group with provided
	 *         name.
	 * @throws QOSException
	 */
	MUserGroup getGroupByName(String name) throws QOSException;

	/**
	 * Gets the collection of {@link MUserGroup} by provided {@link Criterion}.
	 * 
	 * @param criterion
	 * @return the collection of {@link MUserGroup}.
	 * @throws QOSException
	 */
	Collection<MUserGroup> getGroups(Criterion criterion) throws QOSException;

	/**
	 * Removes group by its name.
	 * 
	 * @param name
	 * @throws QOSException
	 */
	void removeGroup(String name) throws QOSException;

	/**
	 * Removes groups by its names.
	 * 
	 * @param names
	 * @throws QOSException
	 */
	void removeGroups(Collection<String> names) throws QOSException;

	/**
	 * Saves or updates provided group.
	 * 
	 * @param group
	 * @throws QOSException
	 */
	MUserGroup saveOrUpdateGroup(MUserGroup group) throws QOSException;
}
