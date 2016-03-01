/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddNamedWidgetToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.CommonResources;
import com.tecomgroup.qos.gwt.client.style.CommonResources.CommonStyle;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.SenchaPopupView;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;

/**
 * @author ivlev.e
 * 
 */
public abstract class AddNamedWidgetToDashboardView
		extends
			SenchaPopupView<AddNamedWidgetToDashboardWidgetPresenter>
		implements
			AddNamedWidgetToDashboardWidgetPresenter.MyView {

	public enum DashboardWidgetSize {
		size1x1(1, 1), size1x2(1, 2), size2x1(2, 1), size2x2(2, 2), size3x1(3,
				1), size3x2(3, 2);

		private final int rowspan;
		private final int colspan;

		DashboardWidgetSize(final int colspan, final int rowspan) {
			this.colspan = colspan;
			this.rowspan = rowspan;
		}

		public int getColspan() {
			return colspan;
		}

		public int getRowspan() {
			return rowspan;
		}
	}

	private final static String CREATE_WIDGET_BUTTON_ID = "createWidgetButtonId";

	private final static int DEFAULT_DIALOG_WIDTH = 750;

	private final static int DEFAULT_DIALOG_HEIGHT = 350;

	private final static int ROWSPAN_1_MARGIN_TOP = 6;

	private final static int ROWSPAN_2_MARGIN_TOP = 0;

	protected final AppearanceFactory appearanceFactory;

	protected final QoSMessages messages;

	protected final VerticalLayoutContainer mainContainer;

	protected TextField title;

	HorizontalLayoutContainer spanContainer;

	protected ComboBox<String> spanComboBox;

	protected final QoSDialog dialog;

	private TextButton createWidgetButton;

	private DashboardWidgetSize currentSize;

	private DashboardWidgetSize[] widgetSizes;

	private List<Widget> widgetSizeImages;

	@Inject
	public AddNamedWidgetToDashboardView(final EventBus eventBus,
			final AppearanceFactoryProvider appearanceFactoryPrvider,
			final QoSMessages messages) {
		super(eventBus);
		this.appearanceFactory = appearanceFactoryPrvider.get();
		this.messages = messages;
		mainContainer = new VerticalLayoutContainer();
		widgetSizes = DashboardWidgetSize.values();
		dialog = createDialog();
	}

	protected boolean areUserFieldsValid() {
		return title.validate();
	}

	@Override
	public Widget asWidget() {
		return dialog;
	}

	protected void clearDialogFields() {
		title.clear();
		selectFirstSize();
	}

	@Override
	public void closeDialog() {
		dialog.hide();
	}

	private QoSDialog createDialog() {
		final QoSDialog dialog = new QoSDialog(appearanceFactory, messages) {

			@Override
			protected void createButtons() {
				super.createButtons();
				createWidgetButton = createDialogButton(
						AddNamedWidgetToDashboardView.this.messages
								.createWidget(),
						CREATE_WIDGET_BUTTON_ID);
				createWidgetButton.addSelectHandler(handler);
				createWidgetButton.setWidth(150);
				buttonBar.insert(createWidgetButton, 0);
			}

			@Override
			protected TextButton getButtonPressedOnEnter() {
				return createWidgetButton;
			}

			@Override
			protected String getTitleText(final QoSMessages messages) {
				return getDialogTitle();
			}

			@Override
			protected void initializeComponents() {
				add(mainContainer);
			}

			@Override
			protected void onButtonPressed(final TextButton button) {
				if (button == getCloseButton()) {
					hide();
				} else if (button == createWidgetButton) {
					if (areUserFieldsValid()) {
						createWidget();
					}
				}
			}
		};
		dialog.addHideHandler(new HideHandler() {

			@Override
			public void onHide(final HideEvent event) {
				clearDialogFields();
			}
		});
		dialog.setWidth(getDialogWidth());
		dialog.setHeight(getDialogHeight());
		return dialog;
	}

	protected FieldLabel createFieldLabel(final Widget w, final String label,
			final int labelWidth) {
		final FieldLabel fieldLabel = new FieldLabel(w, label);
		fieldLabel.setLabelWidth(labelWidth);
		return fieldLabel;
	}

	/**
	 * Creates widget to save.
	 */
	protected abstract void createWidget();

	private Widget createWidgetSizeImage(final DashboardWidgetSize size) {
		final Image widget = new Image(getSizeIcon(size));
		widget.addStyleName(appearanceFactory.resources().css()
				.dashboardWidgetSizeImage());
		widget.addStyleName(appearanceFactory.resources().css().cursorPointer());
		widget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final CommonStyle css = appearanceFactory.resources().css();
				AddNamedWidgetToDashboardView.this.currentSize = size;
				for (final Widget container : widgetSizeImages) {
					container.removeStyleName(css.linkSelected());
				}
				((Image) event.getSource()).addStyleName(css.linkSelected());
			}
		});
		return widget;
	}

	private void fillSpanContainer() {
		widgetSizeImages = new ArrayList<Widget>();
		for (final DashboardWidgetSize size : widgetSizes) {
			final Widget image = createWidgetSizeImage(size);
			widgetSizeImages.add(image);
			final int marginTop = size.getRowspan() == 1
					? ROWSPAN_1_MARGIN_TOP
					: ROWSPAN_2_MARGIN_TOP;
			spanContainer.add(image, new HorizontalLayoutData(-1, -1,
					new Margins(marginTop, 20, 0, 0)));

		}
	}

	@Override
	public int getColspan() {
		return currentSize.getColspan();
	}

	protected int getDialogHeight() {
		return DEFAULT_DIALOG_HEIGHT;
	}

	protected String getDialogTitle() {
		return messages.newWidget();
	}

	protected int getDialogWidth() {
		return DEFAULT_DIALOG_WIDTH;
	}

	@Override
	public int getRowspan() {
		return currentSize.getRowspan();
	}

	private ImageResource getSizeIcon(final DashboardWidgetSize size) {
		final CommonResources res = appearanceFactory.resources();

		ImageResource icon;
		switch (size) {
			case size1x1 :
				icon = res.widget1x1();
				break;
			case size1x2 :
				icon = res.widget1x2();
				break;
			case size2x1 :
				icon = res.widget2x1();
				break;
			case size2x2 :
				icon = res.widget2x2();
				break;
			case size3x1 :
				icon = res.widget3x1();
				break;
			case size3x2 :
				icon = res.widget3x2();
				break;
			default :
				throw new IllegalArgumentException("Can't get icon for :"
						+ size);
		}
		return icon;
	}

	@Override
	public String getTitle() {
		return title.getValue();
	}

	@Override
	public void initialize() {
		initializeTextField();
		initializeSpanContainer();
	}

	private void initializeSpanContainer() {
		spanContainer = new HorizontalLayoutContainer();
		spanContainer.setSize("702px", "30px");
		fillSpanContainer();
		mainContainer.add(createFieldLabel(spanContainer, messages.size(), 50),
				new VerticalLayoutData(1, -1, new Margins(10, 5, 20, 5)));
		selectFirstSize();
	}

	private void initializeTextField() {
		title = new TextField();
		title.addValidator(new TrimEmptyValidator(messages));
		mainContainer.add(createFieldLabel(title, messages.name(), 50),
				new VerticalLayoutData(1, -1, new Margins(10, 5, 10, 5)));
	}

	private void selectFirstSize() {
		if (!widgetSizeImages.isEmpty()) {
			widgetSizeImages.get(0).fireEvent(new ClickEvent() {
			});
		}

	}

	public void setDashboardWidgetSizes(final DashboardWidgetSize... sizes) {
		widgetSizes = sizes;
	}

	@Override
	public void setName(final String name) {
		title.setValue(name);
	}
}