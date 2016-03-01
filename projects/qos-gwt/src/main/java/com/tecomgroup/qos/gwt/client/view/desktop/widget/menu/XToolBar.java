/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.menu;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.ButtonCell.ButtonCellAppearance;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.OverflowEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.tecomgroup.qos.gwt.client.event.RevealPlaceEvent;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * Extended toolbar with ability to show menu when content is overflow. Only
 * {@link XToolbarItem} is allowed to use with this class.
 * 
 * @author ivlev.e
 * 
 */
public class XToolBar extends ToolBar {

	private ButtonCellAppearance<String> buttonCellAppearance;

	private String moreButtonText;

	public XToolBar() {
		super();
	}

	public XToolBar(final ToolBarAppearance appearance,
			final ButtonCellAppearance<String> buttonCellAppearance,
			final String moreButtonText) {
		super(appearance);
		this.buttonCellAppearance = buttonCellAppearance;
		this.moreButtonText = moreButtonText;
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(final ResizeEvent event) {
				if (moreMenu != null && moreMenu.isVisible()) {
					moreMenu.hide();
				}
			}
		});
	}

	@Override
	protected void addWidgetToMenu(final Menu menu, final Widget widget) {
		if (widget instanceof XToolbarItem) {
			final XToolbarItem toolbarItem = (XToolbarItem) widget;
			final MenuItem menuItem = new MenuItem(toolbarItem.getMenuTitle());
			menuItem.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(final SelectionEvent<Item> event) {
					final String placeToken = toolbarItem.getPlaceToken();
					if (placeToken != null) {
                        if (placeToken.contains("FrontEnd")) {
                            com.google.gwt.user.client.Window.Location.assign(placeToken);
                        } else {
                            AppUtils.getEventBus().fireEvent(new RevealPlaceEvent(placeToken));
                        }
                        return;
					}
					final Window popup = toolbarItem.getPopupWindow();
					if (popup != null) {
						popup.show();
						return;
					}
				}
			});
			menu.add(menuItem);
		}
	}

	@Override
	protected void initMore() {
		if (more == null) {
			more = new TextButton(new TextButtonCell(buttonCellAppearance),
					moreButtonText);
			more.addStyleName("x-toolbar-more");
			more.setData("x-ignore-width", true);
			more.setData("gxt-more", "true");
		}
		moreMenu = new Menu();
		moreMenu.addBeforeShowHandler(new BeforeShowHandler() {

			@Override
			public void onBeforeShow(final BeforeShowEvent event) {
				clearMenu();

				for (int i = 0, len = getWidgetCount(); i < len; i++) {
					final Widget w = getWidget(i);
					if (isHidden(w) && w != more) {
						addWidgetToMenu(moreMenu, w);
					}
				}

				XToolBar.this.fireEvent(new OverflowEvent(moreMenu));
			}
		});
		more.setMenu(moreMenu);

		if (more.getParent() != this) {
			add(more);
		}
	}
}
