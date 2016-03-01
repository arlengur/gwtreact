/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.DashboardWidget.HasUpdatableData;
import com.tecomgroup.qos.dashboard.DashboardWidget.WidgetData;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.exception.DuplicateException;
import com.tecomgroup.qos.exception.LimitExceededException;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.exception.SecurityException;
import com.tecomgroup.qos.exception.SecurityException.Reason;

/**
 * 
 * @author abondin
 * 
 */
@RemoteServiceRelativePath("springServices/userService")
public interface UserService extends Service, RemoteService {

	/**
	 * @return all {@link MContactInformation} instances existing in system
	 *         including users and groups.
	 */
	List<MContactInformation> getAllContactInformations();

	/**
	 * 
	 * @return
	 */
	MUser getCurrentUser();

	/**
	 * 
	 * @param username
	 * @return widget dasboard for given user
	 */
	MDashboard getDashboard(String username);

	List<MUser> getLdapUsers();

	/**
	 * 
	 * @param type
	 * @param userId
	 * @param templateName
	 * @return
	 */
	MUserAbstractTemplate getTemplate(TemplateType type, Long userId,
			String templateName);

	/**
	 * 
	 * @param type
	 * @param userId
	 * @return
	 */
	List<? extends MUserAbstractTemplate> getTemplates(TemplateType type,
			Long userId);

	/**
	 * Load data for given widget
	 * 
	 * @param widget
	 * @return
	 */
	<M extends WidgetData> List<M> loadWigetData(HasUpdatableData<M> widget);

	/**
	 * 
	 * @param type
	 * @param userId
	 * @param templateName
	 */
	void removeTemplate(TemplateType type, Long userId, String templateName);

	/**
	 * 
	 * @param template
	 * @return
	 */
	MUserAbstractTemplate saveTemplate(MUserAbstractTemplate template);

	void updateCurrentUser(MUser user);

	/**
	 * Update user widget dashboard
	 * 
	 * @param dashboard
	 * @throws LimitExceededException
	 */
	void updateDashboard(MDashboard dashboard) throws LimitExceededException;

	/**
	 * Changes password of currently authenticated user with the validation
	 * whether provided old password matches existing one.
	 * 
	 * @return true if password change was successful otherwise false.
	 * @param oldPassword
	 * @param newPassword
	 * 
	 * @throws SecurityException
	 *             with the reason {@link Reason#INCORRECT_OLD_PASSWORD}
	 */
	void updatePassword(String oldPassword, String newPassword)
			throws SecurityException;

    void addWidgetToDashboard(DashboardWidget widget) throws LimitExceededException, DuplicateException, SecurityException, ModelSpaceException;

    void removeWidgetFromDashboard(String widgetKey) throws IllegalArgumentException, SecurityException, ModelSpaceException;
}
