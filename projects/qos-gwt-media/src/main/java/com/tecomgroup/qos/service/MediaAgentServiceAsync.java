/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.domain.*;

/**
 * @author abondin
 * 
 */
public interface MediaAgentServiceAsync extends AgentServiceAsync {

	void getAgentLiveStreams(String agentName,
			AsyncCallback<List<MLiveStream>> callback);

	void getAgentRecordedStreams(String agentName,
			AsyncCallback<List<MRecordedStream>> callback);

	void getMediaModule(String agentName, String moduleName,
			AsyncCallback<MMediaAgentModule> callback);

	void getRelatedStream(MAlert alert, AsyncCallback<MRecordedStream> callback);

	void getStream(MAgentTask task, String streamKey,
			AsyncCallback<MStream> callback);

    void getTasksLiveStreams(List<Long> taskIds,
                             AsyncCallback<List<MLiveStreamWrapper>> callback);

    void getTasksRecordedStreams(List<Long> taskIds,
                                 AsyncCallback<List<MRecordedStreamWrapper>> callback);
}
