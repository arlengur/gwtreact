/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

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
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.tecomgroup.qos.gwt.client.presenter.UserManagerPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * @author meleshin.o
 * 
 */
public class UserManagerView extends ViewWithUiHandlers<UserManagerPresenter>
		implements
			UserManagerPresenter.MyView {

	private final BorderLayoutContainer container;

	private final TabPanel tabContainer;

	@Inject
	public UserManagerView() {
		final AppearanceFactory appearanceFactory = AppearanceFactoryProvider
				.instance();
		container = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		container.setBorders(false);
		tabContainer = new TabPanel(appearanceFactory.tabPanelAppearance());
		final MarginData layoutData = new MarginData();
		layoutData.setMargins(new Margins(10));
		container.setCenterWidget(tabContainer, layoutData);
		initHandlers();
	}

	@Override
	public Widget asWidget() {
		return container;
	}

	private void initHandlers() {
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
