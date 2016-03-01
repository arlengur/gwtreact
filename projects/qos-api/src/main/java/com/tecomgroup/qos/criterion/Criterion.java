/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.criterion;

/**
 * @author abondin
 * 
 */
public interface Criterion {

	public enum BinaryCompositeOperation implements Operation {
		AND, OR
	}

	public enum BinaryOperation implements Operation {
		EQ, LIKE, ILIKE, LE, GE, IN, CONTAINS
	}

	public interface Operation {

	}

	public enum TernaryOperation implements Operation {
		BETWEEN
	}

	public enum UnaryCompositeOperation implements Operation {
		NOT
	}

	public enum UnaryOperation implements Operation {
		isNull, isNotNull, isEmpty, isNotEmpty
	}
}
