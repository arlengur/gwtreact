/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.gwt.client.DashboardWidgetFactory;
import com.tecomgroup.qos.gwt.client.presenter.DashboardPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.common.TileAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.style.theme.base.panel.TileBaseAppearance.TileBaseStyle;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.chart.DashboardChartClientWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.DashboardGrid;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.DashboardGridCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.DashboardLogicalGrid.CellIndex;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.Tile;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.dashboard.TileContentElement;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.DashboardMapClientWidget;

/**
 * @author ivlev.e
 *
 */
public class DashboardView extends ViewWithUiHandlers<DashboardPresenter>
		implements
			DashboardPresenter.MyView {

	private static final int DELAY_FOR_REFRESH_AFTER_RESIZE = 1;

	private final DashboardGrid layout;

	private final AppearanceFactory appearanceFactory;


	private final Map<String, TileContentElement> currentWidgets = new HashMap<String, TileContentElement>();

    @Inject
	public DashboardView(final AppearanceFactoryProvider appearanceFacoryProvider) {
		appearanceFactory = appearanceFacoryProvider.get();
		layout = new DashboardGrid();
		layout.setStyleName(appearanceFactory.resources().css()
				.dashBoardTable(), true);
		initHandlers();
	}

	@Override
	public Widget asWidget() {
		return layout;
	}

	public void createPhysicalStructure(final int pageNumber) {
		final List<DashboardGridCell> widgets = layout
				.getPageWidgets(pageNumber);

		if (widgets != null) {
			final TileBaseStyle css = appearanceFactory.tileAppearance(false, false)
					.style();
			final DashboardWidgetFactory widgetFactory = getUiHandlers()
					.getWidgetFactory();

			for (final DashboardGridCell widgetCell : widgets) {
				final DashboardWidget widget = widgetCell.getWidget();
				final TileContentElement tileContentElement = widgetFactory
						.createWidget(widget);
				final TileAppearance appearance = appearanceFactory
						.tileAppearance(hasSaveButton(tileContentElement),
                                hasChartButton(tileContentElement),
								widgetCell.getRowspan(),
								widgetCell.getColspan());
				layout.setTile(widgetCell,
                               new Tile(tileContentElement, appearance));
				tileContentElement.initialize();
				currentWidgets.put(widget.getKey(), tileContentElement);
			}

			if (layout.hasAvailableCells(pageNumber)) {
				fillAvailableCellsByEmptyPlaceholders(
						layout.getAvailableCellsIndexes(pageNumber), css);
			}
			removeCellsWithoutChildren();

		}
	}


    @Override
	public void dashboardReloaded(final MDashboard dashboard) {
		layout.createLogicalStructure(new ArrayList<DashboardWidget>(dashboard
                .getWidgets().values()), dashboard.getRowNumber(), dashboard
                .getColumnNumber());
	}

	@Override
	public void destroyWidgetsCompletely() {
		for (final TileContentElement widget : currentWidgets.values()) {
			widget.destroy();
		}
		layout.removeAllRows();
		currentWidgets.clear();
	}

	@Override
	public void destroyWidgetsWithSavingState() {
		for (final TileContentElement widget : currentWidgets.values()) {
			widget.dispose();
		}
		layout.removeAllRows();
		currentWidgets.clear();
	}

	private void fillAvailableCellsByEmptyPlaceholders(
			final List<CellIndex> cellIndexes, final TileBaseStyle css) {
		final Element div = DOM.createDiv();
		div.addClassName(css.container_1x1());
		for (final CellIndex index : cellIndexes) {
			layout.setWidget(index.getRow(), index.getColumn(), new HTMLPanel(
					div.toString()));
			layout.setCellSize(index.getRow(), index.getColumn(), 1, 1);
		}
	}

	@Override
	public int getPageCount() {
		return layout.getPageCount();
	}

	private boolean hasSaveButton(final TileContentElement tileContentElement) {
		return tileContentElement instanceof DashboardMapClientWidget;
	}

    private boolean hasChartButton(final TileContentElement tileContentElement) {
        return tileContentElement instanceof DashboardChartClientWidget;
    }

	private void initHandlers() {
		// for correct drawing of tiles after window resize;
		Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(final ResizeEvent event) {
                refreshWithDelay(DELAY_FOR_REFRESH_AFTER_RESIZE);
            }
        });
	}

	@Override
	public void onRemoveWidgetFromDashboard(final String widgetKey) {
		final TileContentElement widget = currentWidgets.remove(widgetKey);
		if (widget != null) {
			widget.destroy();
		}
	}

	private void refreshWithDelay(final int delayInSeconds) {
		new Timer() {
			@Override
			public void run() {
				for (final TileContentElement element : currentWidgets.values()) {
					element.refresh();
				}
			}
		}.schedule(delayInSeconds * TimeConstants.MILLISECONDS_PER_SECOND);
	}

	private void removeCellsWithoutChildren() {
		// for removing <td></td> elements from table
		final Node tableBody = layout.getElement().getChild(1);
		final NodeList<Node> rowNodes = tableBody.getChildNodes();
		for (int i = 0; i < rowNodes.getLength(); i++) {
			Node nextCell = rowNodes.getItem(i).getFirstChild();
			while (nextCell != null) {
				final Node cellToCheck = nextCell;
				nextCell = nextCell.getNextSibling();
				if (!cellToCheck.hasChildNodes()) {
					cellToCheck.removeFromParent();
				}
			}
		}
	}

	@Override
	public void showPage(final int pageNumber) {
		destroyWidgetsWithSavingState();
		createPhysicalStructure(pageNumber);
	}
}
