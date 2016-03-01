package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MSource;

/**
 * An interface of the service clearing/removing templates.
 * 
 * @author meleshin.o
 */
public interface TemplateDeleter {
	/**
	 * Clear templates from its child entities, which are related to
	 * {@link MSource}
	 * 
	 * @param source
	 *            is intended to be {@link MAgentTask}
	 */
	void clearSourceRelatedTemplates(MSource source);
}
