/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.message;

import java.util.List;

import com.tecomgroup.qos.communication.result.Result;

/**
 * 
 * Сообщение с результатами измерения
 * 
 * @author abondin
 * 
 */
public class ResultMessage extends QoSMessage {

	public enum ResultType {
		SINGLE_VALUE_RESULT, INTERVAL_RESULT
	}

	private String taskKey;

	private ResultType resultType;

	private List<Result> results;

	/**
	 * @return the results
	 */
	public List<Result> getResults() {
		return results;
	}

	/**
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}

	/**
	 * @return the taskKey
	 */
	public String getTaskKey() {
		return taskKey;
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public void setResults(final List<Result> results) {
		this.results = results;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(final ResultType resultType) {
		this.resultType = resultType;
	}

	/**
	 * @param taskKey
	 *            the taskKey to set
	 */
	public void setTaskKey(final String taskKey) {
		this.taskKey = taskKey;
	}

	@Override
	public String toString() {
		return "{task = " + taskKey + ", resultType = " + resultType + "}";
	}
}
