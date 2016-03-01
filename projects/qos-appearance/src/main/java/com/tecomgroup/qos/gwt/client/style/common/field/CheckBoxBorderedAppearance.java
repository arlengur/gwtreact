/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.field;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.field.CheckBoxDefaultAppearance;
import com.tecomgroup.qos.gwt.client.style.common.grid.AlertSeverityStyle;

/**
 * @author abondin
 * 
 */
public class CheckBoxBorderedAppearance extends CheckBoxDefaultAppearance {
	public interface CheckBoxBorderedResources extends CheckBoxResources {

	}
	public interface CheckBoxBorderedStyle
			extends
				CheckBoxStyle,
				AlertSeverityStyle {
	}
	/**
	 * 
	 */
	public CheckBoxBorderedAppearance() {
		super(
				GWT.<CheckBoxBorderedResources> create(CheckBoxBorderedResources.class));
	}
	/**
	 * @param resources
	 */
	public CheckBoxBorderedAppearance(final CheckBoxBorderedResources resources) {
		super(resources);
	}

	public CheckBoxBorderedResources getResources() {
		return (CheckBoxBorderedResources) resources;
	}

	public CheckBoxBorderedStyle getStyle() {
		return (CheckBoxBorderedStyle) style;
	}
}
