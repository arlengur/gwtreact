/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MUser;

/**
 * @author ivlev.e
 * 
 */
public interface InternalUserService extends UserService {

	MUser findUser(String login);

	List<MDashboard> getAllDashboards();
}
