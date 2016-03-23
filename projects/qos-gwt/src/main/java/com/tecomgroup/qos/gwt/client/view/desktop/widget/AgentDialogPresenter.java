/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.AgentServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class AgentDialogPresenter
		extends
			PresenterWidget<AgentDialogPresenter.AgentDialogView>
		implements
			UiHandlers,
			AgentSelectionListener {

	public static interface AgentDialogView
			extends
				PopupView,
				HasUiHandlers<AgentDialogPresenter> {

		<X> X cast();

		void loadAgents(List<MAgent> agents);

		void postInitialize();

		void select(MAgent agent);

		void setLeftContent(final Widget content);

	}

	public static Logger LOGGER = Logger.getLogger(AgentDialogPresenter.class
			.getName());

	protected QoSMessages messages;

	protected AgentServiceAsync agentService;

	protected AgentDialogPresenter(final EventBus eventBus,
			final AgentDialogView view, final QoSMessages messages,
			final AgentServiceAsync agentService) {
		super(eventBus, view);
		this.messages = messages;
		this.agentService = agentService;
		getView().setUiHandlers(this);
	}

	@Override
	public void agentSelected(final MAgent agent) {
		getView().select(agent);
	}

	@SuppressWarnings("unchecked")
	public <X> X cast() {
		return (X) this;
	}

	protected void loadAgents() {
		agentService
				.getAllAgents(new AutoNotifyingAsyncLogoutOnFailureCallback<List<MAgent>>() {

					@Override
					protected void failure(final Throwable caught) {
						LOGGER.log(Level.SEVERE, "Cannot load agents", caught);
					}

					@Override
					protected void success(final List<MAgent> result) {
						if (result != null) {
							getView().loadAgents(result);
							if (result.size() == 1) {
								getView().select(result.iterator().next());
							}
						}
					}
				});
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().postInitialize();
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		loadAgents();
	}

}
