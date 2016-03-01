/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.rest.data.Comment;
import com.tecomgroup.qos.service.AlertHistoryRetriever;

/**
 * A service to perform {@link MAlertUpdate} processing. It is internal server
 * API
 * 
 * @author kunilov.p
 * 
 */
public interface AlertHistoryService extends AlertHistoryRetriever {

	/**
	 * Adds alert update with comment.
	 * 
	 * @param alert
	 * @param updateType
	 * @param dateTime
	 * @param user
	 * @param field
	 * @param oldValue
	 * @param newValue
	 * @param comment
	 */
	void addAlertUpdate(MAlert alert, UpdateType updateType, Date dateTime,
			String user, String field, Object oldValue, Object newValue,
			String comment);

    /**
     * For each provided {@link MAlertReport} returns list of all {@link MAlertUpdate} associated
     * with {@link MAlertReport#alert} that happened in the time interval of MAlertReport and has non empty comment field.
     * @param reports map of {@link com.tecomgroup.qos.domain.MAlertReport#id} and all it's MAlertUpdate with non empty comment field
     * @param startDate
     *@param endDate @return
     */
    Map<Long, List<MAlertUpdate>> getAlertReportComments(List<MAlertReport> reports, Date startDate, Date endDate);

	/**
	 * @param alertId
	 * @return
	 */
	List<Comment> getAlertReportComments(Long alertId);
}
