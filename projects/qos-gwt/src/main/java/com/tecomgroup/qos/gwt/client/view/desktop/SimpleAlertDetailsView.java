/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AlertDetailsPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;

/**
 * @author novohatskiy.r
 *
 */
public class SimpleAlertDetailsView
		extends
			ViewWithUiHandlers<AlertDetailsPresenter<?, ?>>
		implements
			AlertDetailsPresenter.AlertDetailsView {

	public interface ToolbarActionHandler {
		void onAction(String comment);
	}

	interface ViewUiBinder extends UiBinder<Widget, SimpleAlertDetailsView> {
	}

	public final static int ALERT_DETAILS_PANEL_HEIGHT = 340;

	@UiField
	protected BorderLayoutData commentsPanelData;

	@UiField
	protected BorderLayoutData detailsPanelData;

	private final Widget widget;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	@UiField(provided = true)
	protected BorderLayoutContainer outerBorderLayoutContainer;

	@UiField(provided = true)
	protected BorderLayoutContainer northInnerBorderLayoutContainer;

	@UiField(provided = true)
	protected BorderLayoutContainer northInnerCenterBorderLayoutContainer;

	@UiField(provided = true)
	protected TabPanel tabPanel;

	@UiField
	protected VerticalLayoutContainer detailsContainer;

	@UiField
	protected CssFloatLayoutContainer toolbar;

	@UiField(provided = true)
	protected ContentPanel detailsPanel;

	@UiField(provided = true)
	protected ContentPanel commentsPanel;

	@UiField
	protected CssFloatLayoutContainer commentsHeader;

	@UiField
	protected SimpleContainer commentsContainer;

	@UiField
	protected Label alertNameLabel;

	protected AppearanceFactory appearanceFactory;

	protected DialogFactory dialogFactory;

	protected QoSMessages messages;

	@UiField(provided = true)
	protected BorderLayoutData outerNorthData;

	@Inject
	public SimpleAlertDetailsView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory, final QoSMessages messages) {
		super();
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.dialogFactory = dialogFactory;
		this.messages = messages;
		outerBorderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		northInnerBorderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		northInnerCenterBorderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		tabPanel = new TabPanel(appearanceFactory.tabPanelAppearance());
		tabPanel.setBorders(false);
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(final SelectionEvent<Widget> event) {
				final TabItemConfig tabConfig = tabPanel.getConfig(event
						.getSelectedItem());
				getUiHandlers().actionSelectGridPresenter(tabConfig.getText());
			}
		});
		detailsPanel = new ContentPanel();
		StyleUtils.configureNoHeaders(detailsPanel);

		commentsPanel = new ContentPanel();
		StyleUtils.configureNoHeaders(commentsPanel);

		outerNorthData = new BorderLayoutData(ALERT_DETAILS_PANEL_HEIGHT);
		outerNorthData.setMargins(new Margins(0, 0, 0, 0));
		widget = UI_BINDER.createAndBindUi(this);
		alertNameLabel.addStyleName(appearanceFactory.resources().css()
				.text18px());
		alertNameLabel.addStyleName(appearanceFactory.resources().css()
				.lineHeigth30px());
		alertNameLabel.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		toolbar.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		commentsHeader.addStyleName(appearanceFactory.resources().css()
				.commentPanelHeader());
		addButtonsToToolbar();
	}

	protected void addButtonsToToolbar() {
		final Image ackButton = createToolBarButton(appearanceFactory
				.resources().alertAck(), messages.acknowledge(),
				new ToolbarActionHandler() {
					@Override
					public void onAction(final String comment) {
						getUiHandlers().actionAcknowledgeAlert(comment);
					}
				});

		final Image unackButton = createToolBarButton(appearanceFactory
				.resources().alertUnAck(), messages.unAcknowledge(),
				new ToolbarActionHandler() {
					@Override
					public void onAction(final String comment) {
						getUiHandlers().actionUnAcknowledgeAlert(comment);
					}
				});

		final Image clearButton = createToolBarButton(appearanceFactory
				.resources().alertClear(), messages.clear(),
				new ToolbarActionHandler() {
					@Override
					public void onAction(final String comment) {
						getUiHandlers().actionClearAlert(comment);
					}
				});

		final Widget commentAlert = createToolBarButton(appearanceFactory
				.resources().alertComment(), messages.comment(), null,
				CommentMode.REQUIRED, new ToolbarActionHandler() {
					@Override
					public void onAction(final String comment) {
						getUiHandlers().commentAlert(comment);
					}
				});

		final Image sourceButton = createToolBarButton(appearanceFactory
				.resources().alertSource(), messages.source(), null);
		sourceButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionNavigateToSource();
			}
		});

		final Image tableButton = createToolBarButton(appearanceFactory
				.resources().showTableForAlert(), messages.resultTable(), null);
		tableButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionNavigateToResultDetails();
			}
		});

		final Image chartButton = createToolBarButton(appearanceFactory
				.resources().showChartForAlert(), messages.chart(), null);
		chartButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionNavigateToChart();
			}
		});

		final CssFloatData layoutData = new CssFloatData();
		toolbar.add(ackButton, layoutData);
		toolbar.add(unackButton, layoutData);
		toolbar.add(clearButton, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(commentAlert, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(sourceButton, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(tableButton, layoutData);
		toolbar.add(chartButton, layoutData);
	}
	@Override
	public Widget asWidget() {
		return widget;
	}

	protected Image createSeparator() {
		return StyleUtils.createSeparator(new Margins(6, 7, 3, 7));
	}

	protected Image createToolBarButton(final ImageResource icon,
			final String actionName, final String message,
			final CommentMode commentMode, final ToolbarActionHandler handler) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		button.setTitle(actionName);
		if (handler != null) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					dialogFactory.createConfirmationDialog(
							new ConfirmationHandler() {
								@Override
								public void onCancel() {
									// Do nothing
								}

								@Override
								public void onConfirm(final String comment) {
									handler.onAction(comment);
								}
							}, actionName, message, commentMode).show();
				}
			});
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		button.getElement().<XElement> cast()
				.setMargins(new Margins(10, 7, 3, 7));
		return button;
	}

	protected Image createToolBarButton(final ImageResource icon,
			final String actionName, final ToolbarActionHandler handler) {
		return createToolBarButton(icon, actionName, null,
				CommentMode.OPTIONAL, handler);
	}

	@Override
	public void displayAlert(final MAlert alert) {
		alertNameLabel.setText(alert.getAlertType().getDisplayName());
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot.equals(COMMENTS_GRID)) {
			commentsContainer.clear();
			commentsContainer.add(content);
		} else if (slot.equals(PROPERTIES_GRID)) {
			detailsPanel.clear();
			detailsPanel.add(content);
		} else if (tabPanel.getConfig(content.asWidget()) == null) {
			tabPanel.add(content.asWidget(), new TabItemConfig(slot.toString()));
		}
	}

}
