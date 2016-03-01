/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.*;

import com.tecomgroup.qos.util.AuditLogger;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;

import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyComponentTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionLevels;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.exception.DeletedContactInformationException;
import com.tecomgroup.qos.exception.QOSException;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;
import com.tecomgroup.qos.util.PolicyUtils;

/**
 * 
 * @author novohatskiy.r
 * 
 */
@Service("policyComponentTemplateService")
public class DefaultPolicyComponentTemplateService extends AbstractService
		implements
			PolicyComponentTemplateService {

	@Autowired
	private InternalPolicyConfigurationService policyConfigurationService;

	@Override
	@Transactional(readOnly = true)
	public boolean doesTemplateExist(final String name,
			final String templateClass) throws QOSException {
		return getTemplateByName(name, templateClass) != null;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<MPolicyActionsTemplate> getAllActionsTemplates() {
		return getAllTemplates(MPolicyActionsTemplate.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<MPolicyConditionsTemplate> getAllConditionsTemplates() {
		return getAllTemplates(MPolicyConditionsTemplate.class);
	}

	private <M extends MPolicyComponentTemplate> Collection<M> getAllTemplates(
			final Class<M> type) {
		return modelSpace.getAll(type);
	}

	@Override
	@Transactional
	public Collection<MPolicyConditionsTemplate> getConditionsTemplates(
			final ParameterType parameterType) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		return modelSpace.find(MPolicyConditionsTemplate.class,
				query.eq("parameterType", parameterType));
	}

	@Override
	@Transactional(readOnly = true)
	public MPolicyComponentTemplate getTemplateByName(final String name,
			final String templateClass) throws QOSException {
		return modelSpace.findUniqueEntity(getTemplateClass(templateClass),
				modelSpace.createCriterionQuery().eq("name", name));
	}

	private Class<? extends MPolicyComponentTemplate> getTemplateClass(
			final String templateClassName) {
		if (MPolicyActionsTemplate.class.getName().equals(templateClassName)) {
			return MPolicyActionsTemplate.class;
		} else if (MPolicyConditionsTemplate.class.getName().equals(
				templateClassName)) {
			return MPolicyConditionsTemplate.class;
		} else {
			throw new RuntimeException(
					"No policy template class found for class name "
							+ templateClassName);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTemplate(final String name, final String templateClass)
			throws QOSException {
		final MPolicyComponentTemplate foundTemplate = getTemplateByName(name,
				templateClass);

		if (foundTemplate != null) {
			if (foundTemplate instanceof MPolicyActionsTemplate) {
				policyConfigurationService
						.detachPoliciesFromActionsTemplate((MPolicyActionsTemplate) foundTemplate);
			} else if (foundTemplate instanceof MPolicyConditionsTemplate) {
				policyConfigurationService
						.detachPoliciesFromConditionsTemplate((MPolicyConditionsTemplate) foundTemplate);
			}
			modelSpace.delete(foundTemplate);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeTemplates(final Collection<String> names,
			final String templateClass) throws QOSException {
		try {
			for (final String templateName : names) {
				removeTemplate(templateName, templateClass);
			}
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY,AuditLogger.SyslogActionStatus.OK,"Action templates removed : {}",
					Arrays.toString(names.toArray(new String[names.size()])));
		}catch (Exception e){
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY,AuditLogger.SyslogActionStatus.NOK,"Unable to remove templates : {}, reason : {}",
					Arrays.toString(names.toArray(new String[names.size()])),e.getMessage());
			throw e;
		}
	}

	@Override
	public <M extends MPolicyComponentTemplate> M saveOrUpdateTemplate(
			final M template, final boolean reapplyToPolicies)
			throws QOSException {
		try {
			boolean isNewTemplate=template.getId()==null?true:false;
			if (template instanceof MPolicyActionsTemplate) {
				final Set<String> deletedContacts = policyConfigurationService
						.getDeletedRecipientKeys(((MPolicyActionsTemplate) template)
								.getActions());
				if (deletedContacts.size() > 0) {
					throw new DeletedContactInformationException(deletedContacts);
				}
			}

			final M resultTemplate = executeInTransaction(false,
					new TransactionCallback<M>() {

						@Override
						public M doInTransaction(final TransactionStatus status) {
							M resultTemplate = template;
							if (template.getId() != null) {
								resultTemplate = updateTemplate(template,
										reapplyToPolicies);
							}
							modelSpace.saveOrUpdate(resultTemplate);
							return resultTemplate;
						}
					});

			HibernateEntityConverter.convertHibernateCollections(resultTemplate,
					PersistentCollection.class);
			if(isNewTemplate){
				AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK, "Create policy template  : {}",
						resultTemplate.getName());
			}else{
				AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.OK, "Update policy template  : {}",
						resultTemplate.getName());
			}

			return resultTemplate;
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.POLICY, AuditLogger.SyslogActionStatus.NOK, "Unable to update policy template  : {}, reason : {}",
					template.getName(), e.getMessage());
			throw e;
		}
	}

	private MPolicyActionsTemplate updatePolicyActionsTemplate(
			final MPolicyActionsTemplate template,
			final boolean reapplyToPolicies) {

		final MPolicyActionsTemplate existingTemplate = modelSpace.get(
				MPolicyActionsTemplate.class, template.getId());

		if (existingTemplate != null) {
			existingTemplate.updateSimpleFields(template);

			final List<MPolicyActionWithContacts> oldTemplateActions = existingTemplate
					.getActions();
			final List<MPolicyActionWithContacts> newTemplateActions = template
					.getActions();

			if (!PolicyUtils.arePolicyActionsEqual(oldTemplateActions,
					newTemplateActions)) {

				if (reapplyToPolicies) {
					policyConfigurationService
							.reapplyPolicyActionsTemplate(template);
				} else {
					policyConfigurationService
							.detachPoliciesFromActionsTemplate(existingTemplate);
				}
			}

			final Collection<MPolicyActionWithContacts> actionsToAdd = new ArrayList<>();
			final Collection<MPolicyActionWithContacts> actionsToRemove = new ArrayList<>();

			final Map<Long, MPolicyActionWithContacts> newActions = new HashMap<>();
			for (final MPolicyActionWithContacts action : newTemplateActions) {
				if (action.getId() == null) {
					actionsToAdd.add(action);
				} else {
					newActions.put(action.getId(), action);
				}
			}

			for (final MPolicyActionWithContacts existingAction : existingTemplate
					.getActions()) {
				if (!newActions.containsKey(existingAction.getId())) {
					actionsToRemove.add(existingAction);
				} else {
					final MPolicyActionWithContacts newAction = newActions
							.get(existingAction.getId());
					// actions of the same types
					if (newAction.getClass() == existingAction.getClass()) {

						existingAction.updateSimpleFields(newAction);
						existingAction.setContacts(newAction.getContacts());
						modelSpace.saveOrUpdate(existingAction);

					} else {
						// different types of actions
						// remove existing one and add new one
						actionsToRemove.add(existingAction);
						actionsToAdd.add(newAction.copy());
					}
				}
			}
			// add new actions
			existingTemplate.getActions().addAll(actionsToAdd);
			// remove actions
			existingTemplate.getActions().removeAll(actionsToRemove);
			for (final MPolicyAction actionToRemove : actionsToRemove) {
				modelSpace.delete(actionToRemove);
			}
		}

		return existingTemplate;
	}

	private MPolicyConditionsTemplate updatePolicyConditionsTemplate(
			final MPolicyConditionsTemplate template,
			final boolean reapplyToPolicies) {
		final MPolicyConditionsTemplate existingTemplate = modelSpace.get(
				MPolicyConditionsTemplate.class, template.getId());

		if (existingTemplate != null) {
			existingTemplate.updateSimpleFields(template);

			final MPolicyConditionLevels existingConditionLevels = existingTemplate
					.getConditionLevels();
			final MPolicyConditionLevels newConditionLevels = template
					.getConditionLevels();

			final boolean areConditionsEqual = PolicyUtils
					.arePolicyConditionLevelsEqual(existingConditionLevels,
							newConditionLevels);
			final boolean areTypesEqual = template.getParameterType() == existingTemplate
					.getParameterType();
			if (!areTypesEqual || !areConditionsEqual) {

				if (!areConditionsEqual) {
					existingConditionLevels
							.updateSimpleFields(newConditionLevels);
				}

				if (!areTypesEqual) {
					existingTemplate.setParameterType(template
							.getParameterType());
				}

				if (areTypesEqual && reapplyToPolicies) {
					policyConfigurationService
							.reapplyPolicyConditionsTemplate(template);
				} else {
					if (!areTypesEqual) {
						policyConfigurationService
								.detachDifferentTypePoliciesFromConditionsTemplate(existingTemplate);
					} else {
						policyConfigurationService
								.detachPoliciesFromConditionsTemplate(existingTemplate);
					}
				}
			}
		}

		return existingTemplate;
	}

	@SuppressWarnings("unchecked")
	private <M extends MPolicyComponentTemplate> M updateTemplate(
			final M template, final boolean reapplyToPolicies) {
		M resultTemplate = template;
		if (template instanceof MPolicyActionsTemplate) {
			resultTemplate = (M) updatePolicyActionsTemplate(
					(MPolicyActionsTemplate) resultTemplate, reapplyToPolicies);
		} else if (template instanceof MPolicyConditionsTemplate) {
			resultTemplate = (M) updatePolicyConditionsTemplate(
					(MPolicyConditionsTemplate) resultTemplate,
					reapplyToPolicies);
		} else if (template != null) {
			throw new UnsupportedOperationException(
					"Unsupported template type: "
							+ template.getClass().getName());
		}

		return resultTemplate;
	}
}
