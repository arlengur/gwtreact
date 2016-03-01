/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.template;

import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

/**
 * @author meleshin.o
 * 
 */
@SuppressWarnings("serial")
public class TemplateRow implements TreeGridRow {

	/**
	 * 
	 */
	private final MUserAbstractTemplate template;

	/**
	 * @param displayName
	 */
	public TemplateRow(final MUserAbstractTemplate template) {
		super();
		this.template = template;
	}

	@Override
	public String getKey() {
		return template.getClass().getName() + template.getId().toString();
	}

	@Override
	public String getName() {
		return template.getName();
	}

	/**
	 * @return the template
	 */
	public MUserAbstractTemplate getTemplate() {
		return template;
	}

}
