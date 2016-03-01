/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.logging.Logger;

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
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.DefaultPoliciesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.PoliciesGatekeeper;

/**
 * @author ivlev.e
 * 
 */
public class PolicyListPresenter
		extends
			Presenter<PolicyListPresenter.MyView, PolicyListPresenter.MyProxy>
		implements
			UiHandlers {

	@ProxyCodeSplit
	@UseGatekeeper(PoliciesGatekeeper.class)
	@NameToken(QoSNameTokens.policies)
	public static interface MyProxy extends ProxyPlace<PolicyListPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<PolicyListPresenter> {
	}

	public static Logger LOGGER = Logger.getLogger(PolicyListPresenter.class
			.getName());

	private final DefaultPoliciesGridWidgetPresenter policiesGridWidgetPresenter;

	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 */
	@Inject
	public PolicyListPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy,
			final DefaultPoliciesGridWidgetPresenter policiesGridWidgetPresenter) {
		super(eventBus, view, proxy);
		this.policiesGridWidgetPresenter = policiesGridWidgetPresenter;
		view.setUiHandlers(this);
	}

	// public void onCreatePolicyButtonSelected() {
	// final PlaceRequest req = new PlaceRequest(QoSNameTokens.policyItem);
	// placeManager.revealPlace(req);
	// }
	//
	// public void onEditPolicyButtonSelected(final MPolicy editedPolicy) {
	// final PlaceRequest req = new PlaceRequest(QoSNameTokens.policyItem)
	// .with(RequestParams.policyKey, editedPolicy.getKey());
	// placeManager.revealPlace(req);
	// }

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(0, policiesGridWidgetPresenter);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}

}
