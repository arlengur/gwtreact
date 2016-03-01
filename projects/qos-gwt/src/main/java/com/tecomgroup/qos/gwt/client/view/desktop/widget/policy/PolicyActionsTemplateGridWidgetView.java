/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.gwt.client.ActionListener;
import com.tecomgroup.qos.gwt.client.filter.LocalizedFilterFactory;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.messages.LocalizedRowEditorMessages;
import com.tecomgroup.qos.gwt.client.model.policy.ContactInformationLabelProvider;
import com.tecomgroup.qos.gwt.client.model.policy.ContactInformationModelKeyProvider;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyActionContactsValueProvider;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyActionType;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyActionWrapper;
import com.tecomgroup.qos.gwt.client.model.policy.PolicyActionWrapperProperties;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyActionsTemplateGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.ColumnModelHelper;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.StringModelKeyProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.ValueProviderWithPath;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractLocalDataGridView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomComboBox;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;

/**
 * @author ivlev.e
 * 
 */
public class PolicyActionsTemplateGridWidgetView
		extends
			AbstractLocalDataGridView<PolicyActionWrapper, PolicyActionsTemplateGridWidgetPresenter>
		implements
			PolicyActionsTemplateGridWidgetPresenter.MyView {

	private final static String ADD_BUTTON_ITEM_ID = "addPolicyTemplateActionButton";

	private GridRowEditing<PolicyActionWrapper> gridEditing;

	private ComboBox<MContactInformation> recipients;

	private final PolicyActionWrapperProperties policyActionWrapperProperties = GWT
			.create(PolicyActionWrapperProperties.class);

	private final Set<ColumnConfig<PolicyActionWrapper, ?>> actionColumns = new HashSet<ColumnConfig<PolicyActionWrapper, ?>>();

	private PolicyActionWrapper newPolicyAction = null;

	private TextButton saveButton;

	@Inject
	public PolicyActionsTemplateGridWidgetView(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final LocalizedFilterFactory filterFactory) {
		super(messages, appearanceFactoryProvider, dialogFactory, filterFactory);
	}

	@Override
	protected boolean addButtonsToToolbar() {
		final TextButton addButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellLightAppearance()),
				messages.addNotification());
		addButton.setId(ADD_BUTTON_ITEM_ID);
		addButton.setIcon(appearanceFactory.resources().addButtonMini());
		addButton.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		addButton.getElement().setMargins(new Margins(5, 0, 3, 0));
		addButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(final SelectEvent event) {
				newPolicyAction = new PolicyActionWrapper(
						PolicyActionType.EMAIL, null);
				store.add(0, newPolicyAction);

				final int rowIndex = store.indexOf(newPolicyAction);
				gridEditing.startEditing(new GridCell(rowIndex, rowIndex));
			}
		});

		toolbar.add(addButton);
		return true;
	}

	@Override
	public void clearObsoleteContacts(final Set<String> deletedKeys) {
		for (final PolicyActionWrapper wrapper : grid.getStore().getAll()) {
			if (deletedKeys.contains(wrapper.getRecipient().getKey())) {
				wrapper.setRecipient(null);
			}
		}
		grid.getView().refresh(false);
	}

	@Override
	protected List<Filter<PolicyActionWrapper, ?>> createFilters() {
		return Collections.emptyList();
	}

	@Override
	protected GridAppearance createGridAppearance() {
		return appearanceFactory.gridTemplatesAppearance();
	}

	@Override
	protected ListStore<PolicyActionWrapper> createStore() {
		return new ListStore<PolicyActionWrapper>(
				policyActionWrapperProperties.key());
	}

	private List<MContactInformation> getAllRecipients(
			final List<PolicyActionWrapper> actions) {
		final List<MContactInformation> recipients = new ArrayList<MContactInformation>();
		for (final PolicyActionWrapper wrapper : actions) {
			recipients.add(wrapper.getRecipient());
		}
		return recipients;
	}

	@Override
	protected List<ColumnConfig<PolicyActionWrapper, ?>> getGridColumns() {
		final List<ColumnConfig<PolicyActionWrapper, ?>> columns = new ArrayList<ColumnConfig<PolicyActionWrapper, ?>>();
		final ColumnConfig<PolicyActionWrapper, String> typeColumn = new ColumnConfig<PolicyActionWrapper, String>(
				policyActionWrapperProperties.type(), 100,
				messages.policyNotificationType());
		typeColumn.setMenuDisabled(true);
		typeColumn.setSortable(false);
		final ColumnConfig<PolicyActionWrapper, MContactInformation> recipientColumn = new ColumnConfig<PolicyActionWrapper, MContactInformation>(
				new PolicyActionContactsValueProvider(), 100,
				messages.recipients());

		recipientColumn.setMenuDisabled(true);
		recipientColumn.setSortable(false);
		recipientColumn.setCell(new AbstractCell<MContactInformation>() {

			@Override
			public void render(final Context context,
					final MContactInformation value, final SafeHtmlBuilder sb) {
				if (value != null) {
					sb.append(SafeHtmlUtils
							.fromTrustedString(ContactInformationLabelProvider
									.toLabel(value)));
				}
			}
		});

		columns.add(typeColumn);
		columns.add(recipientColumn);
		final ColumnConfig<PolicyActionWrapper, PolicyActionWrapper> editColumn = ColumnModelHelper
				.createIconedColumn(
						messages.edit(),
						appearanceFactory.resources().editButton(),
						new ActionListener<PolicyActionWrapper>() {

							@Override
							public void onActionPerformed(
									final PolicyActionWrapper target) {
								final int rowIndex = grid.getStore().indexOf(
										target);
								gridEditing.startEditing(new GridCell(rowIndex,
										0));
							}
						},
						new ValueProviderWithPath<PolicyActionWrapper, PolicyActionWrapper>(
								messages.edit()) {

							@Override
							public PolicyActionWrapper getValue(
									final PolicyActionWrapper object) {
								return object;
							}
						});
		columns.add(editColumn);
		final ColumnConfig<PolicyActionWrapper, PolicyActionWrapper> deleteColumn = ColumnModelHelper
				.createIconedColumn(
						messages.delete(),
						appearanceFactory.resources().deleteButton(),
						new ActionListener<PolicyActionWrapper>() {

							@Override
							public void onActionPerformed(
									final PolicyActionWrapper target) {
								grid.getStore().remove(target);
							}
						},
						new ValueProviderWithPath<PolicyActionWrapper, PolicyActionWrapper>(
								messages.delete()) {

							@Override
							public PolicyActionWrapper getValue(
									final PolicyActionWrapper object) {
								return object;
							}
						});
		columns.add(deleteColumn);
		actionColumns.add(editColumn);
		actionColumns.add(deleteColumn);
		return columns;
	}

	@Override
	protected String[] getGridStyles() {
		return new String[]{ClientConstants.QOS_GRID_STANDARD_STYLE,
				ClientConstants.QOS_GRID_TEMPLATES_STYLE,
				ClientConstants.QOS_GRID_UNSELECTABLE_STYLE};
	}

	@Override
	public List<PolicyActionWrapper> getPolicyActionWrappers() {
		// copy to ArrayList for later correct serialization
		return new ArrayList<PolicyActionWrapper>(grid.getStore().getAll());
	}

	private boolean hasDuplicateActions(final List<PolicyActionWrapper> actions) {
		final Map<String, Set<String>> actionTypeRecipientKeys = new HashMap<String, Set<String>>();

		for (final PolicyActionWrapper action : actions) {
			Set<String> keys = actionTypeRecipientKeys.get(action.getType());
			if (keys == null) {
				keys = new HashSet<String>();
				actionTypeRecipientKeys.put(action.getType(), keys);
			}
			keys.add(action.getRecipient().getKey());
		}

		int uniqueRecipientsCount = 0;
		for (final Set<String> keys : actionTypeRecipientKeys.values()) {
			uniqueRecipientsCount += keys.size();
		}

		return actions.size() != uniqueRecipientsCount;
	}

	@SuppressWarnings("unchecked")
	private void initializeEditing(
			final List<ColumnConfig<PolicyActionWrapper, ?>> columns) {
		final QoSMessages qosMessages = messages;
		gridEditing = new GridRowEditing<PolicyActionWrapper>(grid) {

			private Timer blankFieldValidationTimer;

			@Override
			public void cancelEditing() {
				super.cancelEditing();
				cancelMonitoring();
			}

			private void cancelMonitoring() {
				if (blankFieldValidationTimer != null) {
					blankFieldValidationTimer.cancel();
					blankFieldValidationTimer = null;
				}
			}

			@Override
			public void completeEditing() {
				super.completeEditing();
				cancelMonitoring();
			}

			@Override
			protected RowEditorComponent createRowEditor() {
				final RowEditorComponent rowEditor = new RowEditorComponent(
						appearanceFactory.rowEditorAppearance());

				messages = new LocalizedRowEditorMessages(qosMessages);
				final TextButton newButton = new TextButton(new TextButtonCell(
						appearanceFactory.<String> buttonCellLightAppearance()));
				saveButton = rowEditor.getSaveButton();
				final TextButton cancelButton = rowEditor.getCancelButton();

				saveButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(final SelectEvent event) {
						newPolicyAction = null;
						completeEditing();
					}
				});
				newButton.setText(messages.saveText());
				saveButton.getElement().setInnerHTML(
						newButton.getElement().getInnerHTML());
				saveButton.addStyleName(appearanceFactory.resources().css()
						.textMainColor());
				saveButton.getElement().getStyle().setWidth(40, Style.Unit.PX);

				newButton.setText(messages.cancelText());
				cancelButton.addSelectHandler(new SelectHandler() {

					@Override
					public void onSelect(final SelectEvent event) {
						cancelEditing();
						if (newPolicyAction != null
								&& newPolicyAction.getRecipient() == null) {
							store.remove(newPolicyAction);
						}
						recipients.clearInvalid();
					}
				});

				cancelButton.getElement().setInnerHTML(
						newButton.getElement().getInnerHTML());

				cancelButton.addStyleName(appearanceFactory.resources().css()
						.textMainColor());

				return rowEditor;
			}

			@Override
			protected <N, O> Widget doStartEditing(
					final ColumnConfig<PolicyActionWrapper, N> c,
					final PolicyActionWrapper value) {
				if (actionColumns.contains(c)) {
					return new Label("");
				} else {
					return super.doStartEditing(c, value);
				}
			}

			@Override
			protected void onClick(final ClickEvent event) {
			}

			@Override
			public void startEditing(final GridCell cell) {
				super.startEditing(cell);
				blankFieldValidationTimer = new Timer() {

					@Override
					public void run() {
						saveButton.setEnabled(validateRecipients()
								? true
								: false);
					}
				};
				blankFieldValidationTimer.scheduleRepeating(200);
			}
		};

		// must be false. Use custom monitoring blankFieldValidationTimer
		// instead of it.
		gridEditing.setMonitorValid(false);

		final ColumnConfig<PolicyActionWrapper, String> notificationTypeColumn = (ColumnConfig<PolicyActionWrapper, String>) columns
				.get(0);
		final ColumnConfig<PolicyActionWrapper, MContactInformation> recipientsColumn = (ColumnConfig<PolicyActionWrapper, MContactInformation>) columns
				.get(1);
		initializeNotificationTypeEditing(notificationTypeColumn);
		initializeRecipientsEditing(recipientsColumn);
	}

	@Override
	protected void initializeGrid() {
		store = createStore();

		gridAppearance = createGridAppearance();

		final List<ColumnConfig<PolicyActionWrapper, ?>> columns = getGridColumns();

		grid = new Grid<PolicyActionWrapper>(store,
				new ColumnModel<PolicyActionWrapper>(columns),
				new CustomGridView<PolicyActionWrapper>(gridAppearance,
						appearanceFactory.columnHeaderAppearance())) {

			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				PolicyActionsTemplateGridWidgetView.this.onAfterFirstAttach();
			}

			@Override
			protected void onAttach() {
				super.onAttach();
				PolicyActionsTemplateGridWidgetView.this.onAttach();
			}

		};

		for (final String style : getGridStyles()) {
			grid.addStyleName(style);
		}

		grid.getView().setStripeRows(true);
		grid.getView().setAutoFill(true);
		grid.getView().setColumnLines(true);
		grid.getStore().setAutoCommit(true);

		initializeEditing(columns);
		grid.getSelectionModel().setSelectionMode(null);
	}

	private void initializeNotificationTypeEditing(
			final ColumnConfig<PolicyActionWrapper, String> notificationTypeColumn) {
		final ListStore<String> store = new ListStore<String>(
				new StringModelKeyProvider());
		final ComboBox<String> combo = new ComboBox<String>(store,
				new StringLabelProvider<String>());
		setUpCombo(combo);
		combo.setEditable(false);
		combo.getStore().add(PolicyActionType.EMAIL.toString());
		combo.getStore().add(PolicyActionType.SMS.toString());
		gridEditing.addEditor(notificationTypeColumn, combo);
	}

	private void initializeRecipientsEditing(
			final ColumnConfig<PolicyActionWrapper, MContactInformation> recipientsColumn) {
		final ListStore<MContactInformation> store = new ListStore<MContactInformation>(
				new ContactInformationModelKeyProvider());
		recipients = new CustomComboBox<MContactInformation>(store,
				new ContactInformationLabelProvider());
		((CustomComboBox<MContactInformation>) recipients)
				.setUpdateValueOnSelection(true);
		setUpCombo(recipients);
		gridEditing.addEditor(recipientsColumn, recipients);
	}

	protected void onAttach() {
		grid.getView().layout();
	}

	@Override
	public void reset() {
		grid.getStore().clear();
	}

	@Override
	public void setAvailableContacts(final List<MContactInformation> contacts) {
		recipients.getStore().clear();
		recipients.getStore().addAll(contacts);
	}

	@Override
	public void setPolicyActions(final List<PolicyActionWrapper> actions) {
		grid.getStore().clear();
		grid.getStore().addAll(actions);
	}

	private <T> void setUpCombo(final ComboBox<T> combo) {
		combo.setEmptyText(messages.emptyRecipientText());
		combo.setAllowBlank(false);
		combo.setClearValueOnParseError(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setTypeAhead(true);
		combo.setForceSelection(true);
	}

	@Override
	public boolean validate(final boolean isEmptyValid) {
		boolean result = true;
		String errorMessage = null;
		if (!isEmptyValid && grid.getStore().size() == 0) {
			errorMessage = messages.templateMustContainActions();
		} else {
			final List<PolicyActionWrapper> actions = getPolicyActionWrappers();
			if (getAllRecipients(actions).contains(null)) {
				errorMessage = messages.recipientsAreRequered();
			} else if (hasDuplicateActions(actions)) {
				errorMessage = messages.duplicateActionsError();
			}
		}

		if (errorMessage != null) {
			result = false;
			AppUtils.showErrorMessage(errorMessage);
		}
		return result;
	}

	private boolean validateRecipients() {
		/**
		 * Don't use recipients.getCurrentValue(). It leads to
		 * http://rnd/issues/4670
		 */
		return recipients.validate(true);
	}
}