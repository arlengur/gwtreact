/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionLevels;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;

/**
 * @author sviyazov.a
 * 
 */
public interface InternalPolicyConfigurationService
		extends
			PolicyConfigurationService,
			Disabler<MPolicy>,
			Deleter<MPolicy> {

	/**
	 * Add new registered agent to set of registered policy managers on server
	 * 
	 * @param agentKey
	 */
	void addAgentToRegisteredPMInfo(String agentKey);

	/**
	 * Applies agent policy templates to the provided task and creates its
	 * policy templates.
	 * 
	 * @param agentKey
	 *            the key of the agent to apply its policy templates to the
	 *            provided task.
	 * @param task
	 *            the task to apply the agent policy templates to.
	 * 
	 */
	void applyAgentPolicyTemplates(String agentKey, MAgentTask task);

	/**
	 * Applies policy templates to the provided parameter of the provided task.
	 * 
	 * @param task
	 *            the task to apply its policy templates to the provided
	 *            parameter.
	 * @param parameterIdentifier
	 *            the parameter to apply the policy templates to.
	 */
	void applyParameterPolicyTemplates(MAgentTask task,
			ParameterIdentifier parameterIdentifier);

	/**
	 * Applies policy templates to the all parameters of the provided task.
	 * 
	 * @param task
	 *            the task to apply its policy templates to its all parameters.
	 */
	void applyTaskPolicyTemplates(MAgentTask task);

	/**
	 * Is used to correctly clean up legacy templates with null parameter type.
	 * Detaches only policies with parameter type that differs from template
	 * parameter type.
	 * 
	 * @param existingTemplate
	 */
	void detachDifferentTypePoliciesFromConditionsTemplate(
			MPolicyConditionsTemplate existingTemplate);

	/**
	 * Removes association between provided {@link MPolicyActionsTemplate} and
	 * all policies on which it was applied.
	 * 
	 * @param template
	 */
	void detachPoliciesFromActionsTemplate(MPolicyActionsTemplate template);

	/**
	 * Removes association between provided {@link MPolicyConitionsTemplate} and
	 * all policies on which it was applied.
	 * 
	 * @param template
	 */
	void detachPoliciesFromConditionsTemplate(MPolicyConditionsTemplate template);

	/**
	 * 
	 * @return
	 */
	List<MPolicy> getAllPolicies();

	/**
	 * @param actions
	 * @return
	 */
	Set<String> getDeletedRecipientKeys(
			Collection<MPolicyActionWithContacts> actions);

	/**
	 * 
	 * @param pmSupportedAgents
	 * @return
	 */
	Map<Source, PMConfiguration> getPMConfigurations(String policyManagerName);

	/**
	 * 
	 * @param source
	 * @return all policies for given source
	 */
	List<MPolicy> getPolicies(Source source);

	/**
	 * @param user
	 *            target {@link com.tecomgroup.qos.domain.MUser}
	 * @return all policies for given user
	 */
	List<MPolicy> getPoliciesByUser(MUser user);

	/**
	 * Gets {@link MPolicy} by its key.
	 * 
	 * @param policyKey
	 *            the key of the policy
	 * @param onlyActive
	 *            true means only active policies, otherwise all
	 * @return
	 */
	MPolicy getPolicy(String policyKey, boolean onlyActive);

	/**
	 * Gets the names of policy managers registered for provided key of the
	 * system component.
	 * 
	 * @param systemComponentKey
	 * @return
	 */
	Set<String> getRegisteredPolicyManagersBySystemComponent(
			String systemComponentKey);

	/**
	 * Gets policies for provided {@link MAgentTask} and
	 * {@link ParameterIdentifier}.
	 * 
	 * @param task
	 * @param parameterIdentifier
	 * @return
	 */
	List<MPolicy> getTaskParameterPolicies(MAgentTask task,
			ParameterIdentifier parameterIdentifier);

	/**
	 * For all {@link MPolicy} to which the provided template was applied
	 * before, clears and applies all template actions again.
	 * 
	 * @param template
	 */
	void reapplyPolicyActionsTemplate(MPolicyActionsTemplate template);

	/**
	 * For all {@link MPolicy} to which the provided template was applied
	 * before, updates {@link MPolicyConditionLevels} again.
	 * 
	 * @param template
	 * @return
	 */
	void reapplyPolicyConditionsTemplate(MPolicyConditionsTemplate template);

	/**
	 * 
	 * @param policy
	 */
	void registerPolicy(String agentName, MPolicy policy);

	/**
	 * 
	 * @param policyManagerName
	 * @param pmSupportedAgents
	 * 
	 * @return Set of registered agents
	 */
	Set<String> registerPolicyManager(String policyManagerName,
			Collection<String> pmSupportedAgents);

	/**
	 * Removes all {@link MPolicySendEmail} actions that have only provided
	 * contact. Cleans all dead associations.
	 * 
	 * @param contact
	 */
	void removeSendActionsWithContact(MContactInformation contact);

	/**
	 * 
	 * @param task
	 * @param parameterIdentifier
	 */
	void resetDisabledPolicies(MAgentTask task,
			ParameterIdentifier parameterIdentifier);

	/**
	 * Updates configuration of all policies, that have actions related to
	 * specified user. For each affected policy an
	 * {@link com.tecomgroup.qos.communication.message.UpdatePMConfiguration}
	 * event is sent to Policy Manager.
	 *
	 * @param user
	 *            target user
	 */
	void updatePolicyConfigurationsByUser(MUser user);

	/**
	 * Updates configuration of all policies, that have actions related to
	 * specified users. For each affected policy an
	 * {@link com.tecomgroup.qos.communication.message.UpdatePMConfiguration}
	 * event is sent to Policy Manager.
	 *
	 * @param user
	 *            target users
	 */
	public void updatePolicyConfigurationsByUsers(List<MUser> user);
}
