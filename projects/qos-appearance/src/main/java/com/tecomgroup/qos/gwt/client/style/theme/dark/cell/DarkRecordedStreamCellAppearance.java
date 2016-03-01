/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.cell;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.tecomgroup.qos.gwt.client.style.common.cell.RecordedStreamCellAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedStreamCell;

/**
 * @author novohatskiy.r
 * 
 */
public class DarkRecordedStreamCellAppearance<C extends Map<String, String>>
		extends
			DarkStreamCellAppearance<C>
		implements
			RecordedStreamCellAppearance<C> {

	private void appendGroup(final SafeHtmlBuilder sb, final Set<String> group,
			final C value) {
		for (final String element : group) {
			final String currentValue = value.get(element);
			if (currentValue != null) {
				if (element.equals(RecordedStreamCell.VIDEOS_TIMEZONE)) {
					appendTitlelessProperty(sb, currentValue);
				} else {
					appendProperty(sb, element, currentValue);
				}
			}
		}
	}

	private void appendProperty(final SafeHtmlBuilder sb, final String key,
			final String value) {
		sb.append(template.rowItem(style));
		sb.append(SafeHtmlUtils.fromTrustedString(key + ": "));
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("<span>");
		sb.append(SafeHtmlUtils.fromTrustedString(value));
		sb.appendHtmlConstant("</span><br/>");
	}

	private void appendTitlelessProperty(final SafeHtmlBuilder sb,
			final String value) {
		sb.append(template.rowItem(style));
		sb.append(SafeHtmlUtils.fromTrustedString(""));
		sb.appendHtmlConstant("</span>");
		sb.appendHtmlConstant("<span>");
		sb.append(SafeHtmlUtils.fromTrustedString(value));
		sb.appendHtmlConstant("</span><br/>");
	}

	@Override
	public void render(final RecordedStreamCell<C> cell,
			final Set<Set<String>> keysByGroup, final Context context,
			final C value, final SafeHtmlBuilder sb) {
		sb.append(template.cell(style));

		final SafeHtmlBuilder sbContent = new SafeHtmlBuilder();

		final Iterator<Set<String>> iter = keysByGroup.iterator();
		while (iter.hasNext()) {
			final Set<String> group = iter.next();
			appendGroup(sbContent, group, value);
			if (iter.hasNext()) {
				sbContent.appendHtmlConstant("<br/>");
			}
		}

		sb.append(template.textWithStyles(style, sbContent.toSafeHtml()));
		sb.appendHtmlConstant("</pre>");
		sb.append(template.removeIcon(
				style,
				AbstractImagePrototype.create(
						DarkResources.INSTANCE.gridCellRemoveMiniButton())
						.getSafeHtml()));
		sb.appendHtmlConstant("</div>");
	}
}
