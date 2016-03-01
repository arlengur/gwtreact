/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.common.button;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;

/**
 * @author kunilov.p
 * 
 */
public interface ImageAnchorAppearance {

	void render(SafeHtmlBuilder sb, SafeUri href, SafeHtml content,
			String target);
}
