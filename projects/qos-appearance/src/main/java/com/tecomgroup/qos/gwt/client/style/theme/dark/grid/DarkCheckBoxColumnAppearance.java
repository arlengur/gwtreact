/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.grid;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.grid.CheckBoxColumnDefaultAppearance;

/**
 * @author ivlev.e
 * 
 */
public class DarkCheckBoxColumnAppearance<M>
		extends
			CheckBoxColumnDefaultAppearance<M> {

	public interface DarkCheckBoxColumnResources
			extends
				CheckBoxColumnResources {
		@Override
		@Source("DarkCheckBoxColumn.css")
		CheckBoxColumnStyle style();
	}

	public DarkCheckBoxColumnAppearance() {
		super(
				GWT.<DarkCheckBoxColumnResources> create(DarkCheckBoxColumnResources.class));
	}

	public DarkCheckBoxColumnAppearance(final CheckBoxColumnResources resources) {
		super(resources);
	}

	/**
	 * Checks also parent of argument header. Fix bug when header is inner DIV
	 * instead of TD
	 */
	@Override
	public boolean isHeaderChecked(final XElement header) {
		return super.isHeaderChecked(header)
				|| super.isHeaderChecked(header.getParentElement()
						.<XElement> cast());
	}

}
