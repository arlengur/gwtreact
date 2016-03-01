/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.gwt.client.ActionListener;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.GroupInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserLabelProvider;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserValueProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DisabledGridViewConfig;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.UserProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractEntityEditorDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomComboBox;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.IconedActionCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;

/**
 * @author ivlev.e
 * 
 */
public class GroupInformationWidgetView
		extends
			AbstractEntityEditorDialogView<MUserGroup, GroupInformationWidgetPresenter>
		implements
			GroupInformationWidgetPresenter.MyView {

	private TextField groupNameField;

	private TextButton addUserButton;

	private CustomComboBox<MUser> searchUserComboBox;

	private final UserProperties properties = GWT.create(UserProperties.class);

	private Grid<MUser> grid;

	private final DialogFactory dialogFactory;

	private final static UserValueProvider userValueProvider = new UserValueProvider();

	private final static Comparator<MUser> userComparator = new Comparator<MUser>() {

		@Override
		public int compare(final MUser left, final MUser right) {
			int result = userValueProvider.getValue(left).compareTo(
					userValueProvider.getValue(right));

			if (left.isDisabled() && !right.isDisabled()) {
				result = 1;
			} else if (!left.isDisabled() && right.isDisabled()) {
				result = -1;
			}

			return result;
		}
	};

	@Inject
	public GroupInformationWidgetView(final EventBus eventBus,
			final QoSMessages messages, final DialogFactory dialogFactory) {
		super(eventBus, messages);
		this.dialogFactory = dialogFactory;
		initializeGrid();
	}

	@Override
	protected void actionOkButtonPressed() {
		getUiHandlers().actionOkButtonPressed();
	}

	private ColumnConfig<MUser, String> createIconedColumn(
			final String columnDisplayName, final ImageResource icon,
			final ActionListener<String> callback) {

		final IconedActionCell<String> deletionActionCell = new IconedActionCell<String>(
				appearanceFactory.<String> iconedActionCellAppearance(icon,
						columnDisplayName), columnDisplayName,
				new ActionCell.Delegate<String>() {

					@Override
					public void execute(final String treeGridRow) {
						callback.onActionPerformed(treeGridRow);
					}
				});
		final ColumnConfig<MUser, String> deletionActionColumn = new ColumnConfig<MUser, String>(
				properties.login(), 5);
		deletionActionColumn.setCell(deletionActionCell);

		return deletionActionColumn;
	}

	@Override
	protected String getCreationDialogTitle() {
		return messages.createNewGroupTitle();
	}

	@Override
	protected Widget getDialogContent() {
		final SimpleContainer parentContainer = new SimpleContainer();
		parentContainer.setWidth(300);
		parentContainer.setHeight(400);
		final VerticalLayoutContainer vertical = new VerticalLayoutContainer();
		parentContainer.add(vertical);

		groupNameField = new TextField();
		groupNameField.addValidator(new TrimEmptyValidator(messages));
		final VerticalLayoutData nameFieldLayoutData = new VerticalLayoutData(
				1, -1, new Margins(5, 0, 5, 0));
		vertical.add(groupNameField, nameFieldLayoutData);
		final ListStore<MUser> usersStore = new ListStore<MUser>(
				properties.key());
		searchUserComboBox = new CustomComboBox<MUser>(usersStore,
				new UserLabelProvider(messages));
		searchUserComboBox.setUpdateValueOnSelection(true);
		searchUserComboBox.setTypeAhead(true);
		searchUserComboBox.setEditable(true);
		searchUserComboBox.setTriggerAction(TriggerAction.ALL);
		searchUserComboBox.setForceSelection(true);
		searchUserComboBox.setEmptyText(messages.enterLogin());
		final HBoxLayoutContainer horizontal = new HBoxLayoutContainer();
		horizontal.addStyleName(appearanceFactory.resources().css()
				.themeLightBackgroundColor());
		final VerticalLayoutData searchRowLayoutData = new VerticalLayoutData(
				1, -1, new Margins(0, 0, 2, 0));
		vertical.add(horizontal, searchRowLayoutData);
		horizontal.setPadding(new Padding(5));
		horizontal.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
		final BoxLayoutData flex = new BoxLayoutData();
		flex.setFlex(1);
		horizontal.add(searchUserComboBox, flex);

		addUserButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellLightAppearance()),
				messages.actionAdd());
		addUserButton.setIcon(appearanceFactory.resources().addButtonMini());
		addUserButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(final SelectEvent event) {
				final MUser user = searchUserComboBox.getValue();
				if (user != null) {
					if (grid.getStore().findModel(user) == null) {
						grid.getStore().add(user);
						searchUserComboBox.setValue(null);
						searchUserComboBox.redraw(true);
						searchUserComboBox.setText(null);
					} else {
						AppUtils.showErrorMessage(messages
								.userExistsInTheGroup());
					}
				}
			}
		});
		final BoxLayoutData layoutData = new BoxLayoutData(new Margins(0, 20,
				0, 5));
		horizontal.add(addUserButton, layoutData);
		vertical.add(grid, new VerticalLayoutData(1, 1));

		return parentContainer;
	}

	@Override
	protected String getUpdateDialogTitle() {
		return messages.editGroupTitle();
	}

	private List<ColumnConfig<MUser, ?>> getGridColumns() {
		final List<ColumnConfig<MUser, ?>> list = new ArrayList<ColumnConfig<MUser, ?>>();
		list.add(new ColumnConfig<MUser, String>(userValueProvider));
		list.add(createIconedColumn(messages.excludeFromGroup(),
				appearanceFactory.resources().deleteButton(),
				new ActionListener<String>() {

					@Override
					public void onActionPerformed(final String userKey) {
						final MUser user = grid.getStore().findModelWithKey(
								userKey);
						if (user != null) {
							grid.getStore().remove(user);
						}
					}

				}));
		return list;
	}

	@Override
	public String getGroupName() {
		return groupNameField.getValue();
	}

	@Override
	public List<MUser> getGroupUsers() {
		return new ArrayList<MUser>(grid.getStore().getAll());
	}

	private void initializeGrid() {
		final ListStore<MUser> store = new ListStore<MUser>(properties.key());
		store.addSortInfo(new StoreSortInfo<MUser>(userComparator, SortDir.ASC));
		final ColumnModel<MUser> columnModel = new ColumnModel<MUser>(
				getGridColumns());
		grid = new Grid<MUser>(store, columnModel, new CustomGridView<MUser>(
				appearanceFactory.gridTemplatesAppearance(),
				appearanceFactory.columnHeaderAppearance()));
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.addStyleName(ClientConstants.QOS_GRID_TEMPLATES_STYLE);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.setHideHeaders(true);
		final GridView<MUser> gridView = grid.getView();
		gridView.setAutoFill(true);
		gridView.setStripeRows(true);
		gridView.setViewConfig(new DisabledGridViewConfig<MUser>(
				appearanceFactory));
	}

	@Override
	public void reset() {
		groupNameField.reset();
		grid.getStore().clear();
	}

	@Override
	public void setAllUsers(final List<MUser> users) {
		searchUserComboBox.getStore().clear();
		searchUserComboBox.getStore().addAll(users);
		searchUserComboBox.reset();
	}

	@Override
	public void setGroupName(final String groupName) {
		groupNameField.setValue(groupName);
	}

	@Override
	public void setGroupUsers(final List<MUser> users) {
		grid.getStore().clear();
		grid.getStore().addAll(users);
	}

	@Override
	public void showErrorMessage(final String title, final String message) {
		dialogFactory.createWarningDialog(title, message).show();
	}

	@Override
	public boolean validate() {
		return groupNameField.validate();
	}
}
