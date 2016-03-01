/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import com.tecomgroup.qos.domain.MUserAbstractTemplate;

/**
 * Снэпшот(снимок) состояния шаблона.
 * 
 * @author meleshin.o
 * @see TemplateKeeper
 */
public class TemplateSnapshot {
	public static enum State {
		NEW, ACTUAL, OLD
	}

	private MUserAbstractTemplate template;
	private State state;

	public TemplateSnapshot(final MUserAbstractTemplate template) {
		this.template = template;
		state = State.NEW;
	}

	public MUserAbstractTemplate getTemplate() {
		return template;
	}

	public boolean isActual() {
		return state == State.ACTUAL;
	}

	public boolean isNew() {
		return state == State.NEW;

	}

	public boolean isOld() {
		return state == State.OLD;
	}

	public void setActual(final MUserAbstractTemplate template) {
		this.template = template;
		state = State.ACTUAL;
	}

	public void setNew() {
		state = State.NEW;
	}

	public void setOld() {
		state = State.OLD;
	}

	public void setTemplate(final MUserAbstractTemplate template) {
		this.template = template;
	}
}
