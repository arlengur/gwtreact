/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.pm.MPolicy;

/**
 * @author ivlev.e
 * 
 */

public interface PolicyConfigurationServiceAsync {

	void applyPolicyActionsTemplate(String templateName,
			Set<String> policyKeys, AsyncCallback<Void> callback);

	void applyPolicyConditionsTemplate(String templateName,
			Set<String> policyKeys, AsyncCallback<Void> callback);

	void clearPolicyActionsTemplates(Set<String> policyKeys,
			AsyncCallback<Void> callback);

	void clearPolicyConditionsTemplates(Set<String> policyKeys,
			AsyncCallback<Void> callback);

	void deletePolicies(Set<String> policyKeys, AsyncCallback<Void> callback);

	void disablePolicies(Set<String> policyKeys, AsyncCallback<Void> callback);

	void getAgentPolicies(String agentKey, String searchString, Order order,
			Integer startPosition, Integer size,
			AsyncCallback<List<MPolicy>> callback);

	void getAgentPoliciesCount(String agentKey, String searchString,
			AsyncCallback<Long> callback);

	void getPolicies(Criterion criterion, Order order, Integer startPosition,
                     Integer size, String searchText, AsyncCallback<List<MPolicy>> callback);

	void getPoliciesCount(Criterion criterion, String searchText, AsyncCallback<Long> callback);

	void getPolicy(String policyKey, AsyncCallback<MPolicy> callback);

	void saveOrUpdatePolicy(MPolicy policy, AsyncCallback<MPolicy> callback);
}