/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.model.template;

import java.util.HashMap;
import java.util.Map;

import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserAlertsTemplate;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.domain.MUserResultTemplate;

/**
 * @author meleshin.o
 * 
 */
public class DefaultTemplateFactory implements TemplateFactory {

	protected static interface TemplateCreator {
		MUserAbstractTemplate create(String name);
	}

	protected final Map<TemplateType, TemplateCreator> templateCreators = new HashMap<TemplateType, TemplateCreator>();

	public DefaultTemplateFactory() {
		templateCreators.put(BaseTemplateType.ALERT, new TemplateCreator() {

			@Override
			public MUserAbstractTemplate create(final String name) {
				return new MUserAlertsTemplate(name);
			}

		});
		templateCreators.put(BaseTemplateType.REPORT, new TemplateCreator() {

			@Override
			public MUserAbstractTemplate create(final String name) {
				return new MUserReportsTemplate(name);
			}
		});
		templateCreators.put(BaseTemplateType.RESULT, new TemplateCreator() {

			@Override
			public MUserAbstractTemplate create(final String name) {
				return new MUserResultTemplate(name);
			}

		});
	}

	@Override
	public MUserAbstractTemplate createTemplate(final TemplateType type,
			final String templateName) {
		return templateCreators.get(type).create(templateName);
	}

}
