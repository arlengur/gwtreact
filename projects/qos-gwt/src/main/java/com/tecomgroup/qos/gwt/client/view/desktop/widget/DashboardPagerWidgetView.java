/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.tecomgroup.qos.gwt.client.presenter.widget.DashboardPagerPresenterWidget;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;

/**
 * @author kshnyakin.m
 * 
 */
public class DashboardPagerWidgetView
		extends
			ViewWithUiHandlers<DashboardPagerPresenterWidget>
		implements
			DashboardPagerPresenterWidget.MyView {

	private static Logger LOGGER = Logger
			.getLogger(DashboardPagerWidgetView.class.getName());

	private final AppearanceFactory appearanceFactory;

	private final HBoxLayoutContainer canvasContainer;

	private final double CANVAS_SIZE_COEF = 0.03;

	private int canvasSize;

	private final List<Canvas> pageCanvas;

	@Inject
	public DashboardPagerWidgetView(
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		appearanceFactory = appearanceFactoryProvider.get();
		canvasContainer = new HBoxLayoutContainer();
		canvasContainer.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		canvasContainer.setPack(BoxLayoutPack.CENTER);

		pageCanvas = new ArrayList<Canvas>();
	}

	@Override
	public Widget asWidget() {
		return canvasContainer;
	}

	@Override
	public void clear() {
		pageCanvas.clear();
		canvasContainer.clear();
	}

	private void clearCanvas(final Canvas canvas) {
		canvas.getContext2d().clearRect(0, 0, canvas.getOffsetWidth(),
				canvas.getOffsetHeight());
	}

	private void configureCanvas(final Canvas canvas, final int width,
			final int height) {
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		canvas.addStyleName(appearanceFactory.resources().css().cursorPointer());
	}

	private void configureCanvasSize() {
		int tempWidth = (int) Math.round(CANVAS_SIZE_COEF
				* Window.getClientWidth());
		if ((tempWidth % 2) != 0) {
			tempWidth++;
		}
		canvasSize = tempWidth;
	}

	private void createCircleCanvas(final List<Canvas> pageCanvas,
			final int pageCount) {
		for (int i = 0; i < pageCount; ++i) {
			final Canvas canvas = Canvas.createIfSupported();
			configureCanvas(canvas, canvasSize / 2, canvasSize / 2);
			pageCanvas.add(canvas);
			canvas.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					getUiHandlers().actionChangePage(
							DashboardPagerWidgetView.this.pageCanvas
									.indexOf(event.getSource()));
				}
			});
		}
	}

	/**
	 * Drawing left arrow by default (<code>rotateAngle == 0.0</code>)
	 * 
	 * @param context
	 * @param rotateAngle
	 */
	private void drawArrow(final Context2d context, final double rotateAngle) {

		// context.setFillStyle(ARROW_COLOR);
		context.setFillStyle(appearanceFactory.pagerAppearance()
				.getArrowColor());
		final double xTranslate = canvasSize / 2.0;
		final double yTranslate = canvasSize / 2.0;
		context.translate(xTranslate, yTranslate);
		context.rotate(rotateAngle);
		context.beginPath();
		context.moveTo(0 - xTranslate, canvasSize / 2.0 - yTranslate);
		context.lineTo(canvasSize / 2.0 - xTranslate, 0 - yTranslate);
		context.lineTo(canvasSize / 2.0 - xTranslate, canvasSize - yTranslate);
		context.closePath();
		context.fill();
		context.fillRect(canvasSize / 2.0 - xTranslate, canvasSize / 4.0
				- yTranslate, canvasSize / 2.0, canvasSize / 2.0);
	}

	private void drawCircle(final Context2d context, final double xCenter,
			final double yCenter, final double radius, final String fillColor) {
		context.setFillStyle(fillColor);
		context.arc(xCenter, yCenter, radius, 0, 2 * Math.PI, false);
		context.fill();
	}

	private void drawCircles(final List<Canvas> pageCanvas) {
		for (final Canvas canvas : pageCanvas) {
			final Context2d context = canvas.getContext2d();
			if (pageCanvas.indexOf(canvas) == getUiHandlers().getCurrentPage()) {
				drawCircle(context, canvasSize / 4.0, canvasSize / 4.0,
						canvasSize / 4.0, appearanceFactory.pagerAppearance()
								.getActivePageColor());
			} else {
				drawCircle(context, canvasSize / 4.0, canvasSize / 4.0,
						canvasSize / 4.0, appearanceFactory.pagerAppearance()
								.getInactivePageColor());
			}
		}
	}

	@Override
	public void drawNavigateWidget(final int pageCount) {
		clear();
		configureCanvasSize();
		final Canvas leftArrowCanvas = Canvas.createIfSupported();
		if (leftArrowCanvas == null) {
			LOGGER.log(Level.SEVERE, "Canvas is not supported");
			return;
		}
		configureCanvas(leftArrowCanvas, canvasSize, canvasSize);
		final Canvas rightArrowCanvas = Canvas.createIfSupported();
		if (rightArrowCanvas == null) {
			LOGGER.log(Level.SEVERE, "Canvas is not supported");
			return;
		}
		configureCanvas(rightArrowCanvas, canvasSize, canvasSize);

		leftArrowCanvas.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionChangePage(
						getUiHandlers().getCurrentPage() - 1);
			}
		});
		rightArrowCanvas.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().actionChangePage(
						getUiHandlers().getCurrentPage() + 1);
			}
		});
		createCircleCanvas(pageCanvas, pageCount);

		final Margins canvasMargins = new Margins(0, 5, 0, 5);
		canvasContainer.add(leftArrowCanvas, new BoxLayoutData(canvasMargins));
		for (final Canvas canvas : pageCanvas) {
			canvasContainer.add(canvas, new BoxLayoutData(canvasMargins));
		}
		canvasContainer.add(rightArrowCanvas, new BoxLayoutData(canvasMargins));

		drawArrow(leftArrowCanvas.getContext2d(), 0.0);
		drawArrow(rightArrowCanvas.getContext2d(), Math.PI);
		drawCircles(pageCanvas);
		canvasContainer.forceLayout();
	}

	@Override
	public void redrawNavigateWidget(final int previousPage, final int nextPage) {
		if (previousPage < pageCanvas.size()) {
			final Canvas previousPageCanvas = pageCanvas.get(previousPage);
			clearCanvas(previousPageCanvas);
			drawCircle(previousPageCanvas.getContext2d(), canvasSize / 4.0,
					canvasSize / 4.0, canvasSize / 4.0, appearanceFactory
							.pagerAppearance().getInactivePageColor());
		}

		if (nextPage < pageCanvas.size()) {
			final Canvas nextPageCanvas = pageCanvas.get(nextPage);
			clearCanvas(nextPageCanvas);
			drawCircle(nextPageCanvas.getContext2d(), canvasSize / 4.0,
					canvasSize / 4.0, canvasSize / 4.0, appearanceFactory
							.pagerAppearance().getActivePageColor());
		}
	}
}
