/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.SortedMap;

import com.tecomgroup.qos.communication.handler.ResultHandler;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.communication.result.Result.ResultIdentifier;
import com.tecomgroup.qos.domain.MAgentTask;

/**
 * Сервис для работы с результами измерений
 * 
 * @author abondin
 * 
 */
public interface ResultService extends ResultRetriever, ResultHandler {

	/**
	 * Adds results for provided task.
	 * 
	 * @param task
	 * @param results
	 *            <ResultIdentifier, Result>
	 */
	void addResults(MAgentTask task, SortedMap<ResultIdentifier, Result> results);
}
