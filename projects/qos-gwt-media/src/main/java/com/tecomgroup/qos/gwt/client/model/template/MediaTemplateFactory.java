package com.tecomgroup.qos.gwt.client.model.template;
import com.tecomgroup.qos.domain.MLiveStreamTemplate;
import com.tecomgroup.qos.domain.MRecordedStreamTemplate;
import com.tecomgroup.qos.domain.MStreamTemplate.MediaTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;

/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

/**
 * @author meleshin.o
 * 
 */
public class MediaTemplateFactory extends DefaultTemplateFactory {

	public MediaTemplateFactory() {
		super();
		templateCreators.put(MediaTemplateType.LIVE_VIDEO,
				new TemplateCreator() {

					@Override
					public MUserAbstractTemplate create(final String name) {
						return new MLiveStreamTemplate(name);
					}

				});
		templateCreators.put(MediaTemplateType.RECORDED_VIDEO,
				new TemplateCreator() {

					@Override
					public MUserAbstractTemplate create(final String name) {
						return new MRecordedStreamTemplate(name);
					}

				});
	}
}
