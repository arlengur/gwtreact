/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.gwt.client.event.policy.AfterRemovePolicyActionsTemplatesEvent;
import com.tecomgroup.qos.gwt.client.event.policy.AfterUpdatePolicyComponentTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.policy.AfterUpdatePolicyComponentTemplateEvent.AfterUpdatePolicyComponentTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractLocalDataGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractPolicyComponentTemplatesEditorGridWidgetPresenter<M extends MPolicyComponentTemplate, V extends AbstractPolicyComponentTemplatesEditorGridWidgetPresenter.MyView<M, ?>, P extends AbstractPolicyComponentTemplateInformationWidgetPresenter<M, ?>>
		extends
			AbstractLocalDataGridWidgetPresenter<M, V>
		implements
			AfterUpdatePolicyComponentTemplateEventHandler {

	public static interface MyView<M extends MPolicyComponentTemplate, U extends AbstractPolicyComponentTemplatesEditorGridWidgetPresenter<M, ?, ?>>
			extends
				AbstractLocalDataGridWidgetPresenter.MyView<M, U> {

	}

	protected final P policyComponentTemplateInformationWidgetPresenter;

	protected final PolicyComponentTemplateServiceAsync policyComponentTemplateService;

	protected final QoSMessages messages;

	@Inject
	public AbstractPolicyComponentTemplatesEditorGridWidgetPresenter(
			final EventBus eventBus,
			final V view,
			final P policyComponentTemplateInformationWidgetPresenter,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService) {
		super(eventBus, view);
		messages = AppUtils.getMessages();
		this.policyComponentTemplateInformationWidgetPresenter = policyComponentTemplateInformationWidgetPresenter;
		this.policyComponentTemplateService = policyComponentTemplateService;
		getEventBus().addHandler(AfterUpdatePolicyComponentTemplateEvent.TYPE,
				this);
	}

	public void actionOpenTemplateEditor(final M template) {
		beforeOpenTemplateEditor(template);
		this.addToPopupSlot(policyComponentTemplateInformationWidgetPresenter,
				true);
	}

	public void actionRemoveTemplates(final List<M> templates) {
		final Set<String> keys = new HashSet<String>();
		final Set<String> names = new HashSet<String>();
		for (final M template : templates) {
			keys.add(template.getId().toString());
			names.add(template.getName());
		}
		assert (templates.size() > 0);
		policyComponentTemplateService.removeTemplates(names, templates.get(0)
				.getClass().getName(), new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(
				messages.policyComponentTemplateDeletionFail(), true) {

			@Override
			protected void success(final Void result) {
				getView().removeItems(keys);
				AppUtils.showInfoMessage(messages
						.policyComponentTemplateRemovedSuccessfully());
				AppUtils.getEventBus().fireEvent(
						new AfterRemovePolicyActionsTemplatesEvent(names));
			}
		});
	}

	protected void beforeOpenTemplateEditor(final M template) {
		if (template != null) {
			policyComponentTemplateInformationWidgetPresenter
					.setUpdateMode(template);
		} else {
			policyComponentTemplateInformationWidgetPresenter.setCreateMode();
		}
	}

	protected abstract void loadPolicyComponentTemplates();

	@Override
	public void onAfterUpdatePolicyComponentTemplate(
			final AfterUpdatePolicyComponentTemplateEvent event) {
		reload(false);
	}

	@Override
	protected void onReveal() {
		super.onReveal();
		reload(false);
	}

	@Override
	public void reload(final boolean force) {
		loadPolicyComponentTemplates();
	}
}
