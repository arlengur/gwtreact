/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.EventBroadcaster;
import com.tecomgroup.qos.event.TransactionalEventBroadcaster;
import com.tecomgroup.qos.modelspace.ModelSpace;

/**
 * @author abondin
 * 
 */
public abstract class AbstractService {
	/**
	 * Default Spring profile name for Junit tests
	 */
	public static final String TEST_CONTEXT_PROFILE = "test";
	/**
	 * Default Spring profile name for Junit tests
	 */
	public static final String TEST_MEDIA_CONTEXT_PROFILE = "test-media";

	@Autowired
	protected ModelSpace modelSpace;
	@Autowired
	protected TransactionalEventBroadcaster eventBroadcastDispatcher;
	@Autowired
	protected InternalEventBroadcaster internalEventBroadcaster;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private TransactionTemplate readOnlyTransactionTemplate;

	final protected <T> T executeInTransaction(final boolean readOnly,
			final TransactionCallback<T> transactionCallback) {
		return getTransactionTemplate(readOnly).execute(transactionCallback);
	}

	/**
	 * @return the eventBroadcastDispatcher
	 */
	public EventBroadcaster getEventBroadcastDispatcher() {
		return eventBroadcastDispatcher;
	}

	/**
	 * @return the modelSpace
	 */
	public ModelSpace getModelSpace() {
		return modelSpace;
	}

	private TransactionTemplate getTransactionTemplate(final boolean readOnly) {
		return readOnly ? readOnlyTransactionTemplate : transactionTemplate;
	}

	/**
	 * This method must be only called in transaction.
	 * 
	 * @param event
	 */
	final protected void notifyListenersInTransaction(final AbstractEvent event) {
		eventBroadcastDispatcher.broadcast(Arrays.asList(event));
	}

	/**
	 * This method must be only called out of transaction.
	 * 
	 * @param event
	 */
	final protected void notifyListenersWithoutTransaction(
			final AbstractEvent event) {
		eventBroadcastDispatcher.broadcastWithoutTransaction(Arrays
				.asList(event));
	}

	/**
	 * @param eventBroadcastDispatcher
	 *            the eventBroadcastDispatcher to set
	 */
	public void setEventBroadcastDispatcher(
			final TransactionalEventBroadcaster eventBroadcastDispatcher) {
		this.eventBroadcastDispatcher = eventBroadcastDispatcher;
	}

	/**
	 * @param internalEventBroadcaster
	 *            the internalEventBroadcaster to set
	 */
	public void setInternalEventBroadcaster(
			final InternalEventBroadcaster internalEventBroadcaster) {
		this.internalEventBroadcaster = internalEventBroadcaster;
	}

	/**
	 * @param modelSpace
	 *            the modelSpace to set
	 */
	public void setModelSpace(final ModelSpace modelSpace) {
		this.modelSpace = modelSpace;
	}

	/**
	 * @param readOnlyTransactionTemplate
	 *            the readOnlyTransactionTemplate to set
	 */
	public void setReadOnlyTransactionTemplate(
			final TransactionTemplate readOnlyTransactionTemplate) {
		this.readOnlyTransactionTemplate = readOnlyTransactionTemplate;
	}

	/**
	 * @param transactionTemplate
	 *            the transactionTemplate to set
	 */
	public void setTransactionTemplate(
			final TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
}
