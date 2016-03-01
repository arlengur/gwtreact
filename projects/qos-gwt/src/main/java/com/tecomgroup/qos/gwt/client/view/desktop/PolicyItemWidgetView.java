/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.PolicyItemWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractEntityEditorDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ParameterSelectorWidgetWithAgentSelection;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.TrimEmptyValidator;

/**
 * @author ivlev.e
 * 
 */
public class PolicyItemWidgetView
		extends
			AbstractEntityEditorDialogView<MPolicy, PolicyItemWidgetPresenter>
		implements
			PolicyItemWidgetPresenter.MyView {

	public static Logger LOGGER = Logger.getLogger(PolicyItemWidgetView.class
			.getName());

	private static final int DEFAULT_WIDTH = 650;

	private final VerticalLayoutData noMarginVerticalData = new VerticalLayoutData(
			DEFAULT_WIDTH, -1);

	private static final int DEFAULT_HEIGHT = 360;

	private static final int FIELD_WIDTH = ClientConstants.DEFAULT_FIELD_WIDTH - 15;

	protected QoSMessages messages;

	private final ParameterSelectorWidgetWithAgentSelection paramSelector;

	protected final ComboBox<MAgent> agentComboBox;

	protected final ComboBox<MAgentTask> taskComboBox;

	protected final ComboBox<MResultParameterConfiguration> paramComboBox;

	protected TextField policyName;

	private CenterLayoutContainer widget;

	private final AppearanceFactory appearanceFactory;

	private ComboBox<MAlertType> alertTypesComponent;

	private CheckBox alertSendCheckbox;

	private VBoxLayoutContainer actionsPanel;

	private final BoxLayoutData marginSimple = new BoxLayoutData(new Margins(5));

	private final BoxLayoutData marginLabel = new BoxLayoutData(new Margins(5));

	private final BoxLayoutData marginFlex = new BoxLayoutData(new Margins(5));

	private VerticalLayoutContainer conditionsContainer;

	@Inject
	public PolicyItemWidgetView(final EventBus eventBus,
			final QoSMessages messages,
			final ParameterSelectorWidgetWithAgentSelection paramSelector,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		super(eventBus, messages);
		this.paramSelector = paramSelector;
		paramSelector.setOnlyActive(true);
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		agentComboBox = paramSelector.getAgentControl();
		taskComboBox = paramSelector.getTaskControl();
		paramComboBox = paramSelector.getParamControl();
		initialize();
	}

	@Override
	protected void actionOkButtonPressed() {
		if (validate()) {
			getUiHandlers().actionSavePolicy(taskComboBox.getValue());
		}
	}

	private CheckBox createActionCheckBox(final String message) {
		final CheckBox checkbox = new CheckBox();
		checkbox.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		checkbox.setBoxLabel(message);
		return checkbox;
	}

	@Override
	protected String getCreationDialogTitle() {
		return messages.createPolicyTitle();
	}

	@Override
	protected Widget getDialogContent() {
		agentComboBox.setWidth(FIELD_WIDTH);
		taskComboBox.setWidth(FIELD_WIDTH);
		paramComboBox.setWidth(FIELD_WIDTH);
		paramComboBox.setAllowBlank(false);

		policyName = new TextField();
		policyName.setWidth(FIELD_WIDTH);
		policyName.setAllowBlank(false);
		policyName.addValidator(new TrimEmptyValidator(messages));

		final VerticalLayoutContainer mainContainer = new VerticalLayoutContainer();
		mainContainer.setWidth(DEFAULT_WIDTH);

		marginLabel.setMinSize(150);
		marginFlex.setFlex(1.0);

		final TabPanel tabPanel = new TabPanel(
				appearanceFactory.tabPanelAppearance());
		tabPanel.setHeight(320);
		tabPanel.getContainer().addStyleName(
				appearanceFactory.resources().css()
						.themeLighterBackgroundColor());
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(final SelectionEvent<Widget> event) {
				// HACK: fix for user settings layout bug (at rare
				// occasion elements overlap occurs)
				if (event.getSelectedItem() instanceof HasLayout) {
					((HasLayout) event.getSelectedItem()).forceLayout();
				}
			}
		});

		conditionsContainer = new VerticalLayoutContainer();

		conditionsContainer.add(initializePolicyInfoPanel(),
				noMarginVerticalData);

		final Widget actionsContainer = initializeActionsPanel();

		tabPanel.add(conditionsContainer, messages.conditions());
		tabPanel.add(actionsContainer, messages.notifications());

		mainContainer.add(tabPanel, new VerticalLayoutData(DEFAULT_WIDTH, -1,
				new Margins(10)));

		mainContainer.setScrollMode(ScrollMode.AUTO);

		widget = new CenterLayoutContainer();
		widget.setStyleName(appearanceFactory.resources().css()
				.blackBackgroundColor());
		widget.setWidget(mainContainer);
		widget.setHeight(DEFAULT_HEIGHT);
		widget.setWidth(DEFAULT_WIDTH);
		initListeners();
		initValues();

		return widget;
	}

	private FieldLabel getFieldLabel(final Widget widget, final String label) {
		final FieldLabel fieldLabel = new FieldLabel(widget, label);
		fieldLabel.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		fieldLabel.addStyleName(appearanceFactory.resources().css()
				.textAlignRight());
		return fieldLabel;
	}

	private Label getLabel(final String text) {
		final Label label = new Label(text);
		label.addStyleName(appearanceFactory.resources().css().textMainColor());
		return label;
	}

	@Override
	protected String getUpdateDialogTitle() {
		return messages.editPolicyTitle();
	}

	private Widget initializeActionsPanel() {
		actionsPanel = new VBoxLayoutContainer();
		actionsPanel.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

		final SimpleContainer sendAlertActionsPanel = new SimpleContainer();
		sendAlertActionsPanel.getElement().setPadding(
				new Padding(20, 10, 20, 10));
		sendAlertActionsPanel.setStyleName(appearanceFactory.resources().css()
				.containerLigth());
		final FlexTable table = new FlexTable();
		table.setCellSpacing(10);

		alertSendCheckbox = createActionCheckBox(messages.sendAlert());
		table.setWidget(0, 0, getLabel(messages.policyNotificationType()));
		table.setWidget(1, 0, alertSendCheckbox);
		alertTypesComponent = new ComboBox<MAlertType>(
				new ListStore<MAlertType>(new ModelKeyProvider<MAlertType>() {
					@Override
					public String getKey(final MAlertType item) {
						return item.getName();
					}
				}), new LabelProvider<MAlertType>() {
					@Override
					public String getLabel(final MAlertType item) {
						return LabelUtils.getAlertTypeLabel(item);
					}
				});
		alertTypesComponent.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
		alertTypesComponent.getStore().addSortInfo(
				new StoreSortInfo<MAlertType>(new Comparator<MAlertType>() {
					@Override
					public int compare(final MAlertType o1, final MAlertType o2) {
						final String name1 = LabelUtils.getAlertTypeLabel(o1);
						final String name2 = LabelUtils.getAlertTypeLabel(o2);
						return name1 == null ? -1 : name1.compareTo(name2);
					}
				}, SortDir.ASC));
		alertTypesComponent.setAllowBlank(false);
		alertTypesComponent.setForceSelection(true);
		alertTypesComponent.setValidateOnBlur(false);
		alertTypesComponent.setTypeAhead(true);
		alertTypesComponent.setTriggerAction(TriggerAction.ALL);
		alertTypesComponent.setEditable(false);
		alertTypesComponent.setEmptyText(messages.alertType());

		table.setWidget(0, 1, getLabel(messages.alertType()));
		table.setWidget(1, 1, alertTypesComponent);
		sendAlertActionsPanel.setWidget(table);

		final BoxLayoutData sendAlertMargins = new BoxLayoutData(new Margins(
				10, 10, 0, 10));
		actionsPanel.add(sendAlertActionsPanel, sendAlertMargins);
		return actionsPanel;
	}

	private Widget initializePolicyInfoPanel() {
		final SimpleContainer conditionWidget = new SimpleContainer();
		conditionWidget.getElement().setPadding(new Padding(10, 0, 0, 0));

		final VerticalLayoutContainer conditionContainer = new VerticalLayoutContainer();
		final HBoxLayoutContainer conditionFirstRow = new HBoxLayoutContainer();

		conditionFirstRow.add(getFieldLabel(policyName, messages.name()),
				marginSimple);
		conditionFirstRow.add(getFieldLabel(taskComboBox, messages.task()),
				marginSimple);

		final HBoxLayoutContainer conditionSecondRow = new HBoxLayoutContainer();
		conditionSecondRow.add(getFieldLabel(agentComboBox, messages.agent()),
				marginSimple);
		conditionSecondRow.add(
				getFieldLabel(paramComboBox, messages.parameter()),
				marginSimple);

		conditionContainer.add(conditionFirstRow);
		conditionContainer.add(conditionSecondRow);
		conditionWidget.setWidget(conditionContainer);
		conditionWidget.setPixelSize(DEFAULT_WIDTH, 80);

		final CenterLayoutContainer panel = new CenterLayoutContainer();
		panel.setStyleName(appearanceFactory.resources().css()
				.containerDarkBlue());
		panel.getElement().getStyle().setWidth(100, Unit.PCT);
		panel.getElement().getStyle().setHeight(134, Unit.PX);
		panel.add(conditionWidget);

		return panel;
	}

	protected void initListeners() {
		agentComboBox.addSelectionHandler(new SelectionHandler<MAgent>() {
			@Override
			public void onSelection(final SelectionEvent<MAgent> event) {
				if (!MAbstractEntity.equals(agentComboBox.getValue(),
						event.getSelectedItem())) {
					agentComboBox.setValue(event.getSelectedItem());
					getUiHandlers().resetConditions();
					getUiHandlers().enableConditionsControls(false);
				}
			}
		});
		taskComboBox.addSelectionHandler(new SelectionHandler<MAgentTask>() {
			@Override
			public void onSelection(final SelectionEvent<MAgentTask> event) {
				if (!MAbstractEntity.equals(taskComboBox.getValue(),
						event.getSelectedItem())) {
					taskComboBox.setValue(event.getSelectedItem());
					getUiHandlers().resetConditions();
					getUiHandlers().enableConditionsControls(false);
				}
			}
		});
		paramComboBox
				.addValueChangeHandler(new ValueChangeHandler<MResultParameterConfiguration>() {
					@Override
					public void onValueChange(
							final ValueChangeEvent<MResultParameterConfiguration> event) {
						final boolean enabled = event.getValue() != null;
						getUiHandlers().enableConditionsControls(enabled);
						if (enabled) {
							final MResultParameterConfiguration parameter = event
									.getValue();
							getUiHandlers().setParameterType(
									parameter.getType());
							getUiHandlers().setUpConditions(
									getUiHandlers().getPolicy().getCondition());
						}
					}
				});

		alertSendCheckbox
				.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(
							final ValueChangeEvent<Boolean> event) {
						alertTypesComponent.setEnabled(event.getValue());
					}
				});
	}
	protected void initValues() {
		alertSendCheckbox.setValue(true, true);
	}

	private void refreshActions(final MPolicy policy) {
		boolean hasAlertAction = false;
		if (policy != null && policy.getActions() != null) {
			final List<MPolicyActionWithContacts> gridEditableActions = new ArrayList<MPolicyActionWithContacts>();
			for (final MPolicyAction action : policy.getActions()) {
				if (action instanceof MPolicySendAlert) {
					hasAlertAction = true;
					final String alertTypeName = ((MPolicySendAlert) action)
							.getAlertType();
					if (alertTypeName == null) {
						alertTypesComponent.setValue(null, true);
					} else {
						final MAlertType alertType = alertTypesComponent
								.getStore().findModelWithKey(alertTypeName);
						if (alertType == null) {
							LOGGER.warning("Alert type " + alertTypeName
									+ " not  found");
						}
						alertTypesComponent.setValue(alertType, true);
					}
				} else if (action instanceof MPolicyActionWithContacts) {
					gridEditableActions.add((MPolicyActionWithContacts) action);
				}
			}
			if (!gridEditableActions.isEmpty()) {
				getUiHandlers().setPolicyActionsToGrid(gridEditableActions);
			}
		}
		if (hasAlertAction) {
			alertSendCheckbox.setValue(true, true);
		} else {
			alertSendCheckbox.setValue(false, true);
			alertTypesComponent.setValue(null, true);
		}
	}

	private void refreshConditions(final MPolicy policy, final MAgentTask task) {
		if (policy != null
				&& policy.getCondition() != null
				&& policy.getCondition() instanceof MContinuousThresholdFallCondition) {
			final MContinuousThresholdFallCondition condition = (MContinuousThresholdFallCondition) policy
					.getCondition();
			final ParameterIdentifier identifier = condition
					.getParameterIdentifier();
			final MResultParameterConfiguration parameter = task
					.getResultConfiguration().findParameterConfiguration(
							identifier);
			if (parameter == null) {
				LOGGER.warning("Cannot find a parameter with ID=" + identifier);
			}
			paramComboBox.setValue(parameter, true);
		}
		getUiHandlers().refreshConditions();
	}

	@Override
	public void refreshPolicy() {
		policyName.reset();
		agentComboBox.redraw();
		final MPolicy policy = getUiHandlers().getPolicy();
		if (policy == null || policy.getId() == null) {
			agentComboBox.setEnabled(true);
			paramSelector.enableControls();
		} else {
			agentComboBox.setEnabled(false);
			paramSelector.disableControls();
		}
		getUiHandlers().refreshThresholdType();
		policyName
				.setValue(policy == null ? "" : policy.getDisplayName(), true);
		final MAgentTask task = getUiHandlers().getPolicyTask();
		if (task != null) {
			agentComboBox.setValue(task.getModule().getAgent(), false);
			taskComboBox.setValue(task, true);
		}
		refreshConditions(policy, task);
		refreshActions(policy);
	}

	@Override
	public void reset() {
		refreshPolicy();
	}

	/**
	 * Retrieves policy entity from form. Used in EDIT and ADD mode
	 * 
	 * @return
	 */
	@Override
	public void savePolicy() {
		MPolicySendAlert sendAlertAction = null;
		if (alertSendCheckbox.getValue()) {
			sendAlertAction = new MPolicySendAlert();
			sendAlertAction.setAlertType(alertTypesComponent.getValue()
					.getName());
		}

		getUiHandlers().savePolicy(policyName.getValue(),
				paramComboBox.getValue().getParameterIdentifier(),
				sendAlertAction);
	}

	/**
	 * Sets agents both in EDIT or ADD mode
	 */
	@Override
	public void setAgents(final List<MAgent> agents) {
		paramSelector.reset();
		paramSelector.setAgents(agents);
	}

	@Override
	public void setAlertTypes(final List<MAlertType> types) {
		alertTypesComponent.getStore().clear();
		alertTypesComponent.getStore().addAll(getUiHandlers().getAlertTypes());
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot.equals(PolicyItemWidgetPresenter.POLICY_ACTIONS_TEMPLATE_GRID_SLOT)) {
			final BoxLayoutData layoutData = new BoxLayoutData(new Margins(0,
					10, 5, 10));
			layoutData.setFlex(1);
			actionsPanel.add(content, layoutData);
		} else if (slot
				.equals(PolicyItemWidgetPresenter.POLICY_CONDITIONS_TEMPLATE_GRID_SLOT)) {
			conditionsContainer.add(content.asWidget(), new VerticalLayoutData(
					DEFAULT_WIDTH - 20, 160));
		}
	}

	/**
	 * Validates policy before save
	 * 
	 * @return
	 */
	@Override
	public boolean validate() {
		boolean valid = policyName.validate();
		if (valid) {
			// Validation doesn't work for disabled fields
			if (!(valid = (paramComboBox.getValue() != null))) {
				AppUtils.showErrorMessage(messages.parameterNotSelected());
			}
		}
		if (valid) {
			valid = getUiHandlers().validatePolicyConditions();
		}
		if (valid) {
			valid = validateAlertActions();
		}
		if (valid) {
			valid = getUiHandlers().validateNotificationActions();
		}
		return valid;
	}

	/**
	 * Validates alert policy action list. It shouldn't be empty
	 * 
	 * @return
	 */
	protected boolean validateAlertActions() {
		boolean res = false;
		if (alertSendCheckbox.getValue()) {
			if (alertTypesComponent.getValue() == null) {
				AppUtils.showErrorMessage(messages
						.alertTypeNotSelectedMessage());
			} else {
				res = true;
			}
		}
		if (!alertSendCheckbox.getValue()
		// FIXME Add sms and email
		// && !smsSendCheckbox.getValue() &&
		// !emailSendCheckbox.getValue()
		) {
			AppUtils.showInfoWithConfirmMessage(messages
					.emptyPolicyActionsMessage());
		}
		return res;
	}
}
