/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.sencha.gxt.core.client.dom.XElement;

public interface ButtonedGroupingViewSupport<M> {

	void deselectGroup(XElement element);

	String findGroupName(final XElement element);

	boolean isCollapseButtonPressed(final XElement element);

	boolean isRemoveButtonPressed(final XElement element);

	void selectGroup(XElement element);
}