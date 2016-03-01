/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplatesEditorGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PolicyConditionsTemplatesProperties;

/**
 * @author kunilov.p
 * 
 */
public class PolicyConditionsTemplatesEditorGridWidgetView
		extends
			AbstractPolicyComponentTemplatesEditorGridWidgetView<MPolicyConditionsTemplate, PolicyConditionsTemplatesEditorGridWidgetPresenter>
		implements
			PolicyConditionsTemplatesEditorGridWidgetPresenter.MyView {

	private final PolicyConditionsTemplatesProperties policyConditionsTemplatesProperties;

	@Inject
	public PolicyConditionsTemplatesEditorGridWidgetView(
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		policyConditionsTemplatesProperties = GWT
				.create(PolicyConditionsTemplatesProperties.class);
	}

	@Override
	protected ModelKeyProvider<MPolicyConditionsTemplate> getPolicyComponentTemplateModelKeyProperty() {
		return policyConditionsTemplatesProperties.key();
	}

	@Override
	protected ValueProvider<MPolicyConditionsTemplate, String> getPolicyComponentTemplateNameProperty() {
		return policyConditionsTemplatesProperties.name();
	}
}
