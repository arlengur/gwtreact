/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.VideoResult;
import com.tecomgroup.qos.domain.MVideoResult;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/videoService")
public interface VideoResultService extends Service, RemoteService {

	/**
	 * Adds video results.
	 * 
	 * @param taskKey
	 * @param results
	 */
	void addResults(String taskKey, List<VideoResult> results);

	/**
	 * Gets MVideoResults by source. Source must be complex stream source.
	 * 
	 * @param streamSource
	 * @param startPosition
	 * @param size
	 * @return
	 */
	List<MVideoResult> getResults(Source streamSource,
			TimeInterval timeInterval, Integer startPosition, Integer size);
}
