/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author abondin
 * 
 */
public enum GeneralIcons implements QoSIcons {

	/**
	 * Домашняя страница
	 */
	HOME("home60", "home30"),
	/**
	 * Информация о системе
	 */
	SYSTEM_INFORMATION("systemInfo60", "systemInfo30"),
	/**
	 * Сообщения об ошибках
	 */
	ALERTS("alerts60", "alerts30"),
	/**
	 * Таблица результатов
	 */
	TABLE("table60", "table30"),
	/**
	 * Графики
	 */
	CHART("chart60", "chart30"),
	/**
	 * Блоки контроля на карте
	 * 
	 */
	GIS_AGENT("gisAgent60", "gisAgent30"),

	GIS_AGENT_MULTIPLE("gisAgentMultiple60", "gisAgentMultiple30");

	interface Resources extends ClientBundle {
		@Source("icons/30/alerts.png")
		ImageResource alerts30();
		@Source("icons/60/alerts.png")
		ImageResource alerts60();

		@Source("icons/30/chart.png")
		ImageResource chart30();
		@Source("icons/60/chart.png")
		ImageResource chart60();

		@Source("icons/30/gisAgent.png")
		ImageResource gisAgent30();
		@Source("icons/60/gisAgent.png")
		ImageResource gisAgent60();

		@Source("icons/30/gisAgentMultiple.png")
		ImageResource gisAgentMultiple30();
		@Source("icons/60/gisAgentMultiple.png")
		ImageResource gisAgentMultiple60();

		@Source("icons/30/home.png")
		ImageResource home30();
		@Source("icons/60/home.png")
		ImageResource home60();

		@Source("icons/30/systemInfo.png")
		ImageResource systemInfo30();
		@Source("icons/60/systemInfo.png")
		ImageResource systemInfo60();

		@Source("icons/30/table.png")
		ImageResource table30();
		@Source("icons/60/table.png")
		ImageResource table60();
	}
	public Resources IMAGES = GWT.create(Resources.class);

	private ImageResource icon60;
	private ImageResource icon30;

	/**
	 * 
	 */
	private GeneralIcons(final String icon60, final String icon30) {
		this.icon60 = getByName(icon60);
		this.icon30 = getByName(icon30);
	}

	private ImageResource getByName(final String name) {
		if (name.equals("home60")) {
			return IMAGES.home60();
		} else if (name.equals("home30")) {
			return IMAGES.home30();
		} else if (name.equals("systemInfo60")) {
			return IMAGES.systemInfo60();
		} else if (name.equals("systemInfo30")) {
			return IMAGES.systemInfo30();
		} else if (name.equals("alerts60")) {
			return IMAGES.alerts60();
		} else if (name.equals("alerts30")) {
			return IMAGES.alerts30();
		} else if (name.equals("chart60")) {
			return IMAGES.chart60();
		} else if (name.equals("chart30")) {
			return IMAGES.chart30();
		} else if (name.equals("table60")) {
			return IMAGES.table60();
		} else if (name.equals("table30")) {
			return IMAGES.table60();
		} else if (name.equals("gisAgent60")) {
			return IMAGES.gisAgent60();
		} else if (name.equals("gisAgent30")) {
			return IMAGES.gisAgent30();
		} else if (name.equals("gisAgentMultiple60")) {
			return IMAGES.gisAgentMultiple60();
		} else if (name.equals("gisAgentMultiple30")) {
			return IMAGES.gisAgentMultiple60();
		}
		throw new RuntimeException("Unknown icon name " + name);
	}
	/**
	 * @return the midleIcon
	 */
	@Override
	public ImageResource getIcon30() {
		return icon30;
	}

	/**
	 * @return the bigIcon
	 */
	@Override
	public ImageResource getIcon60() {
		return icon60;
	}

}
