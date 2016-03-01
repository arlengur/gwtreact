/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.domain.*;

/**
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/agentService")
public interface MediaAgentService extends AgentService {

	/**
	 * 
	 * @param agentName
	 * @return
	 */
	List<MLiveStream> getAgentLiveStreams(String agentName);

	/**
	 * 
	 * @param agentName
	 * @return
	 */
	List<MRecordedStream> getAgentRecordedStreams(String agentName);

    List<MLiveStreamWrapper> getTasksLiveStreams(List<Long> taskIds);

    List<MRecordedStreamWrapper> getTasksRecordedStreams(List<Long> taskIds);

    /**
	 * 
	 * @param agentName
	 * @param moduleName
	 * @return
	 */
	MMediaAgentModule getMediaModule(String agentName, String moduleName);

	/**
	 * 
	 * @param alert
	 * @return related to this alert stream or null
	 */
	MRecordedStream getRelatedStream(MAlert alert);

	/**
	 * 
	 * @param task
	 * @param streamKey
	 * @return
	 */
	MStream getStream(MAgentTask task, String streamKey);
}
