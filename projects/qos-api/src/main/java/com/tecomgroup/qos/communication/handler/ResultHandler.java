/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.communication.handler;

import java.util.List;

import com.tecomgroup.qos.communication.result.Result;

/**
 * @author kunilov.p
 * 
 */
public interface ResultHandler {

	public static class Interval {
		private final Result left;
		private final Result right;
		/**
		 * 
		 */
		public Interval(final Result left, final Result right) {
			this.left = left;
			this.right = right;
		}

		/**
		 * @return the left
		 */
		public Result getLeft() {
			return left;
		}

		/**
		 * @return the right
		 */
		public Result getRight() {
			return right;
		}
	}

	void handleIntervalResult(String taskKey, List<Interval> intervals);

	void handleSingleValueResult(String taskKey, List<Result> results);

}
