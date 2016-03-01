/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.results;

import java.util.Date;

import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages;

/**
 * @author ivlev.e
 * 
 */
@SuppressWarnings("serial")
public class TaskRow implements ResultRow {

	private final String displayName;
	private final String key;
    private final boolean multipleProgramBitrateTask;
	private Date date;

	public TaskRow(final String key, final String displayName, final boolean multipleProgramBitrateTask) {
		this.key = key;
		this.displayName = displayName;
        this.multipleProgramBitrateTask = multipleProgramBitrateTask;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public String getFormatedValue(final FormattedResultMessages messages) {
		return null;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getName() {
		return displayName;
	}

	@Override
	public Double getValue() {
		return null;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

    public boolean isMultipleProgramBitrateTask() {
        return multipleProgramBitrateTask;
    }
}
