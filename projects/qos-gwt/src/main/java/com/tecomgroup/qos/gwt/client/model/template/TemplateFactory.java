/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.template;

import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;

/**
 * @author meleshin.o
 * 
 */
public interface TemplateFactory {
	MUserAbstractTemplate createTemplate(TemplateType type, String templateName);
}
