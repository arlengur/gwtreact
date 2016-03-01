/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.Page404Presenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.CommonResources;
import com.tecomgroup.qos.gwt.client.style.CommonResources.CommonStyle;

/**
 * @author ivlev.e
 * 
 */
public class Page404View extends ViewWithUiHandlers<Page404Presenter>
		implements
			Page404Presenter.MyView {

	private final BorderLayoutContainer widget;

	@Inject
	public Page404View(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		CommonStyle css = appearanceFactoryProvider.get().resources().css();

		final Label text404 = new Label("404");
		text404.getElement().getStyle().setFontSize(15, Unit.EM);
		text404.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		text404.addStyleName(css.themeLightColor());
		text404.addStyleName(css.textAlignCenter());
		text404.addStyleName(css.defaultFont());

		final Label pageNotFoundText = new Label(messages.pageNotFound());
		pageNotFoundText.getElement().getStyle().setFontSize(1.5, Unit.EM);
		pageNotFoundText.addStyleName(css.textMainColor());
		pageNotFoundText.addStyleName(css.defaultFont());

		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.add(text404);
		verticalPanel.add(pageNotFoundText);

		final CenterLayoutContainer center = new CenterLayoutContainer();
		center.setWidget(verticalPanel);

		widget = new BorderLayoutContainer(appearanceFactoryProvider.get()
				.borderLayoutAppearance());
		widget.setCenterWidget(center);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

}
