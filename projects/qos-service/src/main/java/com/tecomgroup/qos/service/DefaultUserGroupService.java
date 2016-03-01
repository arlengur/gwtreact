/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.tecomgroup.qos.util.AuditLogger;
import com.tecomgroup.qos.util.AuditLogger.SyslogActionStatus;
import com.tecomgroup.qos.util.AuditLogger.SyslogCategory;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.exception.QOSException;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;

/**
 * @author kunilov.p
 *
 */
@Service("userGroupService")
public class DefaultUserGroupService extends AbstractService
		implements
			UserGroupService {

	@Autowired
	private InternalPolicyConfigurationService policyConfigurationService;

	@Override
	@Transactional(readOnly = true)
	public boolean doesGroupExist(final String name) throws QOSException {
		return getGroupByName(name) != null;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<MUserGroup> getAllGroups() {
		return modelSpace.getAll(MUserGroup.class);
	}

	@Override
	@Transactional(readOnly = true)
	public MUserGroup getGroupByName(final String name) throws QOSException {
		return modelSpace.findUniqueEntity(MUserGroup.class, modelSpace
				.createCriterionQuery().eq("name", name));
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<MUserGroup> getGroups(final Criterion criterion) {
		return modelSpace.find(MUserGroup.class, criterion);
	}

    private void removeGroup(final MUserGroup group) {
        policyConfigurationService.removeSendActionsWithContact(group);
        modelSpace.delete(group);
    }

	@Override
	@Transactional(readOnly = false)
	public void removeGroup(final String name) {
		final MUserGroup foundGroup = getGroupByName(name);
		if (foundGroup != null) {
			removeGroup(foundGroup);
			AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, "Users group {} removed", name);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void removeGroups(final Collection<String> names) {
		final List<MUserGroup> foundGroups = modelSpace.find(MUserGroup.class,
				modelSpace.createCriterionQuery().in("name", names));
		if (foundGroups != null) {
			for (final MUserGroup group : foundGroups) {
				removeGroup(group);
			}
		}
		AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, "Users groups {} removed", Arrays.toString(names.toArray(new String[names.size()])));
	}

	@Override
	public MUserGroup saveOrUpdateGroup(final MUserGroup group) {
		boolean isNewGroup=group.getId()==null?true:false;
		final MUserGroup savedOrUpdatedGroup = executeInTransaction(false,
				new TransactionCallback<MUserGroup>() {

					@Override
					public MUserGroup doInTransaction(
							final TransactionStatus status) {
						modelSpace.saveOrUpdate(group);
						return group;
					}
				});
		HibernateEntityConverter.convertHibernateCollections(
				savedOrUpdatedGroup, PersistentCollection.class);
		if(isNewGroup)
		{
			AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, " User group {} created", group.getName());
		}else{
			AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, " User group {} updated", group.getName());
		}
		return savedOrUpdatedGroup;
	}
}
