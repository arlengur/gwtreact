/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent.SaveTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.presenter.SaveTemplatePresenterWidget;

/**
 * This type of event sent by {@link SaveTemplatePresenterWidget} to its holder
 * 
 * @author meleshin.o
 * 
 */
public class SaveTemplateEvent extends GwtEvent<SaveTemplateEventHandler> {

	public static interface SaveTemplateEventHandler extends EventHandler {
		void saveTemplate(SaveTemplateEvent event);
	}

	public final static Type<SaveTemplateEventHandler> TYPE = new Type<SaveTemplateEventHandler>();

	private final MUserAbstractTemplate template;

	public SaveTemplateEvent(final MUserAbstractTemplate template) {
		super();
		this.template = template;
	}

	@Override
	protected void dispatch(final SaveTemplateEventHandler handler) {
		handler.saveTemplate(this);
	}

	@Override
	public Type<SaveTemplateEventHandler> getAssociatedType() {
		return TYPE;
	}

	public MUserAbstractTemplate getTemplate() {
		return template;
	}
}
