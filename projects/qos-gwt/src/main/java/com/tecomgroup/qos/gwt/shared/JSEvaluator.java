/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.shared;

import java.util.Map;

import com.tecomgroup.qos.util.Evaluator;

/**
 * @author kunilov.p
 * 
 */
public class JSEvaluator implements Evaluator {

	private volatile static Evaluator instance;

	public static Evaluator getInstance() {
		if (instance == null) {
			synchronized (JSEvaluator.class) {
				if (instance == null) {
					instance = new JSEvaluator();
				}
			}
		}
		return instance;
	}

	@Override
	public Double evaluate(final String expression,
			final Map<String, String> parameters) {
		String currentExpression = expression;
		for (final Map.Entry<String, String> parameter : parameters.entrySet()) {
			currentExpression = currentExpression.replace(parameter.getKey(),
					parameter.getValue());
		}
		return evaluateJS(currentExpression);
	}

	protected native double evaluateJS(String expression) /*-{
		return $wnd.eval(expression);
	}-*/;

}
