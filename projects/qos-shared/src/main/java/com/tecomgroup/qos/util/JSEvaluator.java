/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.tecomgroup.qos.exception.ServiceException;
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

	private final ScriptEngineManager manager;

	private final ScriptEngine engine;

	private JSEvaluator() {
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("JavaScript");
	}

	@Override
	public Double evaluate(final String expression,
			final Map<String, String> parameters) {
		Object result;
		try {
			for (final Map.Entry<String, String> parameter : parameters
					.entrySet()) {
				engine.put(parameter.getKey(), parameter.getValue());
			}
			result = engine.eval(expression);
		} catch (final ScriptException e) {
			throw new ServiceException("Can't evaluate " + expression, e);
		}

		return result instanceof Double ? (Double) result : Double
				.valueOf(result.toString());
	}
}
