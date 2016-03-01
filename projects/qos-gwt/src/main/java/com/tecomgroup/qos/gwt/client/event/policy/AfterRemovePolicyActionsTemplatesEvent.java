/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.policy;

import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.gwt.client.event.policy.AfterRemovePolicyActionsTemplatesEvent.AfterRemovePolicyActionsTemplatesEventHandler;

/**
 * Fires after a collection of {@link MPolicyActionsTemplate} was removed.
 * 
 * @author sviyazov.a
 * 
 */
public class AfterRemovePolicyActionsTemplatesEvent
		extends
			GwtEvent<AfterRemovePolicyActionsTemplatesEventHandler> {

	public static interface AfterRemovePolicyActionsTemplatesEventHandler
			extends
				EventHandler {
		void onAfterRemovePolicyActionsTemplates(
				AfterRemovePolicyActionsTemplatesEvent event);
	}

	public final static Type<AfterRemovePolicyActionsTemplatesEventHandler> TYPE = new Type<AfterRemovePolicyActionsTemplatesEventHandler>();

	private final Set<String> removedTemplateNames;

	public AfterRemovePolicyActionsTemplatesEvent(
			final Set<String> removedTemplateNames) {
		super();
		this.removedTemplateNames = removedTemplateNames;
	}

	@Override
	protected void dispatch(
			final AfterRemovePolicyActionsTemplatesEventHandler handler) {
		handler.onAfterRemovePolicyActionsTemplates(this);
	}

	@Override
	public Type<AfterRemovePolicyActionsTemplatesEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Set<String> getRemovedTemplateNames() {
		return removedTemplateNames;
	}
}
