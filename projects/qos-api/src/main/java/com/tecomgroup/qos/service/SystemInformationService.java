/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.BuildInfo;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.exception.SecurityException;

/**
 * Предоставляет информацию о системе
 * 
 * @author abondin
 * 
 */
@WebService
@RemoteServiceRelativePath("springServices/systemInformationService")
public interface SystemInformationService extends Service, RemoteService {
	/**
	 * Получить информацию о билде
	 * 
	 * @return
	 */
	BuildInfo getBuildInfo() throws SecurityException;

	/**
	 * Получить клиентские properties
	 */
	Map<String, Object> getClientProperties();

	/**
	 * Получить список доступных таймзон (их ID)
	 * 
	 * @return
	 */
	List<TimeZoneWrapper> getTimeZoneList();
}
