/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

/**
 * Список всех URL системы
 * 
 * Нам нужны константы чтобы передать их в анотации @NameToken<br>
 * Нам нужны методы для ui.xml файлов
 * 
 * @author abondin
 * 
 */
public class QoSNameTokens {

	/**
	 * Основная форма
	 */
	public final static String main = "main";
	/**
	 * Домашняя страница
	 */
	public final static String probesAndTasks = "probesAndTasks";
	/**
	 * GIS
	 */
	public final static String gis = "gis";
	/**
	 * Сообщения об авариях
	 */
	public final static String alerts = "alerts";
	/**
	 * Детальная информация по аварии
	 */
	public final static String alertDetails = "alertDetails";
	/**
	 * Оповещения
	 */
	public final static String policies = "policies";
	/**
	 * Вход в систему
	 */
	public final static String login = "login";
	/**
	 * Мониторинг
	 */
	public final static String monitoring = "monitoring";

	/**
	 * Результаты в виде графиков
	 */
	public final static String chartResults = "charts";

	/**
	 * Результаты в табличном виде по данным с графика
	 */
	public final static String tableResults = "tableResults";

	/**
	 * Страница блока контроля
	 */
	public final static String agentStatus = "probeStatus";

	/**
	 * Страница 404
	 */
	public final static String page404 = "404";

	/**
	 * Профиль пользователя
	 */
	public final static String userProfile = "user";

	/**
	 * Менеджер пользователей для администратора
	 * */
	public final static String users = "users";

	/**
	 * Отчеты
	 */
	public final static String reports = "reports";

	/**
	 * Приборная панель управления
	 */
	public final static String dashboard = "dashboard";

	/**
	 * Channel View
	 */
	public final static String channelView = "FrontEnd.jsp#/";

	/**
	 * Remote Probe Config
	 */
	public final static String remoteProbeConfig = "FrontEnd.jsp#/probes";

	public final static String recordSchedule = "FrontEnd.jsp#/record";

	public final static String userManager = "FrontEnd.jsp#/roles";

}
