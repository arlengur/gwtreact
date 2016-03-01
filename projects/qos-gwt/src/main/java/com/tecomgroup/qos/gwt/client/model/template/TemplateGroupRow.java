/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.template;

import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
/**
 * @author meleshin.o
 * 
 */
@SuppressWarnings("serial")
public class TemplateGroupRow implements TreeGridRow {

	private final String displayName;
	private final TemplateType type;

	/**
	 * @param displayName
	 */
	public TemplateGroupRow(final String displayName, final TemplateType type) {
		super();
		this.displayName = displayName;
		this.type = type;
	}

	@Override
	public String getKey() {
		return type.getTemplateClassName();
	}

	@Override
	public String getName() {
		return displayName;
	}

	public TemplateType getType() {
		return type;
	}

}
