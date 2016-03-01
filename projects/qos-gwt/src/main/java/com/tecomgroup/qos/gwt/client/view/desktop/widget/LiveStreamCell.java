/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.tecomgroup.qos.gwt.client.style.common.cell.LiveStreamCellAppearance;

/**
 * @author novohatskiy.r
 * 
 */
public class LiveStreamCell<C extends Map<String, String>>
		extends
			StreamCell<C> {

	public static interface LiveStreamCellHandler extends StreamCellHandler {
	}

	public LiveStreamCell(final LiveStreamCellAppearance<C> appearance,
			final StreamCellHandler handler,
			final Set<String> excludedProperties) {
		super(appearance, handler, excludedProperties);
	}

	public LiveStreamCell(final LiveStreamCellHandler handler,
			final Set<String> excludedProperties) {
		this(
				GWT.<LiveStreamCellAppearance<C>> create(LiveStreamCellAppearance.class),
				handler, excludedProperties);
	}

}
