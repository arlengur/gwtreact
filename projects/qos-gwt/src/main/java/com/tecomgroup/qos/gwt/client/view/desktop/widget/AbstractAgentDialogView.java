/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.SenchaPopupView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;

/**
 * @author ivlev.e
 * 
 */
public abstract class AbstractAgentDialogView
		extends
			SenchaPopupView<AgentDialogPresenter>
		implements
			AgentDialogPresenter.AgentDialogView {

	interface ViewUiBinder extends UiBinder<Dialog, AbstractAgentDialogView> {
	}

	@UiField(provided = true)
	protected Dialog dialog;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	@UiField(provided = true)
	protected BorderLayoutContainer container;

	@UiField(provided = true)
	protected ContentPanel agentSelectorPanel;

	@UiField(provided = true)
	protected ContentPanel agentGisPanel;

	protected final AgentSelectorWidget agentSelectorWidget;

	protected final AgentGisWidget agentGisWidget;

	protected ContentPanel westContentPanel;

	private final AppearanceFactory af;

	private final int WEST_CONTAINER_DEFAULT_WIDTH = 216;

	private final int WEST_CONTAINER_MARGIN_RIGHT = 5;

	protected AbstractAgentDialogView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final AgentSelectorWidget agentSelectorWidget,
			final AgentGisWidget agentGisWidget) {
		super(eventBus);
		af = appearanceFactoryProvider.get();
		this.agentSelectorWidget = agentSelectorWidget;
		this.agentGisWidget = agentGisWidget;

		dialog = new Dialog(af.dialogAppearance());
		dialog.setPredefinedButtons();

		container = new BorderLayoutContainer(af.borderLayoutAppearance());

		agentSelectorPanel = agentSelectorWidget.asWidget();

		final SimplePanel simplePanel = new SimplePanel();
		simplePanel.addStyleName(appearanceFactoryProvider.get().resources()
				.css().addChartSeriesSelectAgentPanel());
		simplePanel.add(agentGisWidget.asWidget());

		agentGisPanel = new ContentPanel();
		agentGisPanel.add(simplePanel);
		agentGisPanel.setBorders(false);
		agentGisPanel.setBodyBorder(false);
		agentGisPanel.setHeaderVisible(false);

		westContentPanel = new FramedPanel(af.framedPanelAppearance());
		westContentPanel.setBorders(false);
		westContentPanel.setBodyBorder(false);
		westContentPanel.setHeaderVisible(false);
		westContentPanel.setLayoutData(getWestContainerLayoutData());

		container.setWestWidget(westContentPanel);

		dialog = UI_BINDER.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return dialog;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X cast() {
		return (X) this;
	}

	public MAgent getCurrentAgent() {
		return agentSelectorWidget.getSelectedAgent();
	}

	public Dialog getDialog() {
		return dialog;
	}

	protected BorderLayoutContainer.BorderLayoutData getWestContainerLayoutData() {
		final BorderLayoutContainer.BorderLayoutData layoutData = new BorderLayoutContainer.BorderLayoutData();

		layoutData.setSize(WEST_CONTAINER_DEFAULT_WIDTH);
		layoutData
				.setMargins(new Margins(0, WEST_CONTAINER_MARGIN_RIGHT, 0, 0));

		return layoutData;
	}

	@Override
	public void loadAgents(final List<MAgent> agents) {
		agentSelectorWidget.loadAllAgents(agents);
		agentGisWidget.updateAgents(agents);
	}

	@Override
	public void postInitialize() {
		agentSelectorWidget.addListener(getUiHandlers());
		agentSelectorWidget.addListener(agentGisWidget);
		agentGisWidget.addListener(getUiHandlers());
		agentGisWidget.addListener(agentSelectorWidget);
	}

	@Override
	public void select(final MAgent agent) {
		agentSelectorWidget.agentSelected(agent);
	}

	@Override
	public void setLeftContent(final Widget content) {
		westContentPanel.clear();
		westContentPanel.add(content);
		westContentPanel.forceLayout();
	}
}
