/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style.theme.dark.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.toolbar.PagingToolBarBaseAppearance;

/**
 * @author abondin
 * 
 */
public class DarkPagingToolBarAppearance extends PagingToolBarBaseAppearance {
	public interface DarkPagingToolBarResources
			extends
				PagingToolBarResources,
				ClientBundle {
		@Override
		ImageResource first();

		@Override
		ImageResource last();

		@Override
		ImageResource loading();

		@Override
		ImageResource next();

		@Override
		ImageResource prev();

		@Override
		ImageResource refresh();
	}

	public DarkPagingToolBarAppearance() {
		this(
				GWT.<DarkPagingToolBarResources> create(DarkPagingToolBarResources.class));
	}

	public DarkPagingToolBarAppearance(
			final DarkPagingToolBarResources resources) {
		super(resources);
	}
}
