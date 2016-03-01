/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.field.TriggerFieldDefaultAppearance;

/**
 * @author abondin
 * 
 */
public class DarkTriggerFieldAppearance extends TriggerFieldDefaultAppearance {
	public interface DarkTriggerFieldResources extends TriggerFieldResources {
		@Override
		@Source({"DarkTriggerField.css",
				"com/sencha/gxt/theme/base/client/field/ValueBaseField.css",
				"com/sencha/gxt/theme/base/client/field/TextField.css",
				"com/sencha/gxt/theme/base/client/field/TriggerField.css"})
		DarkTriggerFieldStyle css();
		@Override
		ImageResource triggerArrow();

		@Override
		ImageResource triggerArrowClick();

		@Override
		ImageResource triggerArrowFocus();

		@Override
		ImageResource triggerArrowFocusClick();

		@Override
		ImageResource triggerArrowFocusOver();

		@Override
		ImageResource triggerArrowOver();
	}

	public interface DarkTriggerFieldStyle extends TriggerFieldStyle {
	}

	public DarkTriggerFieldAppearance() {
		this(
				GWT.<DarkTriggerFieldResources> create(DarkTriggerFieldResources.class));
	}

	public DarkTriggerFieldAppearance(final DarkTriggerFieldResources resources) {
		super(resources);
	}

}
