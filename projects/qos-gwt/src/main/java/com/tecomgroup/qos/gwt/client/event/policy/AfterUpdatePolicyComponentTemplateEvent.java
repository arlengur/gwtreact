/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.policy;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.gwt.client.event.policy.AfterUpdatePolicyComponentTemplateEvent.AfterUpdatePolicyComponentTemplateEventHandler;

/**
 * Fires after {@link MPolicyComponentTemplate} was saved on server.
 * 
 * @author kunilov.p
 * 
 */
public class AfterUpdatePolicyComponentTemplateEvent
		extends
			GwtEvent<AfterUpdatePolicyComponentTemplateEventHandler> {

	public static interface AfterUpdatePolicyComponentTemplateEventHandler
			extends
				EventHandler {
		void onAfterUpdatePolicyComponentTemplate(
				AfterUpdatePolicyComponentTemplateEvent event);
	}

	private final MPolicyComponentTemplate template;

	private final String oldName;

	public final static Type<AfterUpdatePolicyComponentTemplateEventHandler> TYPE = new Type<AfterUpdatePolicyComponentTemplateEventHandler>();

	public AfterUpdatePolicyComponentTemplateEvent(
			final MPolicyComponentTemplate template, final String oldName) {
		super();
		this.template = template;
		this.oldName = oldName;
	}

	@Override
	protected void dispatch(
			final AfterUpdatePolicyComponentTemplateEventHandler handler) {
		handler.onAfterUpdatePolicyComponentTemplate(this);
	}

	@Override
	public Type<AfterUpdatePolicyComponentTemplateEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getOldName() {
		return oldName;
	}

	public MPolicyComponentTemplate getTemplate() {
		return template;
	}
}
