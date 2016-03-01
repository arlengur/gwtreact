/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.tecomgroup.qos.event.TransactionalEventBroadcaster;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
public class QoSTransactionalManager extends HibernateTransactionManager {

	@Autowired
	private TransactionalEventBroadcaster eventBroadcaster;

	@Override
	protected void doCommit(final DefaultTransactionStatus status) {
		final Transaction transaction = getSessionFactory().getCurrentSession()
				.getTransaction();
		try {
			super.doCommit(status);
			if (transaction != null) {
				eventBroadcaster.commit(transaction);
			}
		} catch (final RuntimeException e) {
			if (transaction != null) {
				eventBroadcaster.rollback(transaction);
			}
			throw e;
		}

	}

	@Override
	protected void doRollback(final DefaultTransactionStatus status) {
		final Transaction transaction = getSessionFactory().getCurrentSession()
				.getTransaction();
		try {
			super.doRollback(status);
		} finally {
			if (transaction != null) {
				eventBroadcaster.rollback(transaction);
			}
		}
	}
}
