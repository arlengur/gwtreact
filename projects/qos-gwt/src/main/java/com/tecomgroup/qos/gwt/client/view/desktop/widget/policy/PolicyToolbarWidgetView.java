package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.Collection;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyTreeGridRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyToolbarWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.*;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.WarningDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PolicyActionsTemplateProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PolicyConditionsTemplatesProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractRemoteDataGridView.SelectedItemsActionHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.policy.ApplyPolicyTemplateDialog.ApplyTemplateHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.search.SearchField;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author smyshlyaev.s
 */
public class PolicyToolbarWidgetView<T extends PolicyTreeGridRow>
		extends
			ViewWithUiHandlers<PolicyToolbarWidgetPresenter<T>>
		implements
			PolicyToolbarWidgetPresenter.MyView<T> {

	private final AppearanceFactory appearanceFactory;
	private final CssFloatLayoutContainer toolbar;
	private final QoSMessages messages;
	private SearchField searchField;

	private final PolicyActionsTemplateProperties policyActionsTemplateProperties = GWT
			.create(PolicyActionsTemplateProperties.class);

	private final PolicyConditionsTemplatesProperties policyConditionsTemplateProperties = GWT
			.create(PolicyConditionsTemplatesProperties.class);
	private Image searchSeparator;

	@Inject
	public PolicyToolbarWidgetView(final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		this.appearanceFactory = appearanceFactory;
		this.messages = messages;
		this.toolbar = new CssFloatLayoutContainer();
		initializeButtons();
	}

	@Override
	public Widget asWidget() {
		return toolbar;
	}

	private Widget createApplyPolicyActionsTemplateActionWidget(
			final Margins margins) {
		final Image widget = createClickableToolBarButton(appearanceFactory
				.resources().addNotificationTemplate(),
				messages.applyNotificationTemplate(),
				new SelectedItemsActionHandler<T>() {

					@Override
					public void onAction(final List<T> items,
							final String comment) {
						openApplyPolicyActionsTemplateDialog();
					}
				});
		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	private Widget createApplyPolicyConditionsTemplateActionWidget(
			final Margins margins) {
		final Image widget = createClickableToolBarButton(appearanceFactory
				.resources().addConditionTemplate(),
				messages.applyConditionsTemplate(),
				new SelectedItemsActionHandler<T>() {

					@Override
					public void onAction(final List<T> items,
							final String comment) {
						openApplyPolicyConditionsTemplateDialog();
					}
				});
		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	private Image createClearPolicyActionsTemplateActionWidget(
			final Margins margins) {

		final Image widget = createToolBarButtonWithWarningConfirmation(
				appearanceFactory.resources().clearNotificationTemplate(),
				messages.clearPolicyActions(),
				messages.clearPolicyActionsConfirmation(), 190,
				new SelectedItemsActionHandler<T>() {
					@Override
					public void onAction(final List<T> items,
							final String comment) {
						getUiHandlers().clearPolicyActionsTemplates(items);
					}
				});
		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	private Widget createClearPolicyConditionsTemplateActionWidget(
			final Margins margins) {
		final Image widget = createToolBarButtonWithWarningConfirmation(
				appearanceFactory.resources().clearConditionTemplate(),
				messages.clearPolicyConditionsTemplates(),
				messages.clearPolicyConditionsTemplatesConfirmation(), null,
				new SelectedItemsActionHandler<T>() {
					@Override
					public void onAction(final List<T> items,
							final String comment) {
						getUiHandlers().clearPolicyConditionsTemplate(items);
					}
				});

		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	private Image createClickableToolBarButton(final ImageResource icon,
			final String tooltip, final SelectedItemsActionHandler<T> handler) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (tooltip != null) {
			button.setTitle(tooltip);
		}
		if (handler != null) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					final List<T> items = getUiHandlers()
							.getGridSelectedItems();

					if (SimpleUtils.isNotNullAndNotEmpty(items)) {
						handler.onAction(items, null);
					}
				}
			});
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		return button;
	}

	private Image createCreateActionWidget(final Margins margins) {
		final Image widget = createToolBarButton(appearanceFactory.resources()
				.newButton(), messages.create(), null);
		widget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionCreatePolicy();
			}
		});
		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	private Widget createDeleteActionWidget(final Margins margins) {
		final Image widget = createClickableToolBarButton(appearanceFactory
				.resources().deleteButton(), messages.policiesDeleteMessage(),
				new SelectedItemsActionHandler<T>() {

					@Override
					public void onAction(final List<T> items,
							final String comment) {
						getUiHandlers().actionDelete(items);
					}
				});
		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	private TextButton createPolicyActionsTemplatesEditorButton() {
		return createTemplatesEditorButton(messages.notificationsEditor(),
				new SelectHandler() {
					@Override
					public void onSelect(final SelectEvent event) {

						getUiHandlers()
								.actionOpenPolicyActionsTemplatesEditor();
					}
				});
	}

	private TextButton createPolicyConditionsTemplatesEditorButton() {
		return createTemplatesEditorButton(messages.conditionsEditor(),
				new SelectHandler() {
					@Override
					public void onSelect(final SelectEvent event) {

						getUiHandlers()
								.actionOpenPolicyConditionsTemplatesEditor();
					}
				});
	}

	private Widget createSearchBoxWidget(final Margins margins) {
		searchField = new SearchField(
				new TriggerClickEvent.TriggerClickHandler() {
					@Override
					public void onTriggerClick(final TriggerClickEvent event) {
						getUiHandlers().actionSearch(searchField.getText());
					}
				}, messages.search());
		searchField.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
		searchField.getElement().<XElement> cast().setMargins(margins);

		return searchField;
	}

	private TextButton createTemplatesEditorButton(final String text,
			final SelectHandler selectHandler) {
		final TextButton templatesEditor = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellLightAppearance()), text);
		templatesEditor.addSelectHandler(selectHandler);

		final XElement templatesEditorElement = templatesEditor.getElement()
				.<XElement> cast();
		templatesEditorElement.setMargins(new Margins(7, 0, 7, 5));
		templatesEditorElement.addClassName(appearanceFactory.resources().css()
				.textMainColor());

		return templatesEditor;
	}

	private Image createToolBarButton(final ImageResource icon,
			final String title, final Margins margins) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (title != null) {
			button.setTitle(title);
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		button.getElement().<XElement> cast().setMargins(margins);
		return button;
	}

	private Image createToolBarButtonWithWarningConfirmation(
			final ImageResource icon, final String title, final String message,
			final Integer dialogHeight,
			final SelectedItemsActionHandler<T> handler) {
		final Image button = AbstractImagePrototype.create(icon).createImage();
		if (title != null) {
			button.setTitle(title);
		}
		if (handler != null) {
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					final List<T> items = getUiHandlers()
							.getGridSelectedItems();
					if (SimpleUtils.isNotNullAndNotEmpty(items)) {
						final WarningDialog dialog = getUiHandlers()
								.getDialogFactory().createWarningDialog(title,
										message, new ConfirmationHandler() {

											@Override
											public void onCancel() {
												// do nothing
											}

											@Override
											public void onConfirm(
													final String comment) {
												handler.onAction(items, comment);
											}
										});
						if (dialogHeight != null) {
							dialog.setHeight(dialogHeight);
						}
						dialog.show();
					}
				}
			});
		}
		button.addStyleName(appearanceFactory.resources().css().cursorPointer());
		return button;
	}

	private Widget createUpdateActionWidget(final Margins margins) {
		final Image widget = createToolBarButton(appearanceFactory.resources()
				.updateButtonInvert(), messages.update(), null);
		widget.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionUpdate();
			}
		});
		widget.getElement().<XElement> cast().setMargins(margins);
		return widget;
	}

	public SearchField getSearchField() {
		return searchField;
	}

	public CssFloatLayoutContainer getToolbar() {
		return toolbar;
	}

	public void initializeButtons() {
		final Margins margins = new Margins(10, 7, 7, 10);
		new CssFloatLayoutContainer.CssFloatData();
		appearanceFactory.resources();

		if (AppUtils.isPermitted(PermissionScope.POLICIES_ADVANCED)) {
			toolbar.add(createCreateActionWidget(margins));
			toolbar.add(createDeleteActionWidget(margins));
			toolbar.add(StyleUtils.createSeparator(margins));
			toolbar.add(createApplyPolicyConditionsTemplateActionWidget(margins));
			toolbar.add(createClearPolicyConditionsTemplateActionWidget(margins));
			toolbar.add(StyleUtils.createSeparator(margins));
			toolbar.add(createApplyPolicyActionsTemplateActionWidget(margins));
			toolbar.add(createClearPolicyActionsTemplateActionWidget(margins));
			toolbar.add(StyleUtils.createSeparator(margins));
		}

		toolbar.add(createUpdateActionWidget(margins));

        // Remove right margin in order to have ths same distance from separator
        // as template button
        final Widget searchBoxWidget = createSearchBoxWidget(new Margins(8, 10,
                7, 0));
        toolbar.add(searchBoxWidget);
        setFloat(searchBoxWidget, Float.RIGHT);

        searchSeparator = StyleUtils.createSeparator(new Margins(10, 7, 7, 7));
        toolbar.add(searchSeparator);
        setFloat(searchSeparator, Float.RIGHT);

		if (AppUtils.isPermitted(PermissionScope.POLICIES_ADVANCED)) {

			final TextButton notificationsTemplatesEditor = createPolicyActionsTemplatesEditorButton();
			toolbar.add(notificationsTemplatesEditor);
			setFloat(notificationsTemplatesEditor, Float.RIGHT);

			final TextButton conditionsTemplatesEditor = createPolicyConditionsTemplatesEditorButton();
			toolbar.add(conditionsTemplatesEditor);
			setFloat(conditionsTemplatesEditor, Float.RIGHT);
		}
	}

	private void openApplyPolicyActionsTemplateDialog() {
		getUiHandlers()
				.loadPolicyActionsTemplates(
						new AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MPolicyActionsTemplate>>() {

							@Override
							protected void success(
									final Collection<MPolicyActionsTemplate> result) {

								final ListStore<MPolicyActionsTemplate> store = new ListStore<MPolicyActionsTemplate>(
										policyActionsTemplateProperties.key());
								final ApplyPolicyTemplateDialog<MPolicyActionsTemplate> dialog = new ApplyPolicyTemplateDialog<MPolicyActionsTemplate>(
										appearanceFactory, messages, store,
										policyActionsTemplateProperties
												.nameLabel());
								dialog.setComboBoxData(result);
								dialog.setApplyTemplateHandler(new ApplyTemplateHandler<MPolicyActionsTemplate>() {
									@Override
									public void applyTemplate(
											final MPolicyActionsTemplate template) {
										getUiHandlers()
												.applyPolicyActionsTemplate(
														template);
									}
								});
								dialog.show();
							}
						});
	}

	private void openApplyPolicyConditionsTemplateDialog() {
		getUiHandlers()
				.loadPolicyConditionsTemplates(
						new AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MPolicyConditionsTemplate>>() {

							@Override
							public void success(
									final Collection<MPolicyConditionsTemplate> result) {

								final ApplyPolicyTemplateDialog<MPolicyConditionsTemplate> dialog = new ApplyPolicyTemplateDialog<MPolicyConditionsTemplate>(
										appearanceFactory,
										messages,
										new ListStore<MPolicyConditionsTemplate>(
												policyConditionsTemplateProperties
														.key()),
										policyConditionsTemplateProperties
												.nameLabel());
								dialog.setComboBoxData(result);
								dialog.setApplyTemplateHandler(new ApplyTemplateHandler<MPolicyConditionsTemplate>() {
									@Override
									public void applyTemplate(
											final MPolicyConditionsTemplate template) {
										getUiHandlers()
												.applyPolicyConditionsTemplate(
														template);
									}
								});
								dialog.show();
							}
						});
	}

	public void reset() {
		searchField.reset();
        searchField.redraw();
	}

	private void setFloat(final Widget widget, final Float cssFloat) {
		widget.getElement().<XElement> cast().getStyle().setFloat(cssFloat);
	}

	@Override
	public void toggleSearchField(final boolean show) {

		if (!show) {
			if (AppUtils.isPermitted(PermissionScope.POLICIES_ADVANCED)) {
				toolbar.remove(searchSeparator);
			}
			toolbar.remove(searchField);
		} else {
			if (AppUtils.isPermitted(PermissionScope.POLICIES_ADVANCED)) {
				toolbar.add(searchSeparator);
			}
			toolbar.add(searchField);
		}

	}
}
