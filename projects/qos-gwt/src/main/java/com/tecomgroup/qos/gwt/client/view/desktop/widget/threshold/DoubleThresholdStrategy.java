/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.gwt.client.utils.ColorConstants;

/**
 * Стратегия рисования слайдера, когда параметр имеет два порога
 * 
 * @author ivlev.e
 */
class DoubleThresholdStrategy extends ThresholdStrategy {

	@Override
	protected void paintStripsAndText(final Double leftThreshold,
			final Double rightThreshold, final ThresholdType thresholdType,
			final RectangleSize[] segments) {

		final RectangleSize segment = segments[0];

		final double stripeWidth = getStripeWidth(segment.height);
		final double stripHeight = getStripeHeight(segment.height);

		String leftSegemntColor, rightSegmentColor;

		if (thresholdType == ThresholdType.GREATER
				|| thresholdType == ThresholdType.GREATER_OR_EQUALS) {
			leftSegemntColor = ColorConstants.getSeverityNone();
			rightSegmentColor = ColorConstants.getSeverityCritical();
		} else {
			leftSegemntColor = ColorConstants.getSeverityCritical();
			rightSegmentColor = ColorConstants.getSeverityNone();
		}

		String thresholdToPaint = thresholdType.toString() + leftThreshold;
		selectColorsAndPaintStripsAndText(thresholdType, leftSegemntColor,
				ColorConstants.getSeverityWarning(), stripeWidth, stripHeight,
				thresholdToPaint, segments[0]);

		thresholdToPaint = thresholdType.toString() + rightThreshold;
		selectColorsAndPaintStripsAndText(thresholdType,
				ColorConstants.getSeverityWarning(), rightSegmentColor,
				stripeWidth, stripHeight, thresholdToPaint, segments[1]);
	}

	@Override
	public void paintThreshold(final Double warningThreshold,
			final Double criticalThreshold, final ThresholdType thresholdType,
			final RectangleSize size) {
		final Context2d ctx = canvas.getContext2d();
		final RectangleSize[] segments = size.divideBy3();
		RectangleSize seg;
		switch (thresholdType) {
			case GREATER :
			case GREATER_OR_EQUALS :
				seg = segments[0];
				CanvasGradient gradient = ctx.createLinearGradient(seg.x0,
						seg.y0, seg.x0 + seg.width, seg.y0);
				gradient.addColorStop(0, "transparent");
				gradient.addColorStop(0.5, ColorConstants.getSeverityNone());
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

				seg = segments[1];
				ctx.setFillStyle(ColorConstants.getSeverityWarning());
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

				seg = segments[2];
				gradient = ctx.createLinearGradient(seg.x0, seg.y0, seg.x0
						+ seg.width, seg.y0);
				gradient.addColorStop(0.5, ColorConstants.getSeverityCritical());
				gradient.addColorStop(1, "transparent");
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);
				paintStripsAndText(warningThreshold, criticalThreshold,
						thresholdType, segments);
				break;

			case LESS :
			case LESS_OR_EQUALS :
				seg = segments[0];
				gradient = ctx.createLinearGradient(seg.x0, seg.y0, seg.x0
						+ seg.width, seg.y0);
				gradient.addColorStop(0, "transparent");
				gradient.addColorStop(0.5, ColorConstants.getSeverityCritical());
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

				seg = segments[1];
				ctx.setFillStyle(ColorConstants.getSeverityWarning());
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

				seg = segments[2];
				gradient = ctx.createLinearGradient(seg.x0, seg.y0, seg.x0
						+ seg.width, seg.y0);
				gradient.addColorStop(0.5, ColorConstants.getSeverityNone());
				gradient.addColorStop(1, "transparent");
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);
				paintStripsAndText(criticalThreshold, warningThreshold,
						thresholdType, segments);
				break;

			default :
				break;
		}
	}

	@Override
	protected void selectColorsAndPaintStripsAndText(
			final ThresholdType thresholdType, String colorNormal,
			String severityColor, final double stripeWidth,
			final double stripHeight, final String thresholdToPaint,
			final RectangleSize segment) {
		if (thresholdType == ThresholdType.LESS
				|| thresholdType == ThresholdType.LESS_OR_EQUALS) {
			final String colorSwapString = colorNormal;
			colorNormal = severityColor;
			severityColor = colorSwapString;
		}
		super.selectColorsAndPaintStripsAndText(thresholdType, colorNormal,
				severityColor, stripeWidth, stripHeight, thresholdToPaint,
				segment);
	}
}
