/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.model.template.TemplateGroupRow;
import com.tecomgroup.qos.gwt.client.model.template.TemplateRow;
import com.tecomgroup.qos.gwt.client.presenter.TemplatesGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.TreeGridRowModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataTreeGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.Anchor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.TreeGridFields;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meleshin.o
 * 
 * 
 */
public class TemplatesGridWidgetView
		extends
			AbstractLocalDataTreeGridView<TreeGridRow, TemplatesGridWidgetPresenter>
		implements
			TemplatesGridWidgetPresenter.MyView {

	private FramedPanel framedPanel;

	private BorderLayoutContainer outerContainer;

	private final DialogFactory dialogFactory;

	@Inject
	public TemplatesGridWidgetView(final DialogFactory dialogFactory) {
		super();
		this.dialogFactory = dialogFactory;
	}

	@Override
	public Widget asWidget() {
		return outerContainer;
	}

	private ValueProvider<TreeGridRow, TreeGridRow> createActionValueProvider(
			final String path) {
		return new ValueProviderWithPath<TreeGridRow, TreeGridRow>(path) {

			@Override
			public TreeGridRow getValue(final TreeGridRow treeGridRow) {
				if (treeGridRow instanceof TemplateGroupRow) {
					return null;
				}
				return treeGridRow;
			}
		};
	}

	private ColumnConfig<TreeGridRow, TreeGridRow> createDeletionActionColumn(
			final String columnDisplayName, final ImageResource icon) {

		final IconedActionCell<TreeGridRow> deletionActionCell = new IconedActionCell<TreeGridRow>(
				appearanceFactory.<TreeGridRow> iconedActionCellAppearance(
						icon, columnDisplayName), columnDisplayName,
				new ActionCell.Delegate<TreeGridRow>() {

					@Override
					public void execute(final TreeGridRow row) {
						dialogFactory.createConfirmationDialog(
								new ConfirmationDialog.ConfirmationHandler() {

									@Override
									public void onCancel() {

									}

									@Override
									public void onConfirm(
											final String comment) {
										getUiHandlers().removeTemplateAction(row);
									}
								}, messages.deleteTemplate(),
								messages.deleteTemplateConfirmation(),
								ConfirmationDialog.CommentMode.DISABLED).show();
					}
				});
		final ColumnConfig<TreeGridRow, TreeGridRow> deletionActionColumn = new ColumnConfig<TreeGridRow, TreeGridRow>(
				createActionValueProvider(columnDisplayName), 5);
		deletionActionColumn.setCell(deletionActionCell);

		return deletionActionColumn;
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridTemplatesAppearance();
	}

	private ColumnConfig<TreeGridRow, TreeGridRow> createNameColumn() {
		final ColumnConfig<TreeGridRow, TreeGridRow> column = new ColumnConfig<TreeGridRow, TreeGridRow>(
				new ValueProvider<TreeGridRow, TreeGridRow>() {

					@Override
					public String getPath() {
						return TreeGridFields.NAME.toString();
					}

					@Override
					public TreeGridRow getValue(final TreeGridRow treeGridRow) {
						return treeGridRow;
					}

					@Override
					public void setValue(final TreeGridRow treeGridRow,
							final TreeGridRow value) {

					}

				}, 90);

		column.setCell(new AbstractCell<TreeGridRow>() {

			@Override
			public void render(final Context context, final TreeGridRow value,
					final SafeHtmlBuilder sb) {
				if (value instanceof TemplateRow) {

					final String linkHtml = getTemplateRowLinkHtml((TemplateRow) value);

					sb.append(SafeHtmlUtils.fromTrustedString(linkHtml));
				} else {
					sb.append(SafeHtmlUtils.fromString(value.getName()));
				}
			}
		});

		return column;
	}

	@Override
	protected TreeStore<TreeGridRow> createStore() {
		return new TreeStore<TreeGridRow>(new TreeGridRowModelKeyProvider());
	}

	@Override
	protected List<ColumnConfig<TreeGridRow, ?>> getGridColumns() {
		final List<ColumnConfig<TreeGridRow, ?>> columns = new ArrayList<ColumnConfig<TreeGridRow, ?>>();
		final ColumnConfig<TreeGridRow, TreeGridRow> deleteColumn = createDeletionActionColumn(
				messages.delete(), appearanceFactory.resources().deleteButton());

		columns.add(createPaddingColumn());
		columns.add(createNameColumn());
		columns.add(deleteColumn);
		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE,
				ClientConstants.QOS_GRID_TEMPLATES_STYLE,
				appearanceFactory.resources().css().textMainColor()};
	}

	@Override
	public TreeStore<TreeGridRow> getStore() {
		return grid.getTreeStore();
	}

	private String getTemplateRowLinkHtml(final TemplateRow templateRow) {
		final MUserAbstractTemplate template = templateRow.getTemplate();
		final PlaceRequest placeRequest = new PlaceRequest.Builder()
				.nameToken(
						getUiHandlers().getTemplateHref(
								template.getClass().getName()))
				.with(RequestParams.template, template.getName()).build();

		final String href = AppUtils.createHref(placeRequest);

		final String linkHtml = new Anchor(href, template.getName())
				.getElement().getString();

		return linkHtml;
	}

	@Override
	protected int getTreeColumnIndex() {
		return 1;
	}

	@Override
	protected void initializeWidget() {
		super.initializeWidget();
		outerContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());

		framedPanel = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());
		StyleUtils.configureNoHeaders(framedPanel);
		outerContainer.add(framedPanel);
		framedPanel.add(grid);
	}

	@Override
	public void refreshGridView() {
		grid.getView().refresh(false);
	}

	@Override
	public void updateTemplates(
			final List<? extends MUserAbstractTemplate> templates) {
		final TreeStore<TreeGridRow> store = grid.getTreeStore();
		TemplateGroupRow group = null;

		for (final MUserAbstractTemplate template : templates) {
			group = (TemplateGroupRow) store.findModelWithKey(template
					.getClass().getName());

			if (group != null) {
				store.add(group, new TemplateRow(template));
			}
		}

	}
}
