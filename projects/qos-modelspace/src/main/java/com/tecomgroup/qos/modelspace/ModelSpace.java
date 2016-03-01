/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace;

import java.util.List;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.projection.Projection;

/**
 * Interface to deal with model objects
 * 
 * @see MAbstractEntity
 * @author abondin
 * 
 */
public interface ModelSpace {
	/**
	 * 
	 * @param type
	 * @param criterion
	 * @return
	 * @throws ModelSpaceException
	 */
	<T extends MAbstractEntity> long count(Class<T> type, Criterion criterion)
			throws ModelSpaceException;

	CriterionQuery createCriterionQuery();

	/**
	 * Delete entity
	 * 
	 * @param entity
	 * @throws ModelSpaceException
	 */
	void delete(MAbstractEntity entity) throws ModelSpaceException;

	/**
	 * Detaches entity from session
	 * 
	 * @param entity
	 * @throws ModelSpaceException
	 */
	void evict(MAbstractEntity entity) throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param criterion
	 * @return
	 * @throws ModelSpaceException
	 */
	<T extends MAbstractEntity> List<T> find(Class<T> type, Criterion criterion)
			throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param criterion
	 * @param order
	 * @param startPosition
	 * @param size
	 * @return
	 * @throws ModelSpaceException
	 */
	<T extends MAbstractEntity> List<T> find(Class<T> type,
			Criterion criterion, Order order, Integer startPosition,
			Integer size) throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param criterion
	 * @param order
	 * @param projection
	 * @param properties
	 * @return if properties contains only one property: List of values of given
	 *         property
	 * 
	 *         if properties contains more that one property: List of Object[].
	 *         For example properties = {name, id} => List{["Name1", 1],
	 *         ["Name2", 2]}
	 * 
	 *         if properties is null or is empty returns the whole object
	 * @throws ModelSpaceException
	 * 
	 */
	List<?> findProperties(Class<? extends MAbstractEntity> type,
			Criterion criterion, Order order, Projection projection,
			String... properties) throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param criterion
	 * @param projection
	 * @param properties
	 *            - properties to fetch
	 * @return if properties contains only one property: List of values of given
	 *         property
	 * 
	 *         if properties contains more that one property: List of Object[].
	 *         For example properties = {name, id} => List{["Name1", 1],
	 *         ["Name2", 2]}
	 * 
	 *         if properties is null or is empty returns the whole object
	 * @throws ModelSpaceException
	 */
	List<?> findProperties(Class<? extends MAbstractEntity> type,
			Criterion criterion, Projection projection, String... properties)
			throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param criterion
	 * @param properties
	 *            - properties to fetch
	 * @return if properties contains only one property: List of values of given
	 *         property
	 * 
	 *         if properties contains more that one property: List of Object[].
	 *         For example properties = {name, id} => List{["Name1", 1],
	 *         ["Name2", 2]}
	 * 
	 *         if properties is null or is empty returns the whole object
	 * @throws ModelSpaceException
	 */
	List<?> findProperties(Class<? extends MAbstractEntity> type,
			Criterion criterion, String... properties)
			throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param criterion
	 * @return
	 * @throws ModelSpaceException
	 */
	<T extends MAbstractEntity> T findUniqueEntity(Class<T> type,
			Criterion criterion) throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @param id
	 * @return
	 * @throws ModelSpaceException
	 */
	<T extends MAbstractEntity> T get(Class<T> type, Long id)
			throws ModelSpaceException;

	/**
	 * 
	 * @param type
	 * @return
	 * @throws ModelSpaceException
	 */
	<T extends MAbstractEntity> List<T> getAll(Class<T> type)
			throws ModelSpaceException;

	/**
	 * Save entity
	 * 
	 * @param entity
	 * @return id of saved instance
	 * @throws ModelSpaceException
	 */
	Long save(MAbstractEntity entity) throws ModelSpaceException;

	/**
	 * Save or update entity
	 * 
	 * @param entity
	 * @throws ModelSpaceException
	 */
	void saveOrUpdate(MAbstractEntity entity) throws ModelSpaceException;

	/**
	 * Update entity
	 * 
	 * @param entity
	 * @throws ModelSpaceException
	 */
	void update(MAbstractEntity entity) throws ModelSpaceException;

	/**
	 * Create query
	 *
	 * @param query
	 * @throws ModelSpaceException
	 */
	List createQuery(final String query) throws ModelSpaceException;

	/*
	 * Flush dirty data to DB
	 */
	boolean flush() throws ModelSpaceException;
}
