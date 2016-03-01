/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;

/**
 * Компонент, визуализирующий пороги параметров
 * 
 * @author ivlev.e
 * 
 */
public class ThresholdSlider {

	protected final ThresholdContext context;

	protected final Canvas canvas;

	protected final Context2d ctx;

	protected double width;

	protected double height;

	protected RectangleSize lineSize;

	public ThresholdSlider(final ThresholdContext context, final Canvas canvas) {
		this.context = context;
		this.canvas = canvas;
		this.ctx = canvas.getContext2d();
		initialize();
	}

	private void initialize() {
		width = canvas.getCoordinateSpaceWidth();
		height = canvas.getCoordinateSpaceHeight();

		lineSize = new RectangleSize();
		lineSize.x0 = 0d;
		lineSize.y0 = (double) Math.round(height * 0.65);
		lineSize.width = width;
		lineSize.height = (double) Math.round(height * 0.25);
	}

	void paintThreshold(final Double warningThreshold,
			final Double criticalThreshold, final ThresholdType thresholdType) {
		context.executeStrategy(warningThreshold, criticalThreshold,
				thresholdType, lineSize);
	}

}
