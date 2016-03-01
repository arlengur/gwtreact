/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.chart;

import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.chart.AddChartToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AddNamedWidgetToDashboardView;

/**
 * @author tabolin.a
 *
 */
public class AddChartToDashboardView extends AddNamedWidgetToDashboardView
		implements
			AddChartToDashboardWidgetPresenter.MyView {
	private final static int DEFAULT_DIALOG_HEIGHT = 175;

	private final static int DEFAULT_DIALOG_WIDTH = 340;

	private CheckBox legendCheckBox;

	@Inject
	public AddChartToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages) {
		super(eventBus, appearanceFactoryPrvider, messages);
	}

	@Override
	protected void clearDialogFields() {
		super.clearDialogFields();
		legendCheckBox.setValue(false);
	}

	@Override
	protected void createWidget() {
		getUiHandlers().<AddChartToDashboardWidgetPresenter> cast()
				.actionCreateWidget(legendCheckBox.getValue());
	}

	@Override
	protected int getDialogHeight() {
		return DEFAULT_DIALOG_HEIGHT;
	}

	@Override
	protected int getDialogWidth() {
		return DEFAULT_DIALOG_WIDTH;
	}

	@Override
	public void initialize() {
		super.initialize();
		initLegengCheckBox();
	}

	private void initLegengCheckBox() {
		legendCheckBox = new CheckBox();
		final HorizontalLayoutContainer layout = new HorizontalLayoutContainer();
		layout.add(legendCheckBox, new HorizontalLayoutData(13, 13,
				new Margins(11, 0, 0, 0)));
		layout.add(new Label(messages.showLegend()), new HorizontalLayoutData(
				-1, 1, new Margins(3, 0, 0, 8)));

		mainContainer.add(layout, new VerticalLayoutData(1, -1, new Margins(10,
				5, 10, 5)));
	}
}