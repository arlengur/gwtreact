/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent.LoadTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.presenter.LoadTemplatePresenterWidget;

/**
 * This type of event sent by {@link LoadTemplatePresenterWidget} to its holder
 * 
 * @author meleshin.o
 */
public class LoadTemplateEvent extends GwtEvent<LoadTemplateEventHandler> {

	public static interface LoadTemplateEventHandler extends EventHandler {
		void loadTemplate(LoadTemplateEvent event);
	}

	public final static Type<LoadTemplateEventHandler> TYPE = new Type<LoadTemplateEventHandler>();

	private final MUserAbstractTemplate template;

	public LoadTemplateEvent(final MUserAbstractTemplate template) {
		super();
		this.template = template;
	}

	@Override
	protected void dispatch(final LoadTemplateEventHandler handler) {
		handler.loadTemplate(this);
	}

	@Override
	public Type<LoadTemplateEventHandler> getAssociatedType() {
		return TYPE;
	}

	public MUserAbstractTemplate getTemplate() {
		return template;
	}
}
