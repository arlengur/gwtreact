/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;

/**
 * @author ivlev.e
 * 
 */
abstract class ThresholdStrategy {

	protected Canvas canvas;

	public static final String TEXT_COLOR = "#FFF";

	protected double getStripeHeight(final double segmentHeight) {
		return segmentHeight * 2;
	}

	protected double getStripeWidth(final double segmentHeight) {
		return segmentHeight * 0.5;
	}

	protected void paintStripeAndText(final RectangleSize segment,
			final String color, final double width, final double height,
			final String text) {
		final Context2d ctx = canvas.getContext2d();
		ctx.setFillStyle(color);
		final double x = segment.x0 + segment.width - width / 2;
		final double y = segment.y0;
		ctx.fillRect(x, y + segment.height / 2 - height / 2, width, height);
		ctx.setFillStyle(TEXT_COLOR);
		ctx.fillText(text, x, y - 3);
	}

	abstract protected void paintStripsAndText(Double leftThreshold,
			Double rightThreshold, ThresholdType thresholdType,
			RectangleSize[] segments);

	abstract protected void paintThreshold(Double warningThreshold,
			Double criticalThreshold, ThresholdType thresholdType,
			RectangleSize size);

	protected void selectColorsAndPaintStripsAndText(
			final ThresholdType thresholdType, final String colorNormal,
			final String severityColor, final double stripeWidth,
			final double stripHeight, final String thresholdToPaint,
			final RectangleSize segment) {
		String paintColor;
		switch (thresholdType) {
			case GREATER :
			case LESS :
				paintColor = colorNormal;
				paintStripeAndText(segment, paintColor, stripeWidth,
						stripHeight, thresholdToPaint);
				break;
			case GREATER_OR_EQUALS :
			case LESS_OR_EQUALS :
				paintColor = severityColor;
				paintStripeAndText(segment, paintColor, stripeWidth,
						stripHeight, thresholdToPaint);
				break;
			default :
				break;
		}
	}

	protected void setCanvas(final Canvas canvas) {
		this.canvas = canvas;
	}

}
