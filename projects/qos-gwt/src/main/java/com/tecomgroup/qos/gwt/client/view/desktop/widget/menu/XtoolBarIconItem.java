/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.menu;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.widget.core.client.Window;
import com.tecomgroup.qos.gwt.client.event.RevealPlaceEvent;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author ivlev.e
 * 
 */
public class XtoolBarIconItem extends SimplePanel implements XToolbarItem {

	public static final class Builder {

		private String palceToken;

		private String title;

		private Window popup;

		private Image icon;

		public Builder() {
		}

		public XtoolBarIconItem build() {
			final XtoolBarIconItem item = new XtoolBarIconItem();
			item.palceToken = palceToken;
			item.title = title;
			item.popup = popup;
			if (icon != null) {
				icon.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						if (item.popup == null) {
							if (item.palceToken != null) {
								AppUtils.getEventBus().fireEvent(
										new RevealPlaceEvent(palceToken));
							}
						} else {
							item.popup.show();
						}
					}
				});
				icon.getElement().getStyle().setCursor(Cursor.POINTER);
				item.add(icon);
			}
			return item;
		}

		public Builder icon(final Image itemIcon) {
			icon = itemIcon;
			return this;
		}

		public Builder placeToken(final String token) {
			palceToken = token;
			return this;
		}

		public Builder popup(final Window popupToShow) {
			popup = popupToShow;
			return this;
		}

		public Builder title(final String itemTitle) {
			title = itemTitle;
			return this;
		}
	}

	private String title;

	private String palceToken;

	private Window popup;

	private XtoolBarIconItem() {
	}

	@Override
	public String getMenuTitle() {
		return title;
	}

	@Override
	public String getPlaceToken() {
		return palceToken;
	}

	@Override
	public Window getPopupWindow() {
		return popup;
	}

}
