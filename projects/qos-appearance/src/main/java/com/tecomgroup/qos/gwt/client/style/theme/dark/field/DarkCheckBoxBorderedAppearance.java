/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.field;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.style.common.field.CheckBoxBorderedAppearance;

/**
 * @author abondin
 * 
 */
public class DarkCheckBoxBorderedAppearance extends CheckBoxBorderedAppearance {
	public interface DarkCheckBoxBorderedResources
			extends
				CheckBoxBorderedResources {
		@Override
		@Source({
				"DarkCheckBoxBordered.css",
				"com/sencha/gxt/theme/base/client/field/ValueBaseField.css",
				"com/sencha/gxt/theme/base/client/field/CheckBox.css",
				"com/tecomgroup/qos/gwt/client/style/theme/dark/grid/DarkAlertSeverityStyle.css"})
		DarkCheckBoxBorderedStyle css();
	}

	public interface DarkCheckBoxBorderedStyle extends CheckBoxBorderedStyle {
	}

	/**
	 * 
	 */
	public DarkCheckBoxBorderedAppearance() {
		super(
				GWT.<DarkCheckBoxBorderedResources> create(DarkCheckBoxBorderedResources.class));
	}

	/**
	 * @param resources
	 */
	public DarkCheckBoxBorderedAppearance(
			final DarkCheckBoxBorderedResources resources) {
		super(resources);
	}

}
