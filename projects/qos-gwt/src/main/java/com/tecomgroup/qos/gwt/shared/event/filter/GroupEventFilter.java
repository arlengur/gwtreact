/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared.event.filter;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.QoSEventFilter;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class GroupEventFilter implements QoSEventFilter {
	public static enum Type {
		AND, OR
	}
	private Type type;
	private QoSEventFilter left;
	private QoSEventFilter right;

	/**
	 * 
	 */
	public GroupEventFilter() {
		this(null, null);
	}

	/**
	 * 
	 * @param left
	 * @param right
	 * @param type
	 */
	public GroupEventFilter(final QoSEventFilter left,
			final QoSEventFilter right) {
		this(Type.OR, left, right);
	}
	/**
	 * 
	 * @param left
	 * @param right
	 * @param type
	 */
	public GroupEventFilter(final Type type, final QoSEventFilter left,
			final QoSEventFilter right) {
		this.left = left;
		this.right = right;
		this.type = type;
	}
	@Override
	public boolean accept(final AbstractEvent event) {
		return (left == null || right == null) ? false : type == Type.AND
				? left.accept(event) && right.accept(event)
				: left.accept(event) || right.accept(event);
	}

	/**
	 * @return the left
	 */
	public QoSEventFilter getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public QoSEventFilter getRight() {
		return right;
	}
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param left
	 *            the left to set
	 */
	public void setLeft(final QoSEventFilter left) {
		this.left = left;
	}

	/**
	 * @param right
	 *            the right to set
	 */
	public void setRight(final QoSEventFilter right) {
		this.right = right;
	}
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
}
