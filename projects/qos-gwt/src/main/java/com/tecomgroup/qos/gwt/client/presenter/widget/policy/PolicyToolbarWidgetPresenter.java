/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.policy;

import java.util.*;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.exception.CompatibilityException;
import com.tecomgroup.qos.exception.ParameterTypeCompatibilityException;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyTreeGridRow;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyWrapper;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ParameterTypeLabelProvider;
import com.tecomgroup.qos.service.PolicyComponentTemplateServiceAsync;
import com.tecomgroup.qos.service.PolicyConfigurationServiceAsync;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.SimpleUtils.SimpleHandler;

/**
 * Toolbar with following policy action buttons:<br/>
 * <ul>
 * <li>create new</li>
 * <li>delete selected</li>
 * <li>apply/clear policy actions template</li>
 * <li>apply/clear policy condition template</li>
 * <li>refresh</li>
 * <li>open actions template editor dialog</li>
 * <li>open condition template editor dialog</li>
 * </ul>
 * <p/>
 * Provides action hooks for few events:
 * {@link #setOpenActionsTemplatesEditorHandler(SimpleHandler)},
 * {@link #setOpenConditionsTemplatesEditorHandler(SimpleHandler)},
 * {@link #setReloadHandler(SimpleHandler)},
 * {@link #setRemoveItemsHandler(RemoveItemsHandler)}.
 * <p/>
 * <p/>
 * </br> </br> <b>Important</b>: policy grid is required to be manually set via
 * {@link #setGrid(Grid)}.
 * 
 * @author sviyazov.a
 */
public class PolicyToolbarWidgetPresenter<T extends PolicyTreeGridRow>
		extends
			PresenterWidget<PolicyToolbarWidgetPresenter.MyView<T>>
		implements
			UiHandlers {

	public interface MyView<T extends PolicyTreeGridRow>
			extends
				View,
				HasUiHandlers<PolicyToolbarWidgetPresenter<T>> {

		void reset();

		void toggleSearchField(boolean value);
	}

	public static interface RemoveItemsHandler {
		void onRemove(Set<String> keys);
	}

	public static interface SearchHandler {
		void onSearch(String searchText);
	}

	private Grid<T> grid;

	private final QoSMessages messages;

	private final DialogFactory dialogFactory;

	private final PolicyComponentTemplateServiceAsync policyComponentTemplateService;

	protected final PolicyConfigurationServiceAsync policyConfigService;

	private SimpleHandler openActionsTemplatesEditorHandler;

	private SimpleHandler reloadHandler;

	private RemoveItemsHandler removeItemsHandler;

	private SimpleHandler openConditionsTemplatesEditorHandler;

	private SimpleHandler openPolicyEditorHandler;

	private SearchHandler searchHandler;

	@Inject
	public PolicyToolbarWidgetPresenter(
			final EventBus eventBus,
			final MyView view,
			final QoSMessages messages,
			final DialogFactory dialogFactory,
			final PolicyComponentTemplateServiceAsync policyComponentTemplateService,
			final PolicyConfigurationServiceAsync policyConfigService) {
		super(eventBus, view);

		this.messages = messages;
		this.dialogFactory = dialogFactory;
		this.policyComponentTemplateService = policyComponentTemplateService;
		this.policyConfigService = policyConfigService;
		getView().setUiHandlers(this);
	}

	public void actionCreatePolicy() {
		if (openPolicyEditorHandler != null) {
			openPolicyEditorHandler.handle();
		}
	}

	public void actionDelete(final List<T> items) {
		if (SimpleUtils.isNotNullAndNotEmpty(items)) {
			dialogFactory.createConfirmationDialog(
					new ConfirmationHandler() {
						@Override
						public void onCancel() {
							// Do nothing
						}

						@Override
						public void onConfirm(final String comment) {
							final Set<String> keySet = new HashSet<String>();
							for (final T policy : items) {
								if (policy instanceof PolicyWrapper) {
									final PolicyWrapper policyWrapper = (PolicyWrapper) policy;
									keySet.add(policyWrapper.getPolicy()
											.getKey());
								}
							}

							policyConfigService.deletePolicies(keySet,
									new AutoNotifyingAsyncLogoutOnFailureCallback<Void>(
											messages.policiesRemovalFail(),
											true) {
										@Override
										protected void success(final Void result) {
											if (removeItemsHandler != null) {
												removeItemsHandler
														.onRemove(keySet);
											}
										}
									});
						}
					}, messages.delete(), messages.policiesDeleteMessage(),
					CommentMode.DISABLED).show();
		}

	}

	public void actionOpenPolicyActionsTemplatesEditor() {
		if (openActionsTemplatesEditorHandler != null) {
			openActionsTemplatesEditorHandler.handle();
		}
	}

	public void actionOpenPolicyConditionsTemplatesEditor() {
		if (openConditionsTemplatesEditorHandler != null) {
			openConditionsTemplatesEditorHandler.handle();
		}
	}

	public void actionSearch(final String searchText) {
		if (searchHandler != null) {
			searchHandler.onSearch(searchText);
		}
	}

	public void actionUpdate() {
		reload();
	}

	public void applyPolicyActionsTemplate(final MPolicyActionsTemplate template) {
		assert (grid != null);
		final Set<String> policyKeys = getPolicyKeys(grid.getSelectionModel()
				.getSelectedItems());
			policyConfigService.applyPolicyActionsTemplate(template.getName(),
					policyKeys, new AutoNotifyingAsyncLogoutOnFailureCallback<Void>() {

						@Override
						protected void success(final Void result) {
							AppUtils.showInfoMessage(messages
									.policyActionsTemplateAppliedSuccessfully());
							reload();
						}
					});
	}

	public void applyPolicyConditionsTemplate(
			final MPolicyConditionsTemplate template) {
		assert (grid != null);
		final Set<String> policyKeys = getPolicyKeys(grid.getSelectionModel()
				.getSelectedItems());
		policyConfigService.applyPolicyConditionsTemplate(template.getName(),
				policyKeys, new AutoNotifyingAsyncLogoutOnFailureCallback<Void>() {

					@Override
					protected void failure(final Throwable caught) {
						String errorMessage;
						if (caught instanceof CompatibilityException) {
							errorMessage = messages
									.parameterTypesAndUnitsConditionsTemplateConstraint();
						} else if (caught instanceof ParameterTypeCompatibilityException) {
							final ParameterTypeCompatibilityException parameterTypeCompatibilityException = (ParameterTypeCompatibilityException) caught;
							errorMessage = messages
									.parameterTypesAndConditionsTemplateContstraint(
											ParameterTypeLabelProvider
													.getLabel(
															messages,
															parameterTypeCompatibilityException
																	.getPolicyParameterType()),
											ParameterTypeLabelProvider
													.getLabel(
															messages,
															parameterTypeCompatibilityException
																	.getTemplateParameterType()));
						} else {
							errorMessage = caught.getMessage();
						}
						AppUtils.showErrorMessage(errorMessage, caught);
					}

					@Override
					protected void success(final Void result) {
						AppUtils.showInfoMessage(messages
								.policyConditionsTemplateAppliedSuccessfully());
						reload();
					}
				});
	}

	public void clearPolicyActions(final Set<String> policyKeys) {
		policyConfigService.clearPolicyActionsTemplates(policyKeys,
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>() {

					@Override
					protected void success(final Void result) {
						AppUtils.showInfoMessage(messages
								.policyActionsClearedSuccessfully());
						reload();
					}
				});
	}

	public void clearPolicyActionsTemplates(final List<T> items) {
		clearPolicyActions(getPolicyKeys(items));
	}

	public void clearPolicyConditions(final Set<String> policyKeys) {
		policyConfigService.clearPolicyConditionsTemplates(policyKeys,
				new AutoNotifyingAsyncLogoutOnFailureCallback<Void>() {

					@Override
					protected void success(final Void result) {
						AppUtils.showInfoMessage(messages
								.policyConditionsTemplatesClearedSuccessfully());
						reload();
					}
				});
	}

	public void clearPolicyConditionsTemplate(final List<T> items) {
		clearPolicyConditions(getPolicyKeys(items));
	}

	public DialogFactory getDialogFactory() {
		return dialogFactory;
	}

	public Grid<T> getGrid() {
		return grid;
	}

	public List<T> getGridSelectedItems() {
		assert (grid != null);
		return grid.getSelectionModel().getSelectedItems();
	}

	public Set<String> getPolicyKeys(final List<T> wrappers) {
		final Set<String> policyKeys = new HashSet<String>();
		for (final T wrapper : wrappers) {
			policyKeys.add(wrapper.getKey());
		}
		return policyKeys;
	}

	public void loadPolicyActionsTemplates(
			final AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MPolicyActionsTemplate>> callback) {
		policyComponentTemplateService.getAllActionsTemplates(callback);
	}

	public void loadPolicyConditionsTemplates(
			final AutoNotifyingAsyncLogoutOnFailureCallback<Collection<MPolicyConditionsTemplate>> callback) {
		policyComponentTemplateService.getAllConditionsTemplates(callback);
	}

	private void reload() {
		if (reloadHandler != null) {
			reloadHandler.handle();
		}
	}

	public void reset() {
		getView().reset();
	}

	public void setGrid(final Grid<T> grid) {
		this.grid = grid;
	}

	public void setOpenActionsTemplatesEditorHandler(final SimpleHandler handler) {
		this.openActionsTemplatesEditorHandler = handler;
	}

	public void setOpenConditionsTemplatesEditorHandler(
			final SimpleHandler simpleHandler) {
		this.openConditionsTemplatesEditorHandler = simpleHandler;
	}

	public void setOpenPolicyEditorHandler(final SimpleHandler handler) {
		this.openPolicyEditorHandler = handler;
	}

	public void setReloadHandler(final SimpleHandler handler) {
		this.reloadHandler = handler;
	}

	public void setRemoveItemsHandler(final RemoveItemsHandler handler) {
		this.removeItemsHandler = handler;
	}

	public void setSearchHandler(final SearchHandler handler) {
		this.searchHandler = handler;
	}

	public void toggleSearchField(final boolean show) {
		getView().toggleSearchField(show);
	}
}
