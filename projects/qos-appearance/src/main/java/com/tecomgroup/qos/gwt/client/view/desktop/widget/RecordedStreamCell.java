/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.tecomgroup.qos.gwt.client.style.common.cell.RecordedStreamCellAppearance;

/**
 * @author novohatskiy.r
 * 
 */
public class RecordedStreamCell<C extends Map<String, String>>
		extends
			StreamCell<C> {

	public static interface RecordedStreamCellHandler extends StreamCellHandler {
	}

	public static String VIDEOS_TIMEZONE = "videosTimezone";

	private Set<Set<String>> keysByGroups;

	public RecordedStreamCell(final RecordedStreamCellAppearance<C> appearance,
			final StreamCellHandler handler, final Set<Set<String>> keysByGroups) {
		super(appearance, handler, new HashSet<String>());
		this.keysByGroups = keysByGroups;
	}

	public RecordedStreamCell(final RecordedStreamCellHandler handler,
			final Set<Set<String>> keysByGroups) {
		this(
				GWT.<RecordedStreamCellAppearance<C>> create(RecordedStreamCellAppearance.class),
				handler, keysByGroups);
	}

	@Override
	public void render(final com.google.gwt.cell.client.Cell.Context context,
			final C value, final SafeHtmlBuilder sb) {
		((RecordedStreamCellAppearance<C>) appearance).render(this,
				keysByGroups, context, value, sb);
	}

}
