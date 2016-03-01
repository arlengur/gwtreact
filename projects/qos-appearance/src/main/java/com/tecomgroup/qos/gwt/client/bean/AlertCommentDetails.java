/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.bean;

import java.io.Serializable;

import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.gwt.client.style.theme.dark.cell.DarkAlertCommentCellAppearance;

/**
 * A part of {@link MAlertUpdate} to render alert comment in alert details page.
 * 
 * @see {@link DarkAlertCommentCellAppearance}
 * @see {@link AlertUpdateValueProvider}
 * 
 * @author kunilov.p
 * 
 */
public class AlertCommentDetails implements Serializable {
	private static final long serialVersionUID = -6843779114588074153L;

	private String dateTime;

	private String updateType;

	private String author;

	private String comment;

	public AlertCommentDetails() {
		super();
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @return the updateType
	 */
	public String getUpdateType() {
		return updateType;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(final String author) {
		this.author = author;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * @param dateTime
	 *            the dateTime to set
	 */
	public void setDateTime(final String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @param updateType
	 *            the updateType to set
	 */
	public void setUpdateType(final String updateType) {
		this.updateType = updateType;
	}
}
