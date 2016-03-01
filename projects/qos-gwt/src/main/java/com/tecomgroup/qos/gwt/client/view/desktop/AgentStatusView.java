/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AgentStatusPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;

/**
 * @author ivlev.e
 * 
 */
public class AgentStatusView extends ViewWithUiHandlers<AgentStatusPresenter>
		implements
			AgentStatusPresenter.MyView {

	interface ViewUiBinder extends UiBinder<Widget, AgentStatusView> {
	}

	private final Widget widget;

	private final AgentGisWidget agentGisWidget;

	private final AppearanceFactory appearanceFactory;

	private final QoSMessages messages;

	@UiField
	protected Label agentNameLabel;

	@UiField
	protected SimplePanel gisPanel;

	@UiField(provided = true)
	protected BorderLayoutContainer borderLayoutContainer;

	@UiField
	protected FlexTable propertiesGrid;

	@UiField(provided = true)
	protected TabPanel tabContainer;

	@UiField(provided = true)
	protected FramedPanel westPanel;

	@UiField(provided = true)
	protected FramedPanel centerFramePanel;

	@UiField
	protected VBoxLayoutContainer westContainer;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	@Inject
	public AgentStatusView(final AgentGisWidget agentGisWidget,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		this.agentGisWidget = agentGisWidget;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.messages = messages;
		beforeUiBinderInitialization();
		widget = UI_BINDER.createAndBindUi(this);
		afterUiBinderInitialization();
		initHandlers();
	}

	private void afterUiBinderInitialization() {
		StyleUtils.configureNoHeaders(westPanel);
		StyleUtils.configureNoHeaders(centerFramePanel);
		gisPanel.getElement().getStyle().setWidth(178, Unit.PX);
		gisPanel.getElement().getStyle().setPadding(10, Unit.PX);
		gisPanel.getElement().getStyle().setHeight(150, Unit.PX);
		gisPanel.addStyleName(appearanceFactory.resources().css().gisPanel());
		gisPanel.setWidget(agentGisWidget.asWidget());

		tabContainer.setBorders(false);
		westContainer.addStyleName(appearanceFactory.resources().css()
				.gisContainer());

		agentNameLabel.addStyleName(appearanceFactory.resources().css()
				.text18px());
		agentNameLabel.addStyleName(appearanceFactory.resources().css()
				.lineHeigth30px());
		agentNameLabel.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		propertiesGrid.addStyleName(appearanceFactory.resources().css()
				.textMainFontAndSize());
		propertiesGrid.setText(0, 0, messages.platform() + ": ");
		propertiesGrid.setText(1, 0, messages.netAddress() + ": ");
		propertiesGrid.setText(2, 0, messages.timezone() + ": ");
		propertiesGrid.setText(3, 0, messages.description() + ": ");
		propertiesGrid.getFlexCellFormatter().setColSpan(4, 0, 2);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private void beforeUiBinderInitialization() {
		borderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		westPanel = new FramedPanel(appearanceFactory.framedPanelAppearance());
		tabContainer = new TabPanel(appearanceFactory.tabPanelAppearance());

		centerFramePanel = new FramedPanel(
				appearanceFactory.framedPanelAppearance());

		agentGisWidget.disableLayerSwitcher();
		agentGisWidget.disableOverviewMap();
		agentGisWidget.disableScaleLine();
	}

	@Override
	public void displayAgent(final MAgent agent) {
		agentGisWidget.updateAgents(Arrays.asList(agent));
		agentNameLabel.setText(agent.getDisplayName());

		final String platform = agent.getPlatform();
		final String address = agent.getNetAddress();

		propertiesGrid.setText(0, 1, platform == null
				? messages.unknown()
				: platform);
		propertiesGrid.setText(1, 1, address == null
				? messages.unknown()
				: address);
		propertiesGrid.setText(2, 1, agent.getTimeZone());
		propertiesGrid.setText(4, 0, agent.getDescription());
	}

	private void initHandlers() {
		tabContainer.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(final SelectionEvent<Widget> event) {
				final TabPanel panel = (TabPanel) event.getSource();
				final Widget w = event.getSelectedItem();
				final TabItemConfig config = panel.getConfig(w);
				getUiHandlers().actionSelectGridPresenter(config.getText());
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

	@Override
	public void updateAgent(final String agentName,
			final PerceivedSeverity severity) {
		agentGisWidget.updateAgent(agentName, severity);
	}

}
