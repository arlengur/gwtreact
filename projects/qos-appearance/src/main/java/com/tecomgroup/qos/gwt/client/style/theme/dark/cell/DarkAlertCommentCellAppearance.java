/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.tecomgroup.qos.gwt.client.bean.AlertCommentDetails;
import com.tecomgroup.qos.gwt.client.style.common.cell.AlertCommentCellAppearance;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AlertCommentCell;

/**
 * @author novohatskiy.r
 * 
 */
public class DarkAlertCommentCellAppearance
		implements
			AlertCommentCellAppearance {

	public interface DarkAlertCommentCellResources extends ClientBundle {
		@Source("DarkAlertCommentCellAppearance.css")
		DarkAlertCommentCellStyle style();
	}

	public interface DarkAlertCommentCellStyle extends CssResource {
		String author();

		String cell();

		String comment();

		String date();
	}

	public interface DarkAlertCommentCellTemplate extends XTemplates {
		@XTemplate("<div class=\"{style.cell}\">"
				+ "<span class=\"{style.date}\">{commentDetails.dateTime}</span>"
				+ "<span class=\"{style.author}\"> {commentDetails.author} ({commentDetails.updateType}):</span>"
				+ "<p class=\"{style.comment}\">{commentDetails.comment}</p>"
				+ "</div>" + "</div>")
		SafeHtml renderAlertComment(DarkAlertCommentCellStyle style,
				AlertCommentDetails commentDetails);
	}

	private final DarkAlertCommentCellStyle style;

	private final DarkAlertCommentCellTemplate template;

	public DarkAlertCommentCellAppearance() {
		this(
				GWT.<DarkAlertCommentCellResources> create(DarkAlertCommentCellResources.class));
	}

	public DarkAlertCommentCellAppearance(
			final DarkAlertCommentCellResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();

		this.template = GWT.create(DarkAlertCommentCellTemplate.class);
	}

	@Override
	public void render(final AlertCommentCell cell, final Context context,
			final AlertCommentDetails commentDetails, final SafeHtmlBuilder sb) {
		sb.append(template.renderAlertComment(style, commentDetails));
	}

}
