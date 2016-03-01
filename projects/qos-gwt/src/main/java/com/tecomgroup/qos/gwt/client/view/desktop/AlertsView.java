/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AlertsPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * @author abondin
 * 
 */
public class AlertsView extends ViewWithUiHandlers<AlertsPresenter>
		implements
			AlertsPresenter.MyView {

	public static Logger LOGGER = Logger.getLogger(AlertsView.class.getName());

	private final AppearanceFactory appearanceFactory;

	private TabPanel tabContainer;

	protected QoSMessages messages;

	private BorderLayoutContainer contentPanel;

	@Inject
	public AlertsView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		init();
	}

	@Override
	public Widget asWidget() {
		return contentPanel;
	}

	/**
	 * 
	 */
	private void init() {
		contentPanel = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		contentPanel.setBorders(false);

		tabContainer = new TabPanel(appearanceFactory.tabPanelAppearance());
		tabContainer.setBorders(false);
		final BorderLayoutData layoutData = new BorderLayoutData();
		layoutData.setMargins(new Margins(10));
		contentPanel.add(tabContainer, layoutData);

		tabContainer.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(final SelectionEvent<Widget> event) {
				final TabPanel panel = (TabPanel) event.getSource();
				final Widget w = event.getSelectedItem();
				final TabItemConfig config = panel.getConfig(w);
				getUiHandlers().actionSelectGridPreseter(config.getText());
			}
		});

	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (tabContainer.getConfig(content.asWidget()) == null) {
			tabContainer.add(content.asWidget(),
					new TabItemConfig(slot.toString()));
		}
	}

}
