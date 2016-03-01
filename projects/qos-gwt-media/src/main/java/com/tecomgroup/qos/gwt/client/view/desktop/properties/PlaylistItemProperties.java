/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.gwt.client.model.PlaylistItem;

/**
 * @author meleshin.o
 * 
 */
public interface PlaylistItemProperties extends PropertyAccess<PlaylistItem> {
	@Path("endTime")
	ValueProvider<PlaylistItem, Long> endTime();

	@Path("name")
	ModelKeyProvider<PlaylistItem> key();

	ValueProvider<PlaylistItem, String> name();

	@Path("startTime")
	ValueProvider<PlaylistItem, Long> startTime();
}
