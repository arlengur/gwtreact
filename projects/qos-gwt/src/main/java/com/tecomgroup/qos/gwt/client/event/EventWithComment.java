/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.EventWithComment.EventWithCommentHandler;

/**
 * Event with comment.
 * 
 * @author novohatskiy.r
 * 
 */
public abstract class EventWithComment<T extends EventWithCommentHandler>
		extends
			GwtEvent<T> {

	public static interface EventWithCommentHandler extends EventHandler {
	}

	private String comment;

	public EventWithComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

}
