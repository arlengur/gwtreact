/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.dashboard;

import java.io.Serializable;

import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * @author abondin
 * 
 */
public abstract class DashboardWidget implements Serializable {

	/**
	 * 
	 * Notify that the widget provides data, that can be reloaded
	 * 
	 * @author abondin
	 * 
	 * @param <M>
	 */
	public interface HasUpdatableData<M extends WidgetData> {

	}

	/**
	 * Notify that this class can be used as a data for dashboard widget. This
	 * class needed only to avoid GWT serialization issues
	 * 
	 * @author abondin
	 * 
	 */
	public interface WidgetData {
	}

	private static final long serialVersionUID = 8342148182700610836L;

	private int colspan = 1;
	private int rowspan = 1;
	private String title;
	/**
	 * @return the colspan
	 */
	public int getColspan() {
		return colspan;
	}
	/**
	 * Get unique key
	 * 
	 * @return
	 */
	@Transient
	@JsonIgnore
	public abstract String getKey();

	/**
	 * @return the rowspan
	 */
	public int getRowspan() {
		return rowspan;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	@Transient
	@JsonIgnore
	public boolean isEmpty() {
		return false;
	}
	/**
	 * @param colspan
	 *            the colspan to set
	 */
	public void setColspan(final int colspan) {
		this.colspan = colspan;
	}
	/**
	 * @param rowspan
	 *            the rowspan to set
	 */
	public void setRowspan(final int rowspan) {
		this.rowspan = rowspan;
	}
	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
}