/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools;

/**
 * Интерфейс утилиты QoS разработчика.
 * 
 * Любой класс унаследованный от QoSTool будет автоматически добавлен в список
 * достуных операций
 * 
 * @see Tools
 * 
 * @author abondin
 * 
 */
public interface QoSTool {
	/**
	 * Выполнить действие
	 */
	void execute();
	/**
	 * Описание утилиты
	 * 
	 * @return
	 */
	String getDescription();
}
