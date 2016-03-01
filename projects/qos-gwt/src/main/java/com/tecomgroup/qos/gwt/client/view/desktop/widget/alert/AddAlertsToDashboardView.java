/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.alert;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.alert.AddAlertsToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.AddAgentsToDashboardView;

/**
 * @author ivlev.e
 * 
 */
public class AddAlertsToDashboardView extends AddAgentsToDashboardView
		implements
			AddAlertsToDashboardWidgetPresenter.MyView {

	private final AlertSeverityToolbarWidget severityToolbarWidget;

	@Inject
	public AddAlertsToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages,
			final AlertSeverityToolbarWidget alertSeverityToolbarWidget) {
		super(eventBus, appearanceFactoryPrvider, messages);
		this.severityToolbarWidget = alertSeverityToolbarWidget;
	}

	@Override
	protected boolean areUserFieldsValid() {
		return super.areUserFieldsValid() && severityToolbarWidget.isValid();
	}

	@Override
	protected void createWidget() {
		getUiHandlers().<AddAlertsToDashboardWidgetPresenter> cast()
				.actionCreateWidget(
						severityToolbarWidget.getSeverityToolbar()
								.getCheckedValues(), getSelectedAgentKeys());
	}

	@Override
	public void initialize() {
		super.initialize();

		final VerticalLayoutData layoutData = new VerticalLayoutData(1, -1,
				new Margins(5, 5, 0, 5));
		if (mainContainer.getWidgetCount() > 2) {
			mainContainer.insert(severityToolbarWidget.asWidget(), 2,
					layoutData);
		} else {
			mainContainer.add(severityToolbarWidget.asWidget(), layoutData);
		}
	}
}
