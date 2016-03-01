/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.ActionListener;
import com.tecomgroup.qos.gwt.client.model.TreeGridRow;
import com.tecomgroup.qos.gwt.client.model.users.UserRow;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.GroupManagerWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.ColumnModelHelper;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.TreeGridRowModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataTreeGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.agent.TreeGridFields;

/**
 * @author ivlev.e
 * 
 */
public class GroupManagerWidgetView
		extends
			AbstractLocalDataTreeGridView<TreeGridRow, GroupManagerWidgetPresenter>
		implements
			GroupManagerWidgetPresenter.MyView {

	private final DialogFactory dialogFactory;

	/**
	 * Sort {@link TreeGridRow} by name and disabled if it is related to
	 * {@link MUser}.
	 */
	private final static Comparator<TreeGridRow> treeGridRowComparator = new Comparator<TreeGridRow>() {

		@Override
		public int compare(final TreeGridRow left, final TreeGridRow right) {
			int result = left.getName().compareTo(right.getName());

			if (left instanceof UserRow && right instanceof UserRow) {
				final MUser leftUser = ((UserRow) left).getUser();
				final MUser rightUser = ((UserRow) right).getUser();

				if (leftUser.isDisabled() && !rightUser.isDisabled()) {
					result = 1;
				} else if (!leftUser.isDisabled() && rightUser.isDisabled()) {
					result = -1;
				}
			}

			return result;
		}
	};

	@Inject
	public GroupManagerWidgetView(final DialogFactory dialogFactory) {
		super();
		this.dialogFactory = dialogFactory;
	}

	private ValueProvider<TreeGridRow, String> createActionValueProvider(
			final String path) {
		return new ValueProviderWithPath<TreeGridRow, String>(path) {

			@Override
			public String getValue(final TreeGridRow row) {
				if (row instanceof UserRow) {
					return null;
				}
				return row.getName();
			}
		};
	}

	@Override
	protected GridViewConfig<TreeGridRow> createGirdViewConfig() {
		return new GridViewConfig<TreeGridRow>() {

			@Override
			public String getColStyle(final TreeGridRow model,
					final ValueProvider<? super TreeGridRow, ?> valueProvider,
					final int rowIndex, final int colIndex) {
				return null;
			}

			@Override
			public String getRowStyle(final TreeGridRow model,
					final int rowIndex) {
				String style = appearanceFactory.resources().css()
						.textDefaultGridColor();

				if (model instanceof UserRow
						&& ((UserRow) model).getUser().isDisabled()) {
					style = appearanceFactory.resources().css()
							.textDisabledColor();
				}

				return style;
			}
		};
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridTemplatesAppearance();
	}

	private QoSDialog createGroupDeletionConfirmationDialog(
			final String groupName) {
		return dialogFactory.createWarningDialog(
				messages.groupDeletionConfirmation(),
				messages.groupDeletionConfirmationMessage(groupName),
				new ConfirmationHandler() {

					@Override
					public void onCancel() {
						// do nothing
					}

					@Override
					public void onConfirm(final String comment) {
						getUiHandlers().actionRemoveGroup(groupName);
					}
				});
	}

	private ColumnConfig<TreeGridRow, ?> createNameColumn() {
		return new ColumnConfig<TreeGridRow, String>(
				new ValueProviderWithPath<TreeGridRow, String>(
						TreeGridFields.NAME.toString()) {

					@Override
					public String getValue(final TreeGridRow row) {
						return row.getName();
					}
				});
	}

	@Override
	protected TreeStore<TreeGridRow> createStore() {
		final TreeStore<TreeGridRow> store = new TreeStore<TreeGridRow>(
				new TreeGridRowModelKeyProvider());
		store.addSortInfo(new Store.StoreSortInfo<TreeGridRow>(
				treeGridRowComparator, SortDir.ASC));
		return store;
	}

	@Override
	protected List<IsWidget> createToolbarButtons() {
		final List<IsWidget> buttons = new ArrayList<IsWidget>();
		final Image createGroupButton = AbstractImagePrototype.create(
				appearanceFactory.resources().newGroup()).createImage();
		createGroupButton.addStyleName(appearanceFactory.resources().css()
				.cursorPointer());
		createGroupButton.setTitle(messages.createNewGroup());
		createGroupButton.getElement().<XElement> cast()
				.setMargins(new Margins(3, 0, 2, 10));

		final Label createGroupLabel = new Label(messages.createNewGroup());
		createGroupLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		final ClickHandler clickHandler = new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionOpenGroupCreationDialog();
			}
		};
		createGroupLabel.addClickHandler(clickHandler);
		createGroupButton.addClickHandler(clickHandler);
		buttons.add(createGroupButton);
		buttons.add(createGroupLabel);
		return buttons;
	}

	@Override
	protected List<ColumnConfig<TreeGridRow, ?>> getGridColumns() {
		final List<ColumnConfig<TreeGridRow, ?>> columns = new ArrayList<ColumnConfig<TreeGridRow, ?>>();
		columns.add(createPaddingColumn());
		columns.add(createNameColumn());
		columns.add(ColumnModelHelper.createIconedColumn(messages.edit(),
				appearanceFactory.resources().editButton(),
				new ActionListener<String>() {

					@Override
					public void onActionPerformed(final String groupName) {
						getUiHandlers().actionOpenGroupEditDialog(groupName);
					}

				}, createActionValueProvider(messages.edit())));
		columns.add(ColumnModelHelper.createIconedColumn(messages.delete(),
				appearanceFactory.resources().deleteButton(),
				new ActionListener<String>() {

					@Override
					public void onActionPerformed(final String groupName) {
						createGroupDeletionConfirmationDialog(groupName).show();
					}

				}, createActionValueProvider(messages.delete())));
		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE,
				ClientConstants.QOS_GRID_TEMPLATES_STYLE};
	}

	@Override
	protected int getTreeColumnIndex() {
		return 1;
	}

	@Override
	public void onGroupRemoved(final String name) {
		final TreeStore<TreeGridRow> store = grid.getTreeStore();
		final TreeGridRow group = store.findModelWithKey(name);
		if (group != null) {
			store.remove(group);
		}
		grid.getView().refresh(false);
	}

	@Override
	public void onGroupSaved(final TreeGridRow group,
			final List<TreeGridRow> users) {
		final TreeStore<TreeGridRow> store = grid.getTreeStore();
		final TreeGridRow foundGroup = store.findModelWithKey(group.getKey());
		if (foundGroup != null) {
			store.remove(foundGroup);
		}
		store.add(group);
		store.add(group, users);
		grid.getView().refresh(false);
	}

	@Override
	public void setGroups(final Map<TreeGridRow, Collection<TreeGridRow>> groups) {
		final TreeStore<TreeGridRow> store = grid.getTreeStore();
		store.clear();
		store.add(new ArrayList<TreeGridRow>(groups.keySet()));
		for (final Entry<TreeGridRow, Collection<TreeGridRow>> entry : groups
				.entrySet()) {
			store.add(entry.getKey(),
					new ArrayList<TreeGridRow>(entry.getValue()));
		}
	}
}
