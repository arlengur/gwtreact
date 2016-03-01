/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.policy.PolicyConditionsTemplateWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.LabelUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomComboBox;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.DefaultEmptyValidator;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class PolicyConditionsTemplateWidgetView
		extends
			ViewWithUiHandlers<PolicyConditionsTemplateWidgetPresenter>
		implements
			PolicyConditionsTemplateWidgetPresenter.MyView {

	private final Map<PerceivedSeverity, IsWidget> conditionWidgets = new TreeMap<PerceivedSeverity, IsWidget>(
			MAlert.SEVERITY_DESC_COMPARATOR);

	private static final int DEFAULT_THRESHOLD_CONTAINER_WIDTH = 300;

	private static final int DEFAULT_CONDITIONS_CONTAINER_WIDTH = 335;

	private static final int DEFAULT_CONTAINER_HEIGHT = 100;

	private final AppearanceFactory appearanceFactory;

	private final QoSMessages messages;

	private Widget widget;

	private TabPanel conditionsPanel;

	private ComboBox<ThresholdType> thresholdTypeComboBox;

	private Label ceaseThresholdLabel;

	private Widget thresholdContainer;

	private ThresholdType previousThresholdType;

	private ComboBox<MPolicyConditionsTemplate> templateComboBox;

	private CheckBox useTemplateCheckbox;

	@Inject
	public PolicyConditionsTemplateWidgetView(final QoSMessages messages) {
		this.appearanceFactory = AppearanceFactoryProvider.instance();
		this.messages = messages;
		initialize();
		toggleTemplateControls(false);
	}

	private void applyConditionsTemplate(
			final MPolicyConditionsTemplate template) {
		getUiHandlers().resetConditions();
		getUiHandlers().setPolicyCondition(template.getConditionLevels());
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private Widget createAndFillThresholdContainer() {
		final VBoxLayoutContainer thresholdContainer = new VBoxLayoutContainer();
		thresholdContainer.setPack(BoxLayoutPack.START);
		final Widget templateRow = createRow(useTemplateCheckbox,
				templateComboBox, 4);
		templateRow.setHeight("24px");
		thresholdContainer.add(templateRow, new BoxLayoutData(new Margins(0, 5,
				3, 5)));
		thresholdContainer.add(
				createRow(createLabelWithMainColor(messages
						.thresholdTypeRaiseMessage()), thresholdTypeComboBox,
						15), new BoxLayoutData(new Margins(8, 5, 5, 5)));
		thresholdContainer.add(
				createRow(createLabelWithMainColor(messages
						.thresholdTypeCeaseMessage()), ceaseThresholdLabel, 5),
				new BoxLayoutData(new Margins(8, 5, 5, 5)));

		thresholdContainer.setPixelSize(DEFAULT_THRESHOLD_CONTAINER_WIDTH,
				DEFAULT_CONTAINER_HEIGHT);

		return thresholdContainer;
	}

	private TabPanel createConditionsPanel() {
		final TabPanel panel = new TabPanel(
				appearanceFactory.tabPanelAppearance());
		panel.setPixelSize(DEFAULT_CONDITIONS_CONTAINER_WIDTH,
				DEFAULT_CONTAINER_HEIGHT);
		panel.setBorders(false);

		return panel;
	}

	private Label createLabelWithMainColor(final String text) {
		final Label label = new Label();
		if (text != null) {
			label.setText(text);
		}
		label.addStyleName(appearanceFactory.resources().css().textMainColor());
		return label;
	}

	private Widget createRow(final Widget leftPart, final Widget rightPart,
			final int marginBetween) {
		final CssFloatLayoutContainer container = new CssFloatLayoutContainer();
		container.setWidth(DEFAULT_THRESHOLD_CONTAINER_WIDTH - 10);
		container.add(rightPart);
		rightPart.getElement().getStyle().setFloat(Float.RIGHT);
		leftPart.getElement().getStyle().setMarginRight(marginBetween, Unit.PX);
		container.add(leftPart);
		leftPart.getElement().getStyle().setFloat(Float.RIGHT);

		return container;
	}

	private SimpleContainer createSimpleContainer(final Widget widget) {
		final SimpleContainer container = new SimpleContainer();
		container.add(widget);
		return container;
	}

	private CheckBox createTemplateCheckbox() {
		final CheckBox checkBox = new CheckBox();
		checkBox.addStyleName(appearanceFactory.resources().css()
				.textMainColor());
		checkBox.setBoxLabel(messages.conditionsTemplate() + ":");
		checkBox.getElement().getChild(0).getChild(0).<XElement> cast()
				.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		checkBox.getElement().getChild(0).getChild(1).<XElement> cast()
				.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(final ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					templateComboBox.enable();
					thresholdTypeComboBox.disable();
					getUiHandlers().actionEnableConditionWidget(false);
				} else {
					templateComboBox.reset();
					templateComboBox.disable();
					if (getUiHandlers().getParameterType() != ParameterType.BOOL) {
						thresholdTypeComboBox.enable();
					}
					getUiHandlers().actionEnableConditionWidget(true);
				}
			}
		});
		return checkBox;
	}

	private ComboBox<MPolicyConditionsTemplate> createTemplateComboBox() {
		final ListStore<MPolicyConditionsTemplate> thresholdTypeStore = new ListStore<MPolicyConditionsTemplate>(
				new ModelKeyProvider<MPolicyConditionsTemplate>() {

					@Override
					public String getKey(final MPolicyConditionsTemplate item) {
						return item.getName();
					}
				});
		final ComboBox<MPolicyConditionsTemplate> comboBox = new CustomComboBox<MPolicyConditionsTemplate>(
				thresholdTypeStore,
				new LabelProvider<MPolicyConditionsTemplate>() {

					@Override
					public String getLabel(final MPolicyConditionsTemplate item) {
						return item.getName();
					}
				});

		comboBox.setForceSelection(true);
		comboBox.setTypeAhead(true);
		comboBox.setTriggerAction(TriggerAction.ALL);
		comboBox.setEditable(false);
		comboBox.setWidth(140);
		comboBox.disable();
		comboBox.setEmptyText(messages.emptyTemplateText());

		comboBox.addValueChangeHandler(new ValueChangeHandler<MPolicyConditionsTemplate>() {
			@Override
			public void onValueChange(
					final ValueChangeEvent<MPolicyConditionsTemplate> event) {
				if (event.getValue() != null) {
					applyConditionsTemplate(event.getValue());
				}
			}
		});
		comboBox.addSelectionHandler(new SelectionHandler<MPolicyConditionsTemplate>() {
			@Override
			public void onSelection(
					final SelectionEvent<MPolicyConditionsTemplate> event) {
				if (event.getSelectedItem() != null) {
					applyConditionsTemplate(event.getSelectedItem());
				}
			}
		});

		return comboBox;
	}

	private ComboBox<ThresholdType> createThresholdTypeComboBox() {
		final ListStore<ThresholdType> thresholdTypeStore = new ListStore<ThresholdType>(
				new ModelKeyProvider<ThresholdType>() {

					@Override
					public String getKey(final ThresholdType item) {
						return item.toString();
					}
				});
		final ComboBox<ThresholdType> comboBox = new ComboBox<ThresholdType>(
				thresholdTypeStore, new LabelProvider<ThresholdType>() {

					@Override
					public String getLabel(final ThresholdType item) {
						return item.toString();
					}
				});

		comboBox.addValidator(new DefaultEmptyValidator<MParameterThreshold.ThresholdType>(
				messages));
		comboBox.setForceSelection(true);
		comboBox.setValidateOnBlur(true);
		comboBox.setTypeAhead(true);
		comboBox.setTriggerAction(TriggerAction.ALL);
		comboBox.setEditable(false);
		comboBox.setWidth(40);

		comboBox.addValueChangeHandler(new ValueChangeHandler<MParameterThreshold.ThresholdType>() {
			@Override
			public void onValueChange(
					final ValueChangeEvent<ThresholdType> event) {
				setThresholdLabel(event.getValue());
			}
		});
		comboBox.addSelectionHandler(new SelectionHandler<MParameterThreshold.ThresholdType>() {
			@Override
			public void onSelection(final SelectionEvent<ThresholdType> event) {
				setThresholdLabel(event.getSelectedItem());
			}
		});

		final Set<ThresholdType> thresholdTypes = new HashSet<ThresholdType>(
				Arrays.asList(ThresholdType.values()));
		thresholdTypes.remove(ThresholdType.NONE);
		comboBox.getStore().addAll(thresholdTypes);

		return comboBox;
	}

	@Override
	public void enableConditionControls(final boolean enabled) {
		thresholdTypeComboBox.setEnabled(enabled
				&& getUiHandlers().getParameterType() != ParameterType.BOOL);
		useTemplateCheckbox.setValue(false, true);
		useTemplateCheckbox.setEnabled(enabled);
		getUiHandlers().actionEnableConditionWidget(enabled);
	}

	@Override
	public MPolicyConditionsTemplate getConditionsTemplate() {
		MPolicyConditionsTemplate template = null;
		if (useTemplateCheckbox.getValue()) {
			template = templateComboBox.getValue();
		}

		return template;
	}

	@Override
	public ThresholdType getThresholdType() {
		return thresholdTypeComboBox.getValue();
	}

	public void initialize() {
		ceaseThresholdLabel = createLabelWithMainColor(null);
		thresholdTypeComboBox = createThresholdTypeComboBox();
		templateComboBox = createTemplateComboBox();
		useTemplateCheckbox = createTemplateCheckbox();
		thresholdContainer = createAndFillThresholdContainer();
		conditionsPanel = createConditionsPanel();
		widget = initializeWidget();
	}

	private Widget initializeWidget() {
		final HorizontalLayoutContainer container = new HorizontalLayoutContainer();
		container.add(createSimpleContainer(thresholdContainer),
				new HorizontalLayoutData(DEFAULT_THRESHOLD_CONTAINER_WIDTH,
						DEFAULT_CONTAINER_HEIGHT, new Margins(0, 5, 0, 0)));
		container.add(createSimpleContainer(conditionsPanel),
				new HorizontalLayoutData(
						DEFAULT_CONDITIONS_CONTAINER_WIDTH - 40,
						DEFAULT_CONTAINER_HEIGHT));

		final SimpleContainer widget = createSimpleContainer(container);
		widget.setPixelSize(DEFAULT_THRESHOLD_CONTAINER_WIDTH
				+ DEFAULT_CONDITIONS_CONTAINER_WIDTH, DEFAULT_CONTAINER_HEIGHT);
		final CenterLayoutContainer panel = new CenterLayoutContainer();
		panel.setStyleName(appearanceFactory.resources().css().containerLigth());
		panel.getElement().getStyle().setWidth(100, Unit.PCT);
		panel.getElement().getStyle().setHeight(100, Unit.PCT);
		panel.add(widget);
		return panel;
	}

	@Override
	public void refreshConditions() {
		IsWidget activeWidget = null;
		for (final Map.Entry<PerceivedSeverity, IsWidget> entry : conditionWidgets
				.entrySet()) {
			final IsWidget widget = entry.getValue();
			final TabItemConfig config = conditionsPanel
					.getConfig((Widget) widget);
			if (config instanceof TabItemConfigWithConditionLevel) {
				final boolean conditionEnabled = getUiHandlers()
						.isConditionEnabled(entry.getKey());
				((TabItemConfigWithConditionLevel) config)
						.setConditionEnabled(conditionEnabled);
				conditionsPanel.update((Widget) widget, config);
				if (activeWidget == null) {
					activeWidget = widget;
				}
			}
		}
		if (activeWidget != null) {
			conditionsPanel.setActiveWidget(activeWidget);
		}
	}

	@Override
	public void refreshThresholdType() {
		thresholdTypeComboBox.reset();
		templateComboBox.reset();
		enableConditionControls(false);
	}

	@Override
	public void reset() {
		thresholdTypeComboBox.reset();
		templateComboBox.setEmptyText(messages.emptyTemplateText());
		templateComboBox.reset();
		setThresholdLabel(null);
	}

	@Override
	public void selectConditionsTemplate(
			final MPolicyConditionsTemplate selectedConditionsTemplate) {
		templateComboBox.setValue(selectedConditionsTemplate);
		useTemplateCheckbox.setValue(true, true);
	}

	@Override
	public void setConditionsTemplates(
			final Collection<MPolicyConditionsTemplate> templates) {
		templateComboBox.getStore().clear();
		templateComboBox.getStore().addAll(templates);

		if (SimpleUtils.isNotNullAndNotEmpty(templates)) {
			templateComboBox.setEmptyText(messages.emptyTemplateText());
			useTemplateCheckbox.enable();
		} else {
			templateComboBox.setEmptyText(messages.noTemplatesText());
			useTemplateCheckbox.disable();
		}
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot instanceof PerceivedSeverity) {
			final PerceivedSeverity severity = (PerceivedSeverity) slot;
			final TabItemConfigWithConditionLevel tabItemConfig = new TabItemConfigWithConditionLevel(
					LabelUtils.getAllSeverityLabels(messages).get(slot),
					severity);
			conditionsPanel.add(content, tabItemConfig);
			conditionWidgets.put(severity, content.asWidget());
			conditionsPanel.forceLayout();
		}
	}

	@Override
	public void setParameterType(final ParameterType newParameterType,
			final ParameterType previousParameterType) {
		if (newParameterType != previousParameterType) {
			if (ParameterType.BOOL == newParameterType) {
				previousThresholdType = getThresholdType();
				setThresholdType(ThresholdType.EQUALS);
				thresholdTypeComboBox.disable();
			} else {
				if (ParameterType.BOOL == previousParameterType) {
					if (previousThresholdType != null) {
						setThresholdType(previousThresholdType);
					}
				}
				thresholdTypeComboBox.enable();
			}
		}
	}

	private void setThresholdLabel(final ThresholdType type) {
		ceaseThresholdLabel.setText(type != null
				? type.inverse().toString()
				: "");
	}

	@Override
	public void setThresholdType(final ThresholdType thresholdType) {
		thresholdTypeComboBox.setValue(thresholdType, true);
	}

	@Override
	public void toggleTemplateControls(final boolean enable) {
		if (enable) {
			useTemplateCheckbox.show();
			templateComboBox.show();
		} else {
			useTemplateCheckbox.hide();
			templateComboBox.hide();
		}
	}

	@Override
	public boolean validate() {
		return thresholdTypeComboBox.validate();
	}
}
