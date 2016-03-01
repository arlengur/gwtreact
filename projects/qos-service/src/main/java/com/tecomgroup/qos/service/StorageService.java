/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import org.rrd4j.core.RrdDb;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author kunilov.p
 * 
 */
public interface StorageService extends Service {

	public interface RrdRequest {
		Object execute(RrdDb rrdDb);
	}

	/**
	 * Executes {@link RrdRequest}
	 * 
	 * @param task
	 * @param parameterIdentifier
	 * @param createStorage
	 *            - true if it is necessary to create storage on disk in case it
	 *            was not created before with the same parameters.
	 * @param releaseStorage
	 *            - true if it is necessary to release {@link RrdDb} after
	 *            execution.
	 * @param startTime
	 *            - null if createStorage false
	 * @param request
	 * @return
	 */
	Object executeInRRD(MAgentTask task,
			ParameterIdentifier parameterIdentifier, Boolean createStorage,
			Boolean releaseStorage, Date startTime,
			StorageService.RrdRequest request);

	/**
	 * Executes {@link RrdRequest} and releases {@link RrdDb} after execution.
	 * 
	 * @param task
	 * @param parameterIdentifier
	 * @param createStorage
	 *            - true if it is necessary to create storage on disk in case it
	 *            was not created before with the same parameters.
	 * @param startTime
	 *            - null if createStorage false
	 * @param request
	 * @return
	 */
	Object executeInRRD(MAgentTask task,
			ParameterIdentifier parameterIdentifier, Boolean createStorage,
			Date startTime, RrdRequest request);
	/**
	 * 
	 * @param task
	 * @param parameterIdentifier
	 * @param createStorage
	 *            - true if it is necessary to create storage on disk in case it
	 *            was not created before with the same parameters.
	 */
	MResultConfiguration initStorage(MAgentTask task,
			ParameterIdentifier parameterIdentifier, Boolean createStorage,
			Date startTime);

	/**
	 * 
	 * @param task
	 * @param parameterIdentifier
	 */
	void removeStorage(MAgentTask task, ParameterIdentifier parameterIdentifier);


    /**
     *
     * @param task
     */
	void removeStorages(MAgentTask task);

	/**
	 * 
	 * @param storageHome
	 */
	void setStorageHome(String storageHome);

	/**
	 * @param agentTask
	 * @param newSamplingRate
	 */
	List<Future<?>> updateSamplingRate(MAgentTask agentTask, Long newSamplingRate);

	/**
	 * @param fullFilePath
	 */
	RrdDb openRrdDb(final String fullFilePath);
}
