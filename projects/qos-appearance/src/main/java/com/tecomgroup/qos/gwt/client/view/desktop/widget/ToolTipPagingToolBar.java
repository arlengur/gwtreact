/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

/**
 * @author zamkin.a
 * 
 */
public class ToolTipPagingToolBar extends PagingToolBar {

	/**
	 * @param pageSize
	 */
	public ToolTipPagingToolBar(final int pageSize) {
		super(pageSize);
		setToolTipButtons();
	}

	/**
	 * @param toolBarAppearance
	 * @param appearance
	 * @param pageSize
	 */
	public ToolTipPagingToolBar(final ToolBarAppearance toolBarAppearance,
			final PagingToolBarAppearance appearance, final int pageSize) {
		super(toolBarAppearance, appearance, pageSize);
		setToolTipButtons();
	}

	private void setToolTipButtons() {
		this.first.setTitle(getMessages().firstText());
		this.prev.setTitle(getMessages().prevText());
		this.next.setTitle(getMessages().nextText());
		this.last.setTitle(getMessages().lastText());
		this.refresh.setTitle(getMessages().refreshText());
	}

}
