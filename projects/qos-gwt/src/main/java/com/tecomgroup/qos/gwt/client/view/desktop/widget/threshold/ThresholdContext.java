/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.threshold;

import com.google.gwt.canvas.client.Canvas;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;

/**
 * Encapsulating {@link ThresholdStrategy} context
 * 
 * @author ivlev.e
 * 
 */
class ThresholdContext {

	private final ThresholdStrategy strategy;

	public ThresholdContext(final ThresholdStrategy strategy,
			final Canvas canvas) {
		this.strategy = strategy;
		this.strategy.setCanvas(canvas);
	}

	public void executeStrategy(final Double warningThreshold,
			final Double criticalThreshold, final ThresholdType thresholdType,
			final RectangleSize size) {
		strategy.paintThreshold(warningThreshold, criticalThreshold,
				thresholdType, size);
	}
}
