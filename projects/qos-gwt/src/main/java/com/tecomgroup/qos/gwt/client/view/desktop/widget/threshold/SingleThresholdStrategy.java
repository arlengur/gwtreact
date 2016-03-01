/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages;
import com.tecomgroup.qos.gwt.client.utils.ColorConstants;

/**
 * Стратегия рисования слайдера, когда параметр имеет только один порог
 * 
 * @author ivlev.e
 */
class SingleThresholdStrategy extends ThresholdStrategy {

	private final FormattedResultMessages messages;

	public SingleThresholdStrategy(final FormattedResultMessages messages) {
		this.messages = messages;
	}

	@Override
	protected void paintStripsAndText(final Double warningThreshold,
			final Double criticalThreshold, final ThresholdType thresholdType,
			final RectangleSize[] segments) {

		final String colorNormal = ColorConstants.getSeverityNone();
		final String severityColor = criticalThreshold != null ? ColorConstants
				.getSeverityCritical() : ColorConstants.getSeverityWarning();
		final double thresholdValue = warningThreshold == null
				? criticalThreshold
				: warningThreshold;
		final String thresholdToPaint = thresholdType.toString()
				+ thresholdValue;

		final RectangleSize segment = segments[0];
		final double stripeWidth = getStripeWidth(segment.height);
		final double stripHeight = getStripeHeight(segment.height);
		switch (thresholdType) {
			case LESS_OR_EQUALS :
			case LESS :
			case GREATER_OR_EQUALS :
			case GREATER :
				selectColorsAndPaintStripsAndText(thresholdType, colorNormal,
						severityColor, stripeWidth, stripHeight,
						thresholdToPaint, segment);
				break;
			case EQUALS :
				paintText(segments[0], stripeWidth, messages.no());
				paintText(segments[1], stripeWidth, messages.yes());
				break;
			default :
				break;
		}

	}

	private void paintText(final RectangleSize segment,
			final double stripeWidth, final String text) {
		final Context2d ctx = canvas.getContext2d();
		final double x = segment.x0 + segment.width - stripeWidth * 7;
		final double y = segment.y0;
		ctx.setFillStyle(ThresholdStrategy.TEXT_COLOR);
		ctx.fillText(text, x, y - 3);
	}

	@Override
	public void paintThreshold(final Double warningThreshold,
			final Double criticalThreshold, final ThresholdType thresholdType,
			final RectangleSize size) {
		final Context2d ctx = canvas.getContext2d();
		final String severityColor = criticalThreshold != null ? ColorConstants
				.getSeverityCritical() : ColorConstants.getSeverityWarning();
		RectangleSize[] segments = size.divideBy2();
		RectangleSize seg;
		switch (thresholdType) {
			case GREATER :
			case GREATER_OR_EQUALS :
				seg = segments[0];
				CanvasGradient gradient = ctx.createLinearGradient(seg.x0,
						seg.y0, seg.x0 + seg.width, seg.y0);
				gradient.addColorStop(0, "transparent");
				gradient.addColorStop(0.7, ColorConstants.getSeverityNone());
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

				seg = segments[1];
				gradient = ctx.createLinearGradient(seg.x0, seg.y0, seg.x0
						+ seg.width, seg.y0);
				gradient.addColorStop(0.3, severityColor);
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
				gradient.addColorStop(0.7, severityColor);
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

				seg = segments[1];
				gradient = ctx.createLinearGradient(seg.x0, seg.y0, seg.x0
						+ seg.width, seg.y0);
				gradient.addColorStop(0.3, ColorConstants.getSeverityNone());
				gradient.addColorStop(1, "transparent");
				ctx.setFillStyle(gradient);
				ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);
				paintStripsAndText(warningThreshold, criticalThreshold,
						thresholdType, segments);
				break;
			case EQUALS :
				seg = segments[0];
				final Double severityValue = warningThreshold == null
						? criticalThreshold
						: warningThreshold;
				if (severityValue.equals(0d)) {
					ctx.setFillStyle(severityColor);
					ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

					seg = segments[1];
					ctx.setFillStyle(ColorConstants.getSeverityNone());
					ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);
				} else {
					ctx.setFillStyle(ColorConstants.getSeverityNone());
					ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);

					seg = segments[1];
					ctx.setFillStyle(severityColor);
					ctx.fillRect(seg.x0, seg.y0, seg.width, seg.height);
				}
				final RectangleSize eqSegment1 = segments[0].divideBy2()[0];
				final RectangleSize eqSegment2 = segments[1].divideBy2()[0];
				segments = new RectangleSize[]{eqSegment1, eqSegment2};
				paintStripsAndText(warningThreshold, criticalThreshold,
						thresholdType, segments);
				break;
			default :
				break;
		}
	}

}
