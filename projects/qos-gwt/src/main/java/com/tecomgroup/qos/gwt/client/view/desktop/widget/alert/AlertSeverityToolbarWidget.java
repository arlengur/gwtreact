/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.renderer.AlertSeverityPropertyEditor;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * A GUI widget represents {@link AlertSeverityToolbar}.
 * 
 * @author kunilov.p
 * 
 */
public class AlertSeverityToolbarWidget implements IsWidget {

	private AlertSeverityToolbar<MAlert> severityToolbar;

	private FramedPanel toolbar;

	private final AppearanceFactory appearanceFactory;

	private final QoSMessages messages;

	/**
	 * Creates {@link AlertSeverityToolbarWidget}.
	 * 
	 * @param appearanceFactory
	 * @param messages
	 */
	@Inject
	public AlertSeverityToolbarWidget(
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super();
		this.appearanceFactory = appearanceFactory;
		this.messages = messages;
	}

	@Override
	public Widget asWidget() {
		return toolbar;
	}

	public AlertSeverityToolbar<MAlert> getSeverityToolbar() {
		return severityToolbar;
	}

	@Inject
	public void initialize() {
		final CssFloatLayoutContainer toolbarContainer = new CssFloatLayoutContainer();
		toolbarContainer.getElement().setMargins(new Margins(0, 0, 4, 0));

		final AlertSeverityPropertyEditor severityPropertyEditor = new AlertSeverityPropertyEditor(
				messages);
		severityToolbar = new AlertSeverityToolbar<MAlert>(toolbarContainer,
				appearanceFactory, severityPropertyEditor);
		severityToolbar.addSeverityFilterButtons();

		toolbar = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());
		toolbar.setHeaderVisible(false);
		toolbar.setBodyBorder(false);
		toolbar.setBorders(false);
		toolbar.add(toolbarContainer);
	}

	/**
	 * Validates selected severities. Show error message if there are no
	 * selected severities.
	 * 
	 * @return
	 */
	public boolean isValid() {
		final boolean result = SimpleUtils.isNotNullAndNotEmpty(severityToolbar
				.getCheckedValues());
		if (!result) {
			AppUtils.showErrorMessage(messages.severityNotSelected());
		}
		return result;
	}
}
