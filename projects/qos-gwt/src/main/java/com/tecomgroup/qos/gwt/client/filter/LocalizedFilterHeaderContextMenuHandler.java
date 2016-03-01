/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.filter;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent.HeaderContextMenuHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * The handler provides correct 'Filters' menu item localization
 * 
 * @author meleshin.o
 * 
 */
public class LocalizedFilterHeaderContextMenuHandler
		implements
			HeaderContextMenuHandler {

	private final QoSMessages messages;

	/**
	 * @param messages
	 */
	public LocalizedFilterHeaderContextMenuHandler(final QoSMessages messages) {
		super();
		this.messages = messages;
	}

	@Override
	public void onHeaderContextMenu(final HeaderContextMenuEvent event) {
		final Menu menu = event.getMenu();
		Widget lastFilterItem;
		int menuItemCount;

		if (menu != null) {
			menuItemCount = menu.getWidgetCount();
			lastFilterItem = menu.getWidget(menuItemCount - 1);
			if (lastFilterItem instanceof CheckMenuItem) {
				((CheckMenuItem) lastFilterItem).setText(messages.filters());
			}
		}
	}

}
