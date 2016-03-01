package com.tecomgroup.qos.gwt.client.model.policy;

import com.tecomgroup.qos.gwt.client.model.TreeGridRow;

/**
 * @author smyshlyaev.s
 */
public interface PolicyTreeGridRow extends TreeGridRow {
	String getActionsTemplateName();

	String getAgent();

	String getConditionsTemplateName();

	String getCriticalCease();

	String getCriticalRaise();

	String getParameterDisplayName();

	String getSourceDisplayName();

	String getWarningCease();

	String getWarningRaise();
}
