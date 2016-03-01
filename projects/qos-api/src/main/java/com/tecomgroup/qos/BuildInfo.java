/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * Информация о билде системы
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class BuildInfo implements Serializable {

	private String branch;
	private String commitId;
	private String buildUserName;
	private String buildUserEmail;
	private Date buildTime;
	private String commitUserName;
	private String commitUserEmail;
	private String commitMessageShort;
	private String commitMessageFull;
	private Date commitTime;
	private String applicationVersion;

	private String buildVersion;

	/**
	 * 
	 */
	public BuildInfo() {
	}

	/**
	 * @param branch
	 * @param commitId
	 * @param buildUserName
	 * @param buildUserEmail
	 * @param buildTime
	 * @param commitUserName
	 * @param commitUserEmail
	 * @param commitMessageShort
	 * @param commitMessageFull
	 * @param commitTime
	 * @param applicationVersion
	 */
	public BuildInfo(final String branch, final String commitId,
			final String buildUserName, final String buildUserEmail,
			final Date buildTime, final String commitUserName,
			final String commitUserEmail, final String commitMessageShort,
			final String commitMessageFull, final Date commitTime,
			final String applicationVersion, final String buildVersion) {
		super();
		this.branch = branch;
		this.commitId = commitId;
		this.buildUserName = buildUserName;
		this.buildUserEmail = buildUserEmail;
		this.buildTime = buildTime;
		this.commitUserName = commitUserName;
		this.commitUserEmail = commitUserEmail;
		this.commitMessageShort = commitMessageShort;
		this.commitMessageFull = commitMessageFull;
		this.commitTime = commitTime;
		this.applicationVersion = applicationVersion;
		this.buildVersion = buildVersion;
	}

	/**
	 * @return the applicationVersion
	 */
	public String getApplicationVersion() {
		return applicationVersion;
	}
	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * @return the buildTime
	 */
	public Date getBuildTime() {
		return buildTime;
	}
	/**
	 * @return the buildUserEmail
	 */
	public String getBuildUserEmail() {
		return buildUserEmail;
	}

	/**
	 * @return the buildUserName
	 */
	public String getBuildUserName() {
		return buildUserName;
	}

	/**
	 * @return the buildVersion
	 */
	public String getBuildVersion() {
		return buildVersion;
	}

	/**
	 * @return the commitId
	 */
	public String getCommitId() {
		return commitId;
	}

	/**
	 * @return the commitMessageFull
	 */
	public String getCommitMessageFull() {
		return commitMessageFull;
	}

	/**
	 * @return the commitMessageShort
	 */
	public String getCommitMessageShort() {
		return commitMessageShort;
	}

	/**
	 * @return the commitTime
	 */
	public Date getCommitTime() {
		return commitTime;
	}

	/**
	 * @return the commitUserEmail
	 */
	public String getCommitUserEmail() {
		return commitUserEmail;
	}

	/**
	 * @return the commitUserName
	 */
	public String getCommitUserName() {
		return commitUserName;
	}

	/**
	 * @param applicationVersion
	 *            the applicationVersion to set
	 */
	public void setApplicationVersion(final String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	@Override
	public String toString() {
		return branch + ":" + commitId + ":" + commitUserName;
	}
}
