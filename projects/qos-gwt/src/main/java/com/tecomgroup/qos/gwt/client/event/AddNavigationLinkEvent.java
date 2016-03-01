/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.QoSIcons;
import com.tecomgroup.qos.gwt.client.event.AddNavigationLinkEvent.AddNavigationLinkHandler;

/**
 * 
 * Событие на создание нового элемнта навигационной панели
 * 
 * @author abondin
 * 
 */
public class AddNavigationLinkEvent extends GwtEvent<AddNavigationLinkHandler> {
	public static interface AddNavigationLinkHandler extends EventHandler {
		void onEvent(AddNavigationLinkEvent event);
	}
	public final static Type<AddNavigationLinkHandler> TYPE = new Type<AddNavigationLinkHandler>();

	private String path;

	private String displayName;

	private QoSIcons icon;

	private Integer menuIndex;
	
	private Integer minScreenWidth;

	/**
	 * @return the minScreenWidth
	 */
	public Integer getMinScreenWidth() {
		return minScreenWidth;
	}

	/**
	 * @param minScreenWidth the minScreenWidth to set
	 */
	public void setMinScreenWidth(Integer minScreenWidth) {
		this.minScreenWidth = minScreenWidth;
	}

	@Override
	protected void dispatch(final AddNavigationLinkHandler handler) {
		handler.onEvent(this);
	}

	@Override
	public Type<AddNavigationLinkHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @return the icon
	 */
	public QoSIcons getIcon() {
		return icon;
	}

	/**
	 * @return the menuIndex
	 */
	public Integer getMenuIndex() {
		return menuIndex;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param displayName
	 *            the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(final QoSIcons icon) {
		this.icon = icon;
	}

	/**
	 * @param menuIndex
	 *            the menuIndex to set
	 */
	public void setMenuIndex(final Integer menuIndex) {
		this.menuIndex = menuIndex;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}
}
