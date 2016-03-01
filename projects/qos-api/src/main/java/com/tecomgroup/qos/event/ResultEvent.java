/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;


/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class ResultEvent extends AbstractEvent {
	private String agentName;
	private String taksKey;
	private int resultCount;
	/**
	 * @param agentName
	 * @param taksKey
	 * @param results
	 */
	public ResultEvent(final String agentName, final String taksKey,
			final int resultCount) {
		super();
		this.agentName = agentName;
		this.taksKey = taksKey;
		this.resultCount = resultCount;
	}
	/**
	 * @return the agentName
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * @return the resultCount
	 */
	public int getResultCount() {
		return resultCount;
	}

	/**
	 * @return the taksKey
	 */
	public String getTaksKey() {
		return taksKey;
	}

	/**
	 * @param agentName
	 *            the agentName to set
	 */
	public void setAgentName(final String agentName) {
		this.agentName = agentName;
	}
	/**
	 * @param resultCount
	 *            the resultCount to set
	 */
	public void setResultCount(final int resultCount) {
		this.resultCount = resultCount;
	}
	/**
	 * @param taksKey
	 *            the taksKey to set
	 */
	public void setTaksKey(final String taksKey) {
		this.taksKey = taksKey;
	}

}
