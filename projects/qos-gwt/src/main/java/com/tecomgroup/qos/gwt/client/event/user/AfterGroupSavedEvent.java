/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.user;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.gwt.client.event.user.AfterGroupSavedEvent.AfterGroupSavedEventHandler;

/**
 * Fires after {@link MUserGroup} was saved on server.
 * 
 * @author ivlev.e
 * 
 */
public class AfterGroupSavedEvent extends GwtEvent<AfterGroupSavedEventHandler> {

	public static interface AfterGroupSavedEventHandler extends EventHandler {
		void onAfterGroupSaved(AfterGroupSavedEvent event);
	}

	private final MUserGroup group;

	private final String oldGroupName;

	public final static Type<AfterGroupSavedEventHandler> TYPE = new Type<AfterGroupSavedEventHandler>();

	public AfterGroupSavedEvent(final MUserGroup group,
			final String oldGroupName) {
		super();
		this.group = group;
		this.oldGroupName = oldGroupName;
	}

	@Override
	protected void dispatch(final AfterGroupSavedEventHandler handler) {
		handler.onAfterGroupSaved(this);
	}

	@Override
	public Type<AfterGroupSavedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public MUserGroup getGroup() {
		return group;
	}

	public String getOldGroupName() {
		return oldGroupName;
	}

}
