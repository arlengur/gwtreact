/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.util.concurrent.Callable;

/**
 * General interface of executable task which can report its status.
 * 
 * @author kunilov.p
 * 
 */
public interface CallableTask<R> extends StatusReporter, Callable<R> {

}
