/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget;

/**
 * 
 * Общий интерфейс презентера для таблиц.
 * 
 * @author abondin
 * 
 */
public interface GridPresenter {
	/**
	 * Reload the data grid
	 * 
	 * @param force
	 *            - try to force reload data
	 */
	void reload(boolean force);
}
