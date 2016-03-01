/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupSelectedEvent;
import com.tecomgroup.qos.gwt.client.style.theme.dark.grid.DarkGroupingViewAppearance;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author ivlev.e
 * 
 */
public class ButtonedGroupingView<M> extends GroupingView<M> {

	public ButtonedGroupingView() {
		super();
	}

	public ButtonedGroupingView(final GridAppearance appearance,
			final GroupingViewAppearance groupingAppearance) {
		super(appearance, groupingAppearance);
	}

	public ButtonedGroupingView(final GroupingViewAppearance groupAppearance) {
		super(groupAppearance);
	}

	protected void doRemoveGroup(XElement head,
			final ButtonedGroupingViewSupport<M> appearance) {
		final List<GroupingData<M>> groupingDataList = getGroupData();
		head = getGroupingAppearance().findHead(head);
		String groupName = null;
		if (head != null) {
			groupName = appearance.findGroupName(head);
		}
		for (final GroupingData<M> groupingData : groupingDataList) {
			final String value = (String) groupingData.getValue();
			if (value.equals(groupName)) {
				AppUtils.getEventBus().fireEvent(
						new GridGroupRemovedEvent<M>(groupingData.getItems()));
				refresh(false);
				break;
			}
		}
	}

	protected void doToggleGroup(XElement head, final Event ge) {
		head = getGroupingAppearance().findHead(head);
		if (head != null) {
			ge.stopPropagation();
			final XElement group = getGroupingAppearance().getGroup(head);
			toggleGroup(group, getGroupingAppearance().isCollapsed(group));
		}
	}

	@Override
	protected void onMouseDown(final Event ge) {
		final XElement head = ge.getEventTarget().cast();
		if (getGroupingAppearance() instanceof DarkGroupingViewAppearance) {
			final DarkGroupingViewAppearance appearance = (DarkGroupingViewAppearance) getGroupingAppearance();
			if (appearance.isCollapseButtonPressed(head)) {
				doToggleGroup(head, ge);
			} else if (appearance.isRemoveButtonPressed(head)) {
				doRemoveGroup(head, appearance);
			} else {
				if (ge.getCtrlKey()) {
					appearance.deselectGroup(head);
				} else {
					AppUtils.getEventBus().fireEvent(
							new GridGroupSelectedEvent<M>());
					final NodeList<Element> groups = getGroups();

					for (int i = 0; i < groups.getLength(); i++) {
						appearance.deselectGroup(groups.getItem(i).getChild(0)
								.getChild(0).<XElement> cast());
					}
					appearance.selectGroup(head);
				}
			}
		} else {
			doToggleGroup(head, ge);
		}

	}

}
