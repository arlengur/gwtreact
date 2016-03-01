/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.results;

import java.util.Date;

import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

/**
 * Модель данных для результатов агента при отображении в TreeGrid
 * 
 * @author ivlev.e
 * 
 */
public interface ResultRow extends TreeGridRow {
	Date getDate();

	String getFormatedValue(FormattedResultMessages messages);

	Double getValue();
}
