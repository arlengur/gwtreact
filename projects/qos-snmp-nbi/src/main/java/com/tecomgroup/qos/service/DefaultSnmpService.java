/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.Status;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.projection.ProjectionFactory;
import com.tecomgroup.qos.snmp.util.ResultWrapper;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author novohatskiy.r
 * 
 */
@Lazy
@Service("snmpService")
@Transactional(readOnly = true)
public class DefaultSnmpService extends AbstractService implements SnmpService {

	private static final String ID = "id";

	private static final String SNMP_ID = "snmpId";

	private Criterion alertActiveCriterion;

	private Criterion notDisabledCriterion;

	private Criterion notDeletedCriterion;

	private Criterion notDisabledAndNotDeletedCriterion;

	@Autowired
	private AlertService alertService;

	@Autowired
	private ResultService resultService;

	@Override
	public boolean doesEntPhysicalContainsRowExist(
			final Pair<Integer, Integer> index) {
		boolean result = false;
		final MSource childEntity = getSourceBySnmpId(index.getRight());
		if (childEntity != null && isOfAcceptedType(childEntity)) {
			final MSource parentEntity = getSourceBySnmpId(index.getLeft());
			if (parentEntity != null && isOfAcceptedType(parentEntity)) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public MAlert getAlertById(final long id) {
		return modelSpace.findUniqueEntity(MAlert.class, SimpleUtils
				.mergeCriterions(alertActiveCriterion, modelSpace
						.createCriterionQuery().eq(ID, id)));
	}

	@Override
	public int getAlertRowCount() {
		return alertService.getAlertsCount(alertActiveCriterion).intValue();
	}

	@Override
	public ResultWrapper getCurrentResult(final int taskSnmpId,
			final int paramSnmpId) {
		final MAgentTask task = getSourceBySnmpId(MAgentTask.class, taskSnmpId,
				notDisabledAndNotDeletedCriterion);
		final MResultParameterConfiguration resultParameterConfiguration = modelSpace
				.find(MResultParameterConfiguration.class,
						modelSpace.createCriterionQuery().eq("snmpId",
								paramSnmpId)).get(0);
		final String taskKey = task.getKey();
		final ParameterIdentifier parameterIdentifier = resultParameterConfiguration
				.getParameterIdentifier();

		final Map<String, Collection<?>> taskParameters = new HashMap<>();
		final List<ParameterIdentifier> parameterIdentifiers = new ArrayList<>();
		parameterIdentifiers.add(parameterIdentifier);
		taskParameters.put(taskKey, parameterIdentifiers);

		final List<Map<String, Object>> lastResults = resultService
				.getLastResults(taskParameters, 0l, 1l, OrderType.DESC);

		Object value = null;
		Date dateTime = null;

		if (!lastResults.isEmpty()) {
			final Map<String, Object> results = lastResults.get(0);
			if (!results.isEmpty()) {
				value = results.get(parameterIdentifier
						.createTaskStorageKey(taskKey));
				dateTime = (Date) results.get(SimpleUtils.DATE_PARAMETER_NAME);
			}
		}

		return new ResultWrapper(taskSnmpId, resultParameterConfiguration,
				value, dateTime);
	}

	@Override
	public int getCurrentResultRowCount() {
		int result = 0;
		for (final MAgentTask task : modelSpace.find(MAgentTask.class,
				notDisabledAndNotDeletedCriterion)) {
			result += task.getResultConfiguration()
					.getParameterConfigurations().size();
		}
		return result;
	}

	@Override
	public int getEntPhysicalContainsRowCount() {
		final Criterion criterion = modelSpace.createCriterionQuery()
				.isNotNull("parent");
		long count = modelSpace.count(MAgentTask.class, criterion);
		count += modelSpace.count(MAgentModule.class, criterion);
		return (int) count;
	}

	@Override
	public int getEntPhysicalRowCount() {
		return (int) modelSpace.count(MSource.class, null);
	}

	@Override
	public Date getLastAlertModificationTimestamp() {
		Date result = null;
		@SuppressWarnings("unchecked")
		final List<Date> queryResult = (List<Date>) modelSpace.findProperties(
				MAlert.class, alertActiveCriterion,
				ProjectionFactory.max("lastUpdateDateTime"));
		if (!queryResult.isEmpty()) {
			result = queryResult.get(0);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Date getLastEntityModificationTimestamp() {
		Date result = null;
		final List<Date> queryResult = (List<Date>) modelSpace.findProperties(
				MSource.class, null,
				ProjectionFactory.max("modificationDateTime"));
		if (!queryResult.isEmpty()) {
			result = queryResult.get(0);
		}
		return result;
	}

	private MSource getNextAcceptedEntity(final int snmpId) {
		int currentSnmpId = snmpId;
		MSource result = null;
		MSource nextEntity = null;
		do {
			nextEntity = getNextEntityBySnmpId(MSource.class, currentSnmpId,
					null);
			if (nextEntity != null && isOfAcceptedType(nextEntity)) {
				result = nextEntity;
				break;
			} else {
				currentSnmpId++;
			}
		} while (nextEntity != null);

		return result;
	}

	@Override
	public int getNextAlertIndex(final long index) {
		return getNextId(MAlert.class, index, alertActiveCriterion);
	}

	@Override
	public Pair<Integer, Integer> getNextCurrentResultIndex(
			final Pair<Integer, Integer> index) {
		Pair<Integer, Integer> result = null;
		final int taskSnmpId = index.getLeft();
		int paramSnmpId = index.getRight();

		MAgentTask task = getSourceBySnmpId(MAgentTask.class, taskSnmpId,
				notDisabledAndNotDeletedCriterion);
		if (task == null) {
			task = getNextEntityBySnmpId(MAgentTask.class, taskSnmpId,
					notDisabledAndNotDeletedCriterion);
		}

		while (task != null) {
			final List<MResultParameterConfiguration> parameterConfigurations = task
					.getResultConfiguration().getParameterConfigurations();

			Integer minParamSnmpId = Integer.MAX_VALUE;

			for (final MResultParameterConfiguration paramConfig : parameterConfigurations) {
				final int currParamSnmpId = paramConfig.getSnmpId();
				if (currParamSnmpId > paramSnmpId
						&& currParamSnmpId < minParamSnmpId) {
					minParamSnmpId = currParamSnmpId;
				}
			}

			if (!minParamSnmpId.equals(Integer.MAX_VALUE)) {
				result = new ImmutablePair<>(task.getSnmpId(), minParamSnmpId);
				break;
			}

			task = getNextEntityBySnmpId(MAgentTask.class, task.getSnmpId(),
					notDisabledAndNotDeletedCriterion);
			paramSnmpId = Integer.MIN_VALUE;
		}

		return result;
	}

	private <T extends MAbstractEntity> T getNextEntity(final Class<T> type,
			final String idPropertyName, final Number currentId,
			final Criterion criterion) {
		T result = null;
		final List<T> nextEntities = modelSpace.find(type, SimpleUtils
				.mergeCriterions(
						modelSpace.createCriterionQuery().ge(idPropertyName,
								currentId), criterion), Order
				.asc(idPropertyName), 0, 1);
		if (!nextEntities.isEmpty()) {
			result = nextEntities.get(0);
		}
		return result;
	}

	private <T extends MAbstractEntity> T getNextEntityById(
			final Class<T> type, final long currentId, final Criterion criterion) {
		return getNextEntity(type, ID, currentId, criterion);
	}

	private <T extends MSource> T getNextEntityBySnmpId(final Class<T> type,
			final int currentId, final Criterion criterion) {
		return getNextEntity(type, SNMP_ID, currentId, criterion);
	}

	@Override
	public Pair<Integer, Integer> getNextEntPhysicalContainsIndex(
			final Pair<Integer, Integer> index) {
		Pair<Integer, Integer> result = null;

		final int parentIndex = index.getLeft();
		int childIndex = index.getRight();
		MSource parentEntity = getSourceBySnmpId(parentIndex);
		if (parentEntity == null) {
			parentEntity = getNextAcceptedEntity(parentIndex);
		}

		while (parentEntity != null) {
			final int nextId = getNextSnmpId(
					MSource.class,
					childIndex,
					modelSpace.createCriterionQuery().eq("parent.snmpId",
							parentEntity.getSnmpId()));

			if (nextId > -1) {
				result = new ImmutablePair<>(parentEntity.getSnmpId(), nextId);
				break;
			}

			parentEntity = getNextAcceptedEntity(parentEntity.getSnmpId());
			childIndex = 0;
		}

		return result;
	}

	@Override
	public int getNextEntPhysicalIndex(final int index) {
		final MSource nextEntity = getNextAcceptedEntity(index);
		return nextEntity != null ? nextEntity.getSnmpId() : -1;
	}

	private <T extends MAbstractEntity> int getNextId(final Class<T> type,
			final long currentId, final Criterion criterion) {
		final T nextEntity = getNextEntityById(type, currentId, criterion);
		return nextEntity != null ? nextEntity.getId().intValue() : -1;
	}

	private <T extends MSource> int getNextSnmpId(final Class<T> type,
			final int currentId, final Criterion criterion) {
		final T nextEntity = getNextEntityBySnmpId(type, currentId, criterion);
		return nextEntity != null ? nextEntity.getSnmpId() : -1;
	}

	@Override
	public Integer getNextTopLevelEntityIndex(final Integer current) {
		Integer result = null;
		final MAgent next = getNextEntityBySnmpId(MAgent.class, current,
				notDeletedCriterion);
		if (next != null) {
			result = next.getSnmpId();
		}
		return result;
	}

	private <T extends MSource> T getSourceBySnmpId(final Class<T> type,
			final int snmpId, final Criterion criterion) {
		return modelSpace.findUniqueEntity(type, SimpleUtils.mergeCriterions(
				modelSpace.createCriterionQuery().eq(SNMP_ID, snmpId),
				criterion));
	}

	@Override
	public MSource getSourceBySnmpId(final int snmpId) {
		return getSourceBySnmpId(MSource.class, snmpId, null);
	}

	@Override
	public int getTopLevelEntityCount() {
		return (int) modelSpace.count(MAgent.class, notDeletedCriterion);
	}

	@PostConstruct
	private void initializeCriterions() {
		notDisabledCriterion = modelSpace.createCriterionQuery().eq("disabled",
				false);
		notDeletedCriterion = modelSpace.createCriterionQuery().eq("deleted",
				false);
		notDisabledAndNotDeletedCriterion = SimpleUtils.mergeCriterions(
				notDisabledCriterion, notDeletedCriterion);
		alertActiveCriterion = SimpleUtils.mergeCriterions(
				notDisabledCriterion,
				modelSpace.createCriterionQuery().eq("status", Status.ACTIVE));
	}

	private boolean isOfAcceptedType(final MSource entity) {
		return entity instanceof MAgent || entity instanceof MAgentModule
				|| entity instanceof MAgentTask;
	}

}