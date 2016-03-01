/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplatesEditorGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PolicyActionsTemplateProperties;

/**
 * @author kshnyakin.m
 * 
 */
public class PolicyActionsTemplatesEditorGridWidgetView
		extends
			AbstractPolicyComponentTemplatesEditorGridWidgetView<MPolicyActionsTemplate, PolicyActionsTemplatesEditorGridWidgetPresenter>
		implements
			PolicyActionsTemplatesEditorGridWidgetPresenter.MyView {

	private final PolicyActionsTemplateProperties policyActionsTemplatesProperties;

	@Inject
	public PolicyActionsTemplatesEditorGridWidgetView(
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		policyActionsTemplatesProperties = GWT
				.create(PolicyActionsTemplateProperties.class);
	}

	@Override
	protected ModelKeyProvider<MPolicyActionsTemplate> getPolicyComponentTemplateModelKeyProperty() {
		return policyActionsTemplatesProperties.key();
	}

	@Override
	protected ValueProvider<MPolicyActionsTemplate, String> getPolicyComponentTemplateNameProperty() {
		return policyActionsTemplatesProperties.name();
	}
}
