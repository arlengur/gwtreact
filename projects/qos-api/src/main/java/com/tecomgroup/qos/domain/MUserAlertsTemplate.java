/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.Entity;

/**
 * 
 * Пользовательский шаблон для страницы алёртов
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MUserAlertsTemplate extends MUserTemplateWithGrid {

	public MUserAlertsTemplate() {
		super();
	}

	public MUserAlertsTemplate(final MUserAlertsTemplate template) {
		super(template);
	}

	public MUserAlertsTemplate(final String name) {
		super(name);
	}

	@Override
	public MUserAbstractTemplate copy() {
		return new MUserAlertsTemplate(this);
	}
}
