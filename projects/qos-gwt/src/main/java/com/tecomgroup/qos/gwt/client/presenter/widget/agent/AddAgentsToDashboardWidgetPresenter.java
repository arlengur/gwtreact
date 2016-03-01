/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.agent;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.data.shared.ListStore;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddNamedWidgetToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.AgentProperties;
import com.tecomgroup.qos.service.AgentServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public abstract class AddAgentsToDashboardWidgetPresenter
		extends
			AddNamedWidgetToDashboardWidgetPresenter {

	public static interface MyView
			extends
				AddNamedWidgetToDashboardWidgetPresenter.MyView {

	}

	private final AgentServiceAsync agentService;

	private final ListStore<MAgent> store;

	private final AgentProperties agentProperties = GWT
			.create(AgentProperties.class);

	@Inject
	public AddAgentsToDashboardWidgetPresenter(final EventBus eventBus,
			final AddNamedWidgetToDashboardWidgetPresenter.MyView view,
			final AgentServiceAsync agentService, final QoSMessages messages) {
		super(eventBus, view, messages);
		this.agentService = agentService;

		store = new ListStore<MAgent>(agentProperties.key());
	}

	public AgentProperties getAgentProperties() {
		return agentProperties;
	}

	public ListStore<MAgent> getStore() {
		return store;
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		agentService.getAllAgents(new AutoNotifyingAsyncCallback<List<MAgent>>(
				"Unable to load agents", true) {

			@Override
			protected void success(final List<MAgent> agents) {
				store.clear();
				store.addAll(agents);
			}
		});
	}
}
