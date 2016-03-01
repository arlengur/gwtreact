/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.VideoResult;
import com.tecomgroup.qos.domain.MVideoResult;
import com.tecomgroup.qos.domain.Source;

/**
 * @author novohatskiy.r
 * 
 */
public interface VideoResultServiceAsync {

	void addResults(String taskKey, List<VideoResult> results,
			AsyncCallback<Void> callback);

	void getResults(Source streamSource, TimeInterval timeInterval,
			Integer startPosition, Integer size,
			AsyncCallback<List<MVideoResult>> callback);

}
