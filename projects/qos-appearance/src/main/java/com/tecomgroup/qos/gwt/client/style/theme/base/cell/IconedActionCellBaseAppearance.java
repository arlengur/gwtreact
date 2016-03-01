/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.base.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.XTemplates;
import com.tecomgroup.qos.gwt.client.style.common.cell.IconedActionCellAppearance;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;

/**
 * @author kunilov.p
 * 
 */
public class IconedActionCellBaseAppearance<C>
		implements
			IconedActionCellAppearance<C> {

	public interface IconedActionCellTemplate extends XTemplates {

		@XTemplate("<div title=\"{title}\" style=\"text-align:center; cursor:pointer;\">{iconHtml}</div>")
		SafeHtml icon(String title, SafeHtml iconHtml);
	}

	private final SafeHtml iconHtml;

	private final String title;

	private final IconedActionCellTemplate template;

	public IconedActionCellBaseAppearance(final ImageResource icon,
			final String title) {
		super();
		this.title = title;
		iconHtml = AbstractImagePrototype.create(icon).getSafeHtml();
		template = GWT
				.<IconedActionCellTemplate> create(IconedActionCellTemplate.class);
	}

	@Override
	public void render(final IconedActionCell<C> cell, final Context context,
			final C value, final SafeHtmlBuilder sb) {
		sb.append(template.icon(title, iconHtml));
	}
}
