/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.AbstractPolicyComponentTemplatesEditorGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataGridView;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 *
 */
public abstract class AbstractPolicyComponentTemplatesEditorGridWidgetView<M extends MPolicyComponentTemplate, U extends AbstractPolicyComponentTemplatesEditorGridWidgetPresenter<M, ?, ?>>
		extends
			AbstractLocalDataGridView<M, U> {

	private final QoSDialog templateDeletionDialog;

	@Inject
	public AbstractPolicyComponentTemplatesEditorGridWidgetView(
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
		templateDeletionDialog = createTemplateDeletionDialog();
	}

	@Override
	protected boolean addButtonsToToolbar() {
		final Margins margins = new Margins(10, 3, 3, 8);
		final CssFloatData layoutData = new CssFloatData();

		final Image createButton = createToolBarButton(appearanceFactory
				.resources().newButton(),
				messages.createNewPolicyActionsTemplate(), null);
		createButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionOpenTemplateEditor(null);
			}
		});

		final Image deleteButton = createToolBarButton(appearanceFactory
				.resources().deleteButton(), messages.actionRemove(), null);
		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
                final List<M> items = grid.getSelectionModel()
                        .getSelectedItems();

                if (SimpleUtils.isNotNullAndNotEmpty(items)) {
                    templateDeletionDialog.show();
                }
			}
		});

		createButton.getElement().<XElement> cast().setMargins(margins);
		deleteButton.getElement().<XElement> cast().setMargins(margins);

		toolbar.add(createButton, layoutData);
		toolbar.add(createSeparator(), layoutData);
		toolbar.add(deleteButton, layoutData);

		return true;
	}

	@Override
	protected List<Filter<M, ?>> createFilters() {
		final List<Filter<M, ?>> filters = new ArrayList<Filter<M, ?>>();
		filters.add(filterFactory
				.<M> createStringFilter(getPolicyComponentTemplateNameProperty()));
		return filters;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridStandardAppearance();
	}

	@Override
	protected ListStore<M> createStore() {
		final StoreSortInfo<M> sortInfo = new StoreSortInfo<M>(
				getPolicyComponentTemplateNameProperty(), SortDir.ASC);
		final ListStore<M> store = new ListStore<M>(
				getPolicyComponentTemplateModelKeyProperty());
		store.addSortInfo(sortInfo);
		return store;
	}

	private QoSDialog createTemplateDeletionDialog() {
		return dialogFactory.createWarningDialog(
				messages.policyComponentTemplateDeletionConfirmation(),
				messages.policyComponentTemplateDeletionConfirmationMessage(),
				new ConfirmationHandler() {

					@Override
					public void onCancel() {
					}

					@Override
					public void onConfirm(final String comment) {
						final List<M> templates = grid.getSelectionModel()
								.getSelectedItems();
						getUiHandlers().actionRemoveTemplates(templates);
					}
				});
	}

	@Override
	protected List<ColumnConfig<M, ?>> getGridColumns() {
		final List<ColumnConfig<M, ?>> templateColumns = new ArrayList<ColumnConfig<M, ?>>();
		final ColumnConfig<M, String> templateName = new ColumnConfig<M, String>(
				getPolicyComponentTemplateNameProperty(), 200,
				messages.templateName());
		templateColumns.add(templateName);

		return templateColumns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE};
	}

	protected abstract ModelKeyProvider<M> getPolicyComponentTemplateModelKeyProperty();

	protected abstract ValueProvider<M, String> getPolicyComponentTemplateNameProperty();

	@Override
	protected void initializeGrid() {
		super.initializeGrid();
		grid.getView().setStripeRows(true);

		final String policyComponentTemplateNamePropertyPath = getPolicyComponentTemplateNameProperty()
				.getPath();
		grid.addCellClickHandler(new CellClickHandler() {

			@Override
			public void onCellClick(final CellClickEvent event) {
				final int colIndex = event.getCellIndex();
				final ColumnConfig<M, ?> column = grid.getColumnModel()
						.getColumn(colIndex);
				if (column != null
						&& column.getPath().equals(
								policyComponentTemplateNamePropertyPath)) {
					final M template = grid.getStore().get(event.getRowIndex());
					getUiHandlers().actionOpenTemplateEditor(template);
				}
			}
		});

		grid.getView().setViewConfig(new GridViewConfig<M>() {
			@Override
			public String getColStyle(final M model,
					final ValueProvider<? super M, ?> valueProvider,
					final int rowIndex, final int colIndex) {
				if (valueProvider.getPath().equals(
						policyComponentTemplateNamePropertyPath)) {
					return appearanceFactory.resources().css().clickableText();
				}
				return null;
			}
			@Override
			public String getRowStyle(final M model, final int rowIndex) {
				return null;
			}
		});
	}
}
