/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.tecomgroup.qos.ChartType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardChartWidget;
import com.tecomgroup.qos.dashboard.DashboardChartWidget.ChartSeriesData;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.ResultsAnalyticsPresenter.MyView;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ChartResultUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.RenameDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.RenameDialog.RenameHandler;

/**
 * @author ivlev.e
 *
 */
public class ChartToolbar {

	public static interface AddChartToDashboardDialogListener {

		public void onAddChartToDashboard(DashboardChartWidget widget);

	}

	public enum LineType {
		LINE("Line"), AREA("Area"), SPLINE("Spline"), COLUMN("Column"), AREARANGE(
				"Arearange"), STEP("Step");

		private String name;

		LineType(final String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	BeforeSelectionHandler<Item> typeHandler = new BeforeSelectionHandler<Item>() {

		@Override
		public void onBeforeSelection(final BeforeSelectionEvent<Item> event) {
			final CheckMenuItem item = (CheckMenuItem) event.getItem();
			if (selectedLineType != item) {
				final String oldTypeName = getLineType();
				selectedLineType = item;
				final String typeDisplayName = item.getItemId();
				changeChartType(oldTypeName, typeDisplayName.toLowerCase());
			}
		}
	};

	ClickHandler renameHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			final RenameHandler handler = new RenameHandler() {

				@Override
				public void rename(final String oldName, final String newName) {
					setChartName(newName);
					view.getPresenter()
							.updateWidgetIconsRelatedToChart(newName);
				}
			};
			final RenameDialog dialog = dialogFactory.createRenameDialog(
					handler, chartName, view);
			dialog.show();
		}
	};

	private final AddChartToDashboardDialogListener addChartDialogListener;

	ClickHandler exportToImageHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			ChartResultUtils.exportChart(chartName, view.getTimeZone());
		}
	};

	ClickHandler tableViewHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			final String[] seriesIds = ChartResultUtils
					.getVisibleSeriesKeys(chartName);

			if (seriesIds.length > 0) {
				final Double[] extremes = ChartResultUtils.getExtremes(
						chartName, ChartResultUtils.Axis.X);
				view.getPresenter().revealTableView(chartName, seriesIds,
						extremes, view.getTimeZone());
			} else {
				AppUtils.showInfoMessage(messages
						.chartSeriesAreHidden(chartName));
			}
		}
	};

	ClickHandler addWidgetHandler = new ClickHandler() {
		@Override
		public void onClick(final ClickEvent event) {
			final boolean addedToFavourites = !((ToggleButton) event
					.getSource()).getValue();
			if (addedToFavourites) {
				AppUtils.showErrorMessage(messages.widgetAlreadyExists());
				setWidgetIconState(true);
			} else {
				final TimeInterval interval = view.getTimeInterval();
				if (interval.getType() == TimeInterval.Type.CUSTOM) {
					AppUtils.showErrorMessage(messages
							.fixedIntervalChartWidgetsNotAllowed());
					setWidgetIconState(false);
				} else {
					final DashboardChartWidget widget = new DashboardChartWidget();
					widget.setTitle(chartName);
					widget.setChartName(chartName);
					widget.setChartType(type);
					widget.setLineType(selectedLineType.getItemId());
					widget.setIntervalType(interval.getType());
					widget.setTimezone(interval.getTimeZone());
					widget.setTimezoneType(interval.getTimeZoneType());
					final List<ChartSeriesData> seriesData = view
							.getPresenter().getChartSeriesDataByChartName(
									chartName);
					widget.setSeriesData(seriesData);
					final List<MChartSeries> series = ChartResultUtils
							.findSeriesByChartName(view.getPresenter()
									.getAllSeries(), chartName);
					widget.setSeries(series);

					widget.setAutoscalingEnabled(autoscalingButton.getValue());
					widget.setThresholdsEnabled(thresholdsButton.getValue());

					addChartDialogListener.onAddChartToDashboard(widget);

					setWidgetIconState(false);
				}
			}
		}
	};

	ClickHandler thresholdsHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			final boolean thresholdsEnabled = ((ToggleButton) event.getSource())
					.getValue();
			ChartResultUtils.manageChartThresholdLines(chartName,
					thresholdsEnabled);
			setThresholdsValue(thresholdsEnabled);
		}
	};

	ClickHandler autoscalingHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			final boolean autoscalingEnabled = ((ToggleButton) event
					.getSource()).getValue();
			ChartResultUtils.manageAutoscaling(chartName, type.isSingle(),
					autoscalingEnabled);
			setAutoscalingValue(autoscalingEnabled);
		}
	};

	ClickHandler mouseTrackingHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			final boolean mouseTrackingEnabled = ((ToggleButton) event
					.getSource()).getValue();
			ChartResultUtils.setMouseTrackingEnabled(chartName,
					mouseTrackingEnabled);
			setMouseTrackingValue(mouseTrackingEnabled);

		}
	};

	ClickHandler captionsHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			final boolean captionsEnabled = ((ToggleButton) event.getSource())
					.getValue();
			final boolean thresholdsEnabled = thresholdsButton.getValue();
			ChartResultUtils.manageCaptions(chartName, captionsEnabled,
					thresholdsEnabled);
			setCaptionsValue(captionsEnabled);
		}
	};

	ClickHandler undoZoomHandler = new ClickHandler() {

		@Override
		public void onClick(final ClickEvent event) {
			ChartResultUtils.undoZoom(chartName);
		}
	};

	private CheckMenuItem selectedLineType;

	private final ChartType type;

	private String chartName;

	private final String divElementId;

	private final VerticalLayoutContainer container;

	private final HBoxLayoutContainer toolbar;

	private final QoSMessages messages;

	private final Menu typeMenu;

	private final Label chartNameLabel;

	private final ToggleButton addWidgetButton;

	private final ToggleButton autoscalingButton;

	private final ToggleButton thresholdsButton;

	private final PushButton renameButton;

	private final PushButton exportToImageButton;

	private final PushButton tableViewButton;

	private final PushButton undoZoomButton;

	private final TextButton chartTypeButton;

	private final ToggleButton mouseTrackingButton;

	private final ToggleButton captionsButton;

	private final AppearanceFactory appearanceFactory;

	private final MyView view;

	private final DialogFactory dialogFactory;

	public ChartToolbar(final String chartName, final ChartType type,
			final QoSMessages messages, final String divElementId,
			final MyView view, final AppearanceFactory appearanceFactory,
			final DialogFactory dialogFactory,
			final AddChartToDashboardDialogListener addChartDialogListener) {
		this.chartName = chartName;
		this.messages = messages;
		this.divElementId = divElementId;
		this.type = type;
		this.view = view;
		this.appearanceFactory = appearanceFactory;
		this.dialogFactory = dialogFactory;
		this.addChartDialogListener = addChartDialogListener;

		final Image createWidgetIcon = new Image(appearanceFactory.resources()
				.createWidgetIcon());
		createWidgetIcon.getElement().getStyle().setMarginTop(3, Unit.PX);
		final Image createdWidgetIcon = new Image(appearanceFactory.resources()
				.createdWidgetIcon());
		createdWidgetIcon.getElement().getStyle().setMarginTop(3, Unit.PX);
		addWidgetButton = new ToggleButton(createWidgetIcon, createdWidgetIcon,
				addWidgetHandler);
		addWidgetButton.getElement().getStyle()
		.setProperty("background", "none");

		addWidgetButton.setTitle(messages.createWidget());

		thresholdsButton = new ToggleButton(AbstractImagePrototype.create(
				appearanceFactory.resources().thresholdsToggleUp())
				.createImage(), AbstractImagePrototype.create(
						appearanceFactory.resources().thresholdsToggleDown())
						.createImage(), thresholdsHandler);
		setThresholdsValue(true);

		autoscalingButton = new ToggleButton(AbstractImagePrototype.create(
				appearanceFactory.resources().autoscalingToggleUp())
				.createImage(), AbstractImagePrototype.create(
						appearanceFactory.resources().autoscalingToggleDown())
						.createImage(), autoscalingHandler);
		setAutoscalingValue(true);

		undoZoomButton = new PushButton(
				AbstractImagePrototype.create(
						appearanceFactory.resources().undoZoomButtonUp())
						.createImage(), AbstractImagePrototype.create(
								appearanceFactory.resources().undoZoomButtonDown())
								.createImage(), undoZoomHandler);
		undoZoomButton.setTitle(messages.undoZoom());

		renameButton = new PushButton(AbstractImagePrototype.create(
				appearanceFactory.resources().renameButtonUp()).createImage(),
				AbstractImagePrototype.create(
						appearanceFactory.resources().renameButtonDown())
						.createImage(), renameHandler);
		renameButton.setTitle(messages.actionRename());

		exportToImageButton = new PushButton(AbstractImagePrototype.create(
				appearanceFactory.resources().exportButtonUp()).createImage(),
				AbstractImagePrototype.create(
						appearanceFactory.resources().exportButtonDown())
						.createImage(), exportToImageHandler);
		exportToImageButton.setTitle(messages.saveAsImage());

		tableViewButton = new PushButton(AbstractImagePrototype.create(
				appearanceFactory.resources().tableButtonUp()).createImage(),
				AbstractImagePrototype.create(
						appearanceFactory.resources().tableButtonDown())
						.createImage(), tableViewHandler);
		tableViewButton.setTitle(messages.viewInTable());

		final TextButtonCell cell = new TextButtonCell(
				appearanceFactory.<String> buttonCellRectangleAppearance());
		cell.setIcon(appearanceFactory.resources().chartTypeButton());
		chartTypeButton = new TextButton(cell, this.messages.chartType());
		typeMenu = new Menu();
		typeMenu.addBeforeSelectionHandler(typeHandler);
		chartTypeButton.setMenu(typeMenu);

		mouseTrackingButton = new ToggleButton(AbstractImagePrototype.create(
				appearanceFactory.resources().mouseTrackingButtonUp())
				.createImage(), AbstractImagePrototype.create(
						appearanceFactory.resources().mouseTrackingButtonDown())
						.createImage(), mouseTrackingHandler);
		mouseTrackingButton.setTitle(messages.disableMouseTracking());

		captionsButton = new ToggleButton(
				AbstractImagePrototype.create(
						appearanceFactory.resources().captionsToggleUp())
						.createImage(), AbstractImagePrototype.create(
								appearanceFactory.resources().captionsToggleDown())
								.createImage(), captionsHandler);
		captionsButton.setTitle(messages.seriesCaptions());

		container = new VerticalLayoutContainer();
		container.addStyleName(appearanceFactory.resources().css().chartItem());
		container.setBorders(false);

		toolbar = new HBoxLayoutContainer();
		toolbar.setLayoutData(new VerticalLayoutData(1, -1));

		chartNameLabel = new Label(chartName);
		chartNameLabel.addStyleName(appearanceFactory.resources().css()
				.text18px());

		final BoxLayoutData boxMarginData = new BoxLayoutData(new Margins(10,
				0, 0, 10));
		final BoxLayoutData boxMarginDataFlex = new BoxLayoutData(new Margins(
				10, 0, 0, 10));
		final BoxLayoutData boxMarginEndData = new BoxLayoutData(new Margins(
				10, 10, 0, 10));

		boxMarginDataFlex.setFlex(1);

		toolbar.add(addWidgetButton, boxMarginData);
		toolbar.add(createDelimeter(), boxMarginData);
		toolbar.add(renameButton, boxMarginData);
		toolbar.add(chartNameLabel, boxMarginData);
		toolbar.add(new Label(), boxMarginDataFlex);
		toolbar.add(exportToImageButton, boxMarginData);
		toolbar.add(tableViewButton, boxMarginData);
		toolbar.add(createDelimeter(), boxMarginData);
		toolbar.add(thresholdsButton, boxMarginData);
		toolbar.add(autoscalingButton, boxMarginData);
		toolbar.add(undoZoomButton, boxMarginData);
		toolbar.add(chartTypeButton, boxMarginData);
		toolbar.add(mouseTrackingButton, boxMarginEndData);
		toolbar.add(captionsButton, boxMarginEndData);
		container.add(toolbar);
		final SimplePanel divElement = new SimplePanel();
		divElement.getElement().setId(divElementId);
		container.add(divElement);
		configureToolbar();
	}
	public Widget asWidget() {
		return container;
	}

	protected void changeChartType(final String oldType, final String newType) {
		switch (type) {
			case LEVEL_SINGLE :
			case PERCENTAGE_SINGLE :
				ChartResultUtils.changeTypeSingleSeriesChart(chartName,
						oldType, newType);
				break;
			case LEVEL_MULTIPLE :
			case COUNTER :
			case PERCENTAGE_MULTIPLE :
				ChartResultUtils.changeTypeMultipleSeriesChart(chartName,
						oldType, newType);
				break;
			case BOOL :
				ChartResultUtils.changeTypeBoolChart(chartName, oldType,
						newType);
				break;
			default :
				break;
		}
	}

	protected void configureToolbar() {
		typeMenu.clear();

		CheckMenuItem item;
		switch (type) {
			case LEVEL_SINGLE :
			case PERCENTAGE_SINGLE :
				item = createLineTypeMenuItem(messages.chartTypeColumn(),LineType.COLUMN);
				item.setChecked(true);
				selectedLineType = item;
				typeMenu.add(item);
				item = createLineTypeMenuItem(messages.chartTypeLine(),LineType.LINE);
				typeMenu.add(item);
				item = createLineTypeMenuItem(messages.chartTypeSpline(),LineType.SPLINE);
				typeMenu.add(item);
				item = createLineTypeMenuItem(messages.chartTypeArea(),LineType.AREA);
				typeMenu.add(item);
				setThresholdsValue(true);
				//disableThresholds();
				setCaptionsValue(false);
				// autoscalling should be enabled according to
				// http://rnd/issues/2016
				// setAutoscalingValue(false);
				enableAutoscaling();
				break;
			case LEVEL_MULTIPLE :
			case COUNTER :
			case PERCENTAGE_MULTIPLE :
				item = createLineTypeMenuItem(messages.chartTypeLine(),LineType.LINE);
				item.setChecked(true);
				selectedLineType = item;
				typeMenu.add(item);
				item = createLineTypeMenuItem(messages.chartTypeSpline(),LineType.SPLINE);
				typeMenu.add(item);
				item = createLineTypeMenuItem(messages.chartTypeArea(),LineType.AREA);
				typeMenu.add(item);
				setCaptionsValue(false);
				break;
			case BOOL :
				// chartTypeButton.setEnabled(false);
				item = createLineTypeMenuItem(messages.chartTypeStep(),LineType.STEP);
				item.setChecked(true);
				selectedLineType = item;
				typeMenu.add(item);
				item = createLineTypeMenuItem(messages.chartTypeArearange(),LineType.AREARANGE);
				typeMenu.add(item);
				disableThresholds();
				disableAutoscaling();
				setCaptionsValue(true);
				break;
			default :
				break;
		}
		setMouseTrackingValue(true);
	}

	private Image createDelimeter() {
		return AbstractImagePrototype.create(
				appearanceFactory.resources().buttonsDelimiter()).createImage();
	}

	private CheckMenuItem createLineTypeMenuItem(final String lineTypeName,LineType lineType) {
		final CheckMenuItem item = new CheckMenuItem(lineTypeName);
		item.setData(lineType.getName(),lineType);
		item.setItemId(lineType.getName());
		item.setGroup("radios");
		return item;
	}

	protected void disableAutoscaling() {
		autoscalingButton.setEnabled(false);
		autoscalingButton.setTitle(messages.inactive());
	}

	protected void disableCaptions() {
		captionsButton.setEnabled(false);
		captionsButton.setTitle(messages.inactive());
	}

	protected void disableThresholds() {
		thresholdsButton.setEnabled(false);
		thresholdsButton.setTitle(messages.inactive());
	}

	protected void enableAutoscaling() {
		autoscalingButton.setEnabled(true);
		autoscalingButton.setTitle(autoscalingButton.getValue() ? messages
				.disableAutoscaling() : messages.enableAutoscaling());
	}

	protected void enableCaptions() {
		captionsButton.setEnabled(true);
		captionsButton.setTitle(captionsButton.getValue() ? messages
				.disableCaptions() : messages.enableCaptions());
	}

	protected void enableThresholds() {
		thresholdsButton.setEnabled(true);
		thresholdsButton.setTitle(thresholdsButton.getValue() ? messages
				.disableThresholds() : messages.enableThresholds());
	}

	/**
	 * @return the chartName
	 */
	public String getChartName() {
		return chartName;
	}

	/**
	 * @return the divElementId
	 */
	public String getDivElementId() {
		return divElementId;
	}

	public String getLineType() {
		return selectedLineType.getItemId().toLowerCase();
	}

	/**
	 * @return the type
	 */
	public ChartType getType() {
		return type;
	}

	public boolean isAutoscalingEnabled() {
		return autoscalingButton.getValue();
	}

	public boolean isCaptionsEnabled() {
		return captionsButton.getValue();
	}

	public boolean isMouseTrackingEnabled() {
		return mouseTrackingButton.getValue();
	}

	public boolean isRendered() {
		return container.isRendered();
	}

	public boolean isThresholdsEnabled() {
		return thresholdsButton.getValue();
	}

	public void setAutoscalingValue(final boolean autoscalingEnabled) {
		autoscalingButton.setValue(autoscalingEnabled);
		autoscalingButton.setTitle(autoscalingEnabled ? messages
				.disableAutoscaling() : messages.enableAutoscaling());
	}

	protected void setCaptionsValue(final boolean captionsEnabled) {
		captionsButton.setValue(captionsEnabled);
		captionsButton.setTitle(captionsEnabled
				? messages.disableCaptions()
				: messages.enableCaptions());
	}

	/**
	 * @param chartName
	 *            the chartName to set
	 */
	public void setChartName(final String chartName) {
		this.chartName = chartName;
		chartNameLabel.setText(chartName);
		toolbar.forceLayout();
	}

	public void setLineType(final String type) {
		for (int i = 0; i < typeMenu.getWidgetCount(); i++) {
			((CheckMenuItem) typeMenu.getWidget(i)).setChecked(false);
		}
		final Widget item = typeMenu.getItemByItemId(type);
		if (item != null) {
			((CheckMenuItem) item).setChecked(true);
			selectedLineType = (CheckMenuItem) item;
		}
	}

	protected void setMouseTrackingValue(final boolean mouseTrackingEnabled) {
		mouseTrackingButton.setValue(mouseTrackingEnabled);
		mouseTrackingButton.setTitle(mouseTrackingEnabled ? messages
				.disableMouseTracking() : messages.enableMouseTracking());
	}

	public void setThresholdsValue(final boolean thresholdsEnabled) {
		thresholdsButton.setValue(thresholdsEnabled);
		thresholdsButton.setTitle(thresholdsEnabled ? messages
				.disableThresholds() : messages.enableThresholds());
	}

	public void setWidgetIconState(final boolean active) {
		addWidgetButton.setDown(active);
	}
}
