/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.tecomgroup.qos.domain.GISPosition;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.GisPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;

/**
 * Представление для карты с отмеченными на ней блоками контроля
 *
 * @author novohatskiy.r
 *
 */
public class GisView extends ViewWithUiHandlers<GisPresenter>
		implements
			GisPresenter.MyView {
	private final AppearanceFactory appearanceFactory;

	private final QoSMessages messages;

	private final AgentGisWidget agentGisWidget;

	private final BorderLayoutContainer contentPanel;

	@Inject
	public GisView(final AgentGisWidget agentGisWidget,
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceProvider) {
		this.appearanceFactory = appearanceProvider.get();
		this.messages = messages;
		this.agentGisWidget = agentGisWidget;

		contentPanel = new BorderLayoutContainer() {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				setPixelSize(getParent().getOffsetWidth(), getParent()
						.getOffsetHeight());
			}
		};

		contentPanel.setNorthWidget(createHeader(), new BorderLayoutData(25));
		contentPanel.setCenterWidget(agentGisWidget.asWidget());
	}

	@Override
	public Widget asWidget() {
		return contentPanel;
	}

	@Override
	public void bind() {
		agentGisWidget.addListener(getUiHandlers());
	}

	private Image createAddToDashboardButton() {
		final Image addToDashboard = createToolBarButton(appearanceFactory
				.resources().createWidgetIcon(),
				messages.addWidgetToDashboardMessage());
		addToDashboard.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().displayAddAgentsToDashboardDialog();
			}
		});
		return addToDashboard;
	}

	private Widget createHeader() {
		final CssFloatLayoutContainer container = new CssFloatLayoutContainer();
		container.add(createAddToDashboardButton());
		container.add(createLabel());
		container.getElement().getStyle().setPadding(4, Unit.PX);
		container.getElement().getStyle().setBackgroundColor("#D7D7D7");
		return container;
	}

	private Widget createLabel() {
		final Widget label = new Label(messages.navigationMap());
		final Style css = label.getElement().getStyle();
		css.setMarginLeft(5, Unit.PX);
		css.setProperty("fontFamily", "tahoma, arial, verdana, sans-serif");
		css.setProperty("fontSize", "11px");
		css.setProperty("fontWeight", "bold");
		css.setProperty("lineHeight", "15px");
		css.setProperty("color", "#3C3C3C");
		return label;
	}

	private Image createToolBarButton(final ImageResource icon,
			final String title) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (title != null) {
			button.setTitle(title);
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		return button;
	}

	@Override
	public GISPosition getCenter() {
		return agentGisWidget.getCenter();
	}

	@Override
	public int getZoom() {
		return agentGisWidget.getZoom();
	}

	@Override
	public void updateAgent(final String agentName,
			final PerceivedSeverity severity) {
		agentGisWidget.updateAgent(agentName, severity);
	}

	@Override
	public void updateAgents(final List<MAgent> agents) {
		agentGisWidget.updateAgents(agents);
	}

	@Override
	public void updateAgentStatuses(
			final Map<Source, PerceivedSeverity> agentStatuses) {
		agentGisWidget.updateAgentStatuses(agentStatuses);
	}
}
