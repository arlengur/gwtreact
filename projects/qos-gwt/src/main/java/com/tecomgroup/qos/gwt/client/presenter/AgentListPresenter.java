/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentTasksGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.agent.AgentsGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.ProbesGatekeeper;

/**
 * Список Блоков Контроля
 * 
 * @author abondin
 * 
 */
public class AgentListPresenter
		extends
			Presenter<AgentListPresenter.MyView, AgentListPresenter.MyProxy>
		implements
			UiHandlers {

	@ProxyCodeSplit
	@UseGatekeeper(ProbesGatekeeper.class)
	@NameToken(QoSNameTokens.probesAndTasks)
	public static interface MyProxy extends ProxyPlace<AgentListPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<AgentListPresenter> {
	}

	private final AgentsGridWidgetPresenter agentGridWidgetPresenter;

	private final AgentTasksGridWidgetPresenter agentTaskGridWidgetPresenter;

	/**
	 * @param eventBus
	 * @param view
	 * @param agentService
	 */
	@Inject
	public AgentListPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy,
			final AgentsGridWidgetPresenter agentGridWidgetPresenter,
			final AgentTasksGridWidgetPresenter agentTaskGridWidgetPresenter) {
		super(eventBus, view, proxy);
		this.agentGridWidgetPresenter = agentGridWidgetPresenter;
		this.agentTaskGridWidgetPresenter = agentTaskGridWidgetPresenter;
		init();
	}

	private void init() {
		getView().setUiHandlers(this);
		agentGridWidgetPresenter
				.setAgentTaskGridWidgetPresenter(agentTaskGridWidgetPresenter);
	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(agentGridWidgetPresenter.getClass().getName(),
				agentGridWidgetPresenter);
		setInSlot(agentTaskGridWidgetPresenter.getClass().getName(),
				agentTaskGridWidgetPresenter);

		agentGridWidgetPresenter.reload(true);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}
}
