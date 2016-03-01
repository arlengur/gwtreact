/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.event;

import java.util.Collection;

/**
 * 
 * Отправляет события в рамках комита транзакции
 * 
 * @author abondin
 * 
 */
public interface TransactionalEventBroadcaster extends EventBroadcaster {
	/**
	 * 
	 */
	void broadcastWithoutTransaction(Collection<? extends AbstractEvent> events);
	/**
	 * 
	 * @param transaction
	 */
	void commit(Object transaction);

	/**
	 * 
	 * @param transaction
	 */
	void rollback(Object transaction);
}
