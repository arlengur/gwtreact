/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.gwt.client.messages.FormattedResultMessages;

/**
 * @author ivlev.e
 * 
 */
public class ThresholdCell extends AbstractCell<MParameterThreshold> {

	private final int width;

	private final int height;

	private final FormattedResultMessages messages;

	public ThresholdCell(final int width, final int height,
			final FormattedResultMessages messages) {
		this.width = width;
		this.height = height;
		this.messages = messages;
	}

	@Override
	public void render(final Context cellContext,
			final MParameterThreshold threshold, final SafeHtmlBuilder sb) {

		final Canvas canvas = Canvas.createIfSupported();

		if (threshold == null
				|| (threshold.getCriticalLevel() == null && threshold
						.getWarningLevel() == null) || canvas == null) {
			return;
		}

		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		canvas.getContext2d().setFont("9px arial");

		final Double warningThreshold = threshold.getWarningLevel();
		final Double criticalThreshold = threshold.getCriticalLevel();

		final ThresholdType thresholdType = threshold.getType();

		ThresholdContext context;
		if (warningThreshold != null && criticalThreshold != null) {
			context = new ThresholdContext(new DoubleThresholdStrategy(),
					canvas);
		} else {
			context = new ThresholdContext(
					new SingleThresholdStrategy(messages), canvas);
		}

		ThresholdSlider thresholdSlider = null;

		switch (thresholdType) {
			case LESS :
			case LESS_OR_EQUALS :
			case GREATER :
			case GREATER_OR_EQUALS :
			case EQUALS :
				thresholdSlider = new ThresholdSlider(context, canvas);
				break;
			default :
				break;
		}
		if (thresholdSlider != null) {
			thresholdSlider.paintThreshold(warningThreshold, criticalThreshold,
					thresholdType);
			sb.append(SafeHtmlUtils.fromTrustedString("<img src=\""
					+ canvas.toDataUrl() + "\" />"));
		}
	}

}
