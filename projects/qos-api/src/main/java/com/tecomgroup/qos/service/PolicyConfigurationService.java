/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.domain.pm.MPolicyTemplate;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.PolicyEvent;
import com.tecomgroup.qos.exception.CompatibilityException;
import com.tecomgroup.qos.exception.QOSException;

/**
 * @author abondin
 * 
 */
@RemoteServiceRelativePath("springServices/policyConfigurationService")
public interface PolicyConfigurationService extends Service, RemoteService {
	/**
	 * Clears all {@link MPolicyAction}s of given policies except
	 * {@link MPolicySendAlert} and applies {@link MPolicyActionsTemplate} with
	 * provided name to these policies.
	 * 
	 * @param templateName
	 * @param policyKeys
	 */
	void applyPolicyActionsTemplate(String templateName, Set<String> policyKeys);

	/**
	 * @param templateName
	 * @param policyKeys
	 * @throws CompatibilityException
	 *             in case of some policies have incompatible parameters,
	 * 
	 *             SourceNotFoundException in case of some sources of provided
	 *             policies is not found.
	 */
	void applyPolicyConditionsTemplate(String templateName,
			Set<String> policyKeys) throws QOSException;

	/**
	 * Clears all {@link MPolicyAction}s of given policies except
	 * {@link MSendAlertAction} and clears all links between provided policies
	 * and its {@link MPolicyActionsTemplate}s.
	 * 
	 * @param policyKeys
	 */
	void clearPolicyActionsTemplates(Set<String> policyKeys);

	/**
	 * Clears all links between provided policies and its
	 * {@link MPolicyTemplate}s.
	 * 
	 * @param policyKeys
	 */
	void clearPolicyConditionsTemplates(Set<String> policyKeys);

	/**
	 * Deletes policies by provided keys and sends event {@link PolicyEvent}
	 * with {@link EventType#DELETE}.
	 * 
	 * @param policyKeys
	 * @exception QOSException
	 */
	void deletePolicies(Set<String> policyKeys) throws QOSException;

	/**
	 * Disables policies by provided keys and sends event {@link PolicyEvent}
	 * with {@link EventType#DELETE}.
	 * 
	 * @param policyKeys
	 * @exception QOSException
	 */
	void disablePolicies(Set<String> policyKeys) throws QOSException;

	/**
	 * Finds policies attached to agent and all agent's tasks
	 * 
	 */
	List<MPolicy> getAgentPolicies(final String agentKey, String searchString,
			Order order, Integer startPosition, Integer size);

	/**
	 * Get count of policies according to an agent or tasks
	 * 
	 */
	Long getAgentPoliciesCount(final String agentKey, String searchString);

	/**
	 * @param criterion
	 * @param order
	 * @param startPosition
	 * @param size
	 * @param searchText
     * @return
	 */
	List<MPolicy> getPolicies(Criterion criterion, Order order,
                              Integer startPosition, Integer size, String searchText);

	/**
	 * @param criterion
	 * @param searchText
     * @return
	 */
	Long getPoliciesCount(Criterion criterion, String searchText);

	/**
	 * Returns only active policies
	 * 
	 * @param policyKey
	 * @return
	 */
	MPolicy getPolicy(String policyKey);

	/**
	 * 
	 * @param policy
	 * @return saved policy
	 * @exception QOSException
	 */
	MPolicy saveOrUpdatePolicy(MPolicy policy) throws QOSException;

}