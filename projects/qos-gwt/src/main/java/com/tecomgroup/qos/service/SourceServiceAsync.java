/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MSystemComponent;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * @see SourceService
 */
public interface SourceServiceAsync {

	void getDomainSource(Source source, AsyncCallback<MSource> callback);

	void getDomainSource(String sourceKey, AsyncCallback<MSource> callback);

	void getParent(MSource source, AsyncCallback<MSource> callback);

	void getSystemComponent(MSource source,
			AsyncCallback<MSystemComponent> callback);

	void getSystemComponent(Source source,
			AsyncCallback<MSystemComponent> callback);
}
