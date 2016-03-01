/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.util.Map;

/**
 * @author kunilov.p
 * 
 */
public interface Evaluator {

	/**
	 * 
	 * @param expression
	 * @return
	 */
	public abstract Double evaluate(String expression,
			Map<String, String> parameters);
}
