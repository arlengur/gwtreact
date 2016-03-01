/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AbstractRemoteDataGridWidgetPresenterWithTemplates;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Abstract grid view with template functionality
 * 
 * @author sviyazov.a
 * 
 */
public abstract class AbstractRemoteDataGridViewWithTemplates<M, U extends AbstractRemoteDataGridWidgetPresenterWithTemplates<M, ?>>
		extends
			AbstractRemoteDataGridView<M, U> {

	protected Label loadedTemplateLabel;
	protected Label loadedTemplateLabelDescr;

	@Inject
	public AbstractRemoteDataGridViewWithTemplates(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
	}

	protected void addTemplateButtonsToToolbar() {
		final Image loadTemplate = createToolBarButton(appearanceFactory
				.resources().loadTemplateButton(),
				messages.templateLoadingHeader(), null);
		loadTemplate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().openLoadTemplateDialog();
			}
		});

		final Image saveTemplate = createToolBarButton(appearanceFactory
				.resources().saveTemplateButton(),
				messages.tempalteSavingHeader(), null);
		saveTemplate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().openSaveTemplateDialog();
			}
		});

		loadedTemplateLabel = new Label();
		loadedTemplateLabel.setSize("200px", "20px");
		loadedTemplateLabel.addStyleName(appearanceFactory.resources().css()
				.textAlignLeft());
		loadedTemplateLabel.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		loadedTemplateLabel.addStyleName(appearanceFactory.resources().css()
				.textOverflowEllipsis());

		loadedTemplateLabelDescr = new Label();
		loadedTemplateLabelDescr.setSize("70px", "20px");
		loadedTemplateLabelDescr.addStyleName(appearanceFactory.resources().css()
				.textAlignLeft());
		loadedTemplateLabelDescr.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		loadedTemplateLabelDescr.setText(messages.alertTemplateTitle());

		setToolbarStandardMargins(saveTemplate.getElement().<XElement> cast());
		setToolbarStandardMargins(loadTemplate.getElement().<XElement> cast());
		setToolbarStandardMargins(loadedTemplateLabel.getElement()
				.<XElement> cast());
		setToolbarStandardMargins(loadedTemplateLabelDescr.getElement()
				.<XElement> cast());

		final CssFloatData layoutData = new CssFloatData();
		toolbar.add(new Label(), new CssFloatData(50));
		toolbar.add(saveTemplate, layoutData);
		toolbar.add(loadTemplate, layoutData);
		toolbar.add(loadedTemplateLabel, layoutData);
		toolbar.add(loadedTemplateLabelDescr, layoutData);

		saveTemplate.getElement().<XElement> cast().getStyle()
				.setFloat(Float.RIGHT);
		loadTemplate.getElement().<XElement> cast().getStyle()
				.setFloat(Float.RIGHT);
		loadedTemplateLabel.getElement().<XElement> cast().getStyle()
				.setFloat(Float.RIGHT);
		loadedTemplateLabelDescr.getElement().<XElement> cast().getStyle()
				.setFloat(Float.RIGHT);
	}

	abstract protected boolean hasToolbalTemplateButtons();

	public void onBind() {
		if (hasToolbalTemplateButtons()) {
			addTemplateButtonsToToolbar();
		}
		toolbar.forceLayout();
	}

	public void setTemplateLabel(final String templateName) {
		loadedTemplateLabel.setText(templateName);
	}

	protected void setToolbarStandardMargins(final XElement element) {
		element.setMargins(new Margins(10, 7, 3, 7));
	}
}
