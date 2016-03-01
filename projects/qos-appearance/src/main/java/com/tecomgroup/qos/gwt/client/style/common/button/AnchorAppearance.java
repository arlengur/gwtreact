/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.button;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorBaseAppearance.AnchorBaseResources;

/**
 * @author ivlev.e
 * 
 */
public interface AnchorAppearance {

	AnchorBaseResources getResources();

	void render(SafeHtmlBuilder sb, SafeUri href, String text, String target);
}
