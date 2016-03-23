/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.exception.QOSException;

/**
 * Service for user's managing operations, such as creation, updating
 * information, and disabling.
 * 
 * @author meleshin.o
 */
@RemoteServiceRelativePath("springServices/userManagerService")
public interface UserManagerService extends Service, RemoteService {
	/**
	 * Disable users, so they can not authenticate in system.
	 * 
	 * @param userKeys
	 *            - set of user logins for disabling
	 * */
	void disableUsers(Set<String> userKeys);
	/**
	 * Enable users, if they were blocked by an Administrator earlier
	 * 
	 * @param userKeys
	 *            - set of user logins for disabling
	 * */
	void enableUsers(Set<String> userKeys);

	/**
	 * Get all users, include disabled.
	 * */
	List<MUser> getAllUsers();

	List<MUser> getAllUsersNotFiltered();

	/**
	 * Get users by passed criterion, order, and paging (<i>startPosition</i>
	 * and <i>size</i>) options
	 * 
	 * @param criterion
	 *            - users fetch criteria
	 * @param order
	 *            - in which order should fetch user records
	 * @param startPosition
	 *            - from which position service should fetch user records
	 * @param size
	 *            - number of records to show
	 * */
	List<MUser> getUsers(Criterion criterion, Order order,
			Integer startPosition, Integer size);

	/**
	 * Get count of users, which pass certain criteria condtion
	 * 
	 * @param criterion
	 *            - users fetch criteria
	 * */
	Long getUsersCount(Criterion criterion);

	/**
	 * Save new or update existing user
	 * 
	 * @param user
	 *            - user for saving
	 * 
	 * @param updatePassword
	 *            - whether to update password field
	 * */
	MUser saveOrUpdateUser(MUser user, boolean updatePassword)
			throws QOSException;

	void logoutUsers(List<MUser> users);

	List<MRole> getAllRoles();
}
