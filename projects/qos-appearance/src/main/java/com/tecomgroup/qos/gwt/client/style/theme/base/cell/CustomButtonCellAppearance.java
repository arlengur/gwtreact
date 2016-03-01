/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.cell;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance;

/**
 * Changes font size to bigger and text align to left.
 * @author sviyazov.a
 * 
 */
public class CustomButtonCellAppearance<T>
		extends
			ButtonCellDefaultAppearance<T> {

	public interface DefaultButtonCellResources
			extends
				ButtonCellDefaultAppearance.ButtonCellResources {

		@Override
		@Source({"com/sencha/gxt/theme/base/client/button/ButtonCell.css",
				"DefaultButtonCell.css"})
		ButtonCellStyle style();
	}

	public CustomButtonCellAppearance() {
		super(
				GWT.<DefaultButtonCellResources> create(DefaultButtonCellResources.class));
	}

}
