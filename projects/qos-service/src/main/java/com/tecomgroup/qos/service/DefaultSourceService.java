/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MAgentTask;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MSystemComponent;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.exception.QOSException;
import com.tecomgroup.qos.exception.SourceNotFoundException;
import com.tecomgroup.qos.exception.UnknownSourceException;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class DefaultSourceService extends AbstractService
		implements
		InternalSourceService {

	@Override
	public <M extends MSource> M getDomainSource(final Source source)
			throws QOSException {
		M result = null;
		if (validateSource(source)) {
			result = getDomainSource(source.getKey());
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends MSource> M getDomainSource(final String sourceKey)
			throws QOSException {
		final M result = (M) executeInTransaction(true,
				new TransactionCallback<MSource>() {
					@Override
					public MSource doInTransaction(
							final TransactionStatus status) {
						return modelSpace.findUniqueEntity(
								MSource.class,
								modelSpace.createCriterionQuery().eq("key",
										sourceKey));
					}
				});
		return result;
	}

	@Override
	public MSource getParent(final MSource source) throws QOSException {
		final MSource parent;
		if (source instanceof MPolicy) {
			final MPolicy policy = ((MPolicy) source);
			parent = getDomainSource(policy.getSource());
			if (parent == null) {
				throw new SourceNotFoundException("Source "
						+ policy.getSource() + " not found for policy: "
						+ source);
			}
		} else {
			parent = source.getParent();
		}
		return parent;
	}

	@Override
	public MSystemComponent getSystemComponent(final MSource source)
			throws QOSException {
		final MSystemComponent root;
		if (source instanceof MPolicy) {
			final MSource policyParent = getParent(source);
			root = getSystemComponent(policyParent);
		} else {
			root = SimpleUtils.findSystemComponent(source);
		}
		return root;
	}

	@Override
	public MSystemComponent getSystemComponent(final Source source)
			throws QOSException {
		return getSystemComponent(getDomainSource(source));
	}

	private boolean validateSource(final Source source) {
		final boolean result = true;
		switch (source.getType()) {
			case AGENT :
				break;
			case MODULE :
				break;
			case TASK :
				break;
			case POLICY :
				break;
			case POLICY_MANAGER :
				break;
			case SERVER :
				break;
			case STREAM :
				break;

			default :
				throw new UnknownSourceException("Usupported source type "
						+ source.getType());
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <M extends MSource> M getDomainSource(final Class<M> clazz, final String sourceKey)
			throws QOSException {
		final M result = (M) executeInTransaction(true,
				new TransactionCallback<MSource>() {
					@Override
					public MSource doInTransaction(
							final TransactionStatus status) {
						return modelSpace.findUniqueEntity(
								clazz,
								modelSpace.createCriterionQuery().eq("key",
										sourceKey));
					}
				});
		return result;
	}
}
