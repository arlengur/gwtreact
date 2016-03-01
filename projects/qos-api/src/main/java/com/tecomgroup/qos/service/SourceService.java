/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MSystemComponent;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.exception.QOSException;

/**
 * Service to manage only sources {@link Source}, {@link MSource} and there
 * descendants.
 * 
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/sourceService")
public interface SourceService extends Service, RemoteService {

	/**
	 * Gets domain source for provided source.
	 * 
	 * @param source
	 * @return domain source.
	 */
	<M extends MSource> M getDomainSource(Source source) throws QOSException;

	/**
	 * Gets {@link MSource} for given key.
	 * 
	 * @param key
	 * @return
	 */
	<M extends MSource> M getDomainSource(String sourceKey) throws QOSException;

	/**
	 * Gets real parent of the source.
	 * 
	 * @param source
	 * @return
	 */
	MSource getParent(MSource source) throws QOSException;

	/**
	 * Gets root {@link MSystemComponent} system component for provided source.
	 * 
	 * @param source
	 * @return
	 */
	MSystemComponent getSystemComponent(MSource source) throws QOSException;

	/**
	 * This method is an alias of
	 * {@link SourceService#getSystemComponent(MSource)} to support
	 * {@link Source}.
	 * 
	 * @param source
	 * @return
	 * @throws QOSException
	 */
	MSystemComponent getSystemComponent(Source source) throws QOSException;
}
