/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.modelspace.hibernate.HibernateCriterionConverter.QueryWrapper;
import com.tecomgroup.qos.projection.Projection;
import com.tecomgroup.qos.projection.ProjectionFactory;

/**
 * 
 * Hibernate implementation of QoS modelspace
 * 
 * @author abondin
 * 
 */
@Component
public class HibernateModelspace implements ModelSpace {
	@Autowired
	private SessionFactory sessionFactory;

	private final static Logger LOGGER = Logger
			.getLogger(HibernateModelspace.class);

	@Override
	public <T extends MAbstractEntity> long count(final Class<T> type,
			final Criterion criterion) throws ModelSpaceException {
		final Query query = getQuery(type, criterion, null,
				ProjectionFactory.rowCount());
		return (Long) query.uniqueResult();
	}

	@Override
	public CriterionQuery createCriterionQuery() {
		return CriterionQueryFactory.getQuery();
	}

	private QueryWrapper createQueryWrapper(
			final Class<? extends MAbstractEntity> type,
			final Criterion criterion, final Order order,
			final Projection projection, final String... properties) {
		final StringBuilder query = new StringBuilder();

		final String rootEntityAlias = StringUtils.uncapitalize(type
				.getSimpleName().substring(1));
		if (projection != null) {
			query.append("select ")
					.append(HibernateProjectionConverter.getConverter()
							.toNativeProjection(rootEntityAlias, projection))
					.append(" ");

		} else if (properties != null && properties.length != 0) {
			query.append("select ")
					.append(StringUtils.collectionToDelimitedString(
							Arrays.asList(properties), ",", rootEntityAlias
									+ ".", "")).append(" ");
		}

		final String entityName = HibernateEntityConverter
				.getEntityAnnotationName(type);
		query.append("from " + entityName + " " + rootEntityAlias);

		QueryWrapper nativeCriteria = null;
		if (criterion != null) {
			nativeCriteria = (QueryWrapper) HibernateCriterionConverter
					.getConverter().toNativeCriterion(rootEntityAlias,
							criterion);
			query.append(" where ");
			query.append(nativeCriteria.getQuery());
		}
		if (order != null) {
			query.append(" order by "
					+ rootEntityAlias
					+ "."
					+ order.getPropertyName()
					+ " "
					+ (order.getType() == OrderType.ASC ? " asc" : "desc"
							+ ", " + rootEntityAlias + ".id desc"));
		}
		final QueryWrapper wrapper = new QueryWrapper(query.toString(),
				(nativeCriteria == null
						? new HashMap<String, Object>()
						: nativeCriteria.getArguments()));
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(wrapper.getQuery() + " - " + wrapper.getArguments());
		}
		return wrapper;
	}

	@Override
	public void delete(final MAbstractEntity entity) throws ModelSpaceException {
		try {
			sessionFactory.getCurrentSession().delete(entity);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
	}

	@Override
	public void evict(final MAbstractEntity entity) {
		try {
			sessionFactory.getCurrentSession().evict(entity);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
	}

	@Override
	public <T extends MAbstractEntity> List<T> find(final Class<T> type,
			final Criterion criterion) {
		List<T> result = null;
		try {
			result = find(type, criterion, null, null, null);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MAbstractEntity> List<T> find(final Class<T> type,
			final Criterion criterion, final Order order,
			final Integer startPosition, final Integer size) {
		List<T> result = null;
		try {
			final Query query = getQuery(type, criterion, order, null);
			if (startPosition != null && size != null) {
				query.setFirstResult(startPosition);
				query.setMaxResults(size);
			}
			result = query.list();;
			HibernateEntityConverter.convertHibernateCollections(result,
					PersistentCollection.class);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}

		return result;
	}

	@Override
	public List<?> findProperties(final Class<? extends MAbstractEntity> type,
			final Criterion criterion, final Order order,
			final Projection projection, final String... properties)
			throws ModelSpaceException {
		List<?> result = null;
		try {
			result = getQuery(type, criterion, order, projection, properties)
					.list();
			HibernateEntityConverter.convertHibernateCollections(result,
					PersistentCollection.class);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return result;
	}

	@Override
	public List<?> findProperties(final Class<? extends MAbstractEntity> type,
			final Criterion criterion, final Projection projection,
			final String... properties) throws ModelSpaceException {
		return findProperties(type, criterion, null, projection, properties);
	}

	@Override
	public List<?> findProperties(final Class<? extends MAbstractEntity> type,
			final Criterion criterion, final String... properties)
			throws ModelSpaceException {
		return findProperties(type, criterion, null, properties);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MAbstractEntity> T findUniqueEntity(final Class<T> type,
			final Criterion criterion) {
		T result = null;
		try {
			result = (T) getQuery(type, criterion, null, null).uniqueResult();
			HibernateEntityConverter.convertHibernateCollections(result,
					PersistentCollection.class);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MAbstractEntity> T get(final Class<T> type, final Long id) {
		T result = null;
		try {
			result = (T) sessionFactory.getCurrentSession().get(type, id);
			HibernateEntityConverter.convertHibernateCollections(result,
					PersistentCollection.class);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MAbstractEntity> List<T> getAll(final Class<T> type) {
		List<T> result = null;
		try {
			result = sessionFactory.getCurrentSession().createCriteria(type)
					.list();
			HibernateEntityConverter.convertHibernateCollections(result,
					PersistentCollection.class);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return result;
	}

	protected Query getQuery(final Class<? extends MAbstractEntity> type,
			final Criterion criterion, final Order order,
			final Projection projection, final String... properties) {
		final QueryWrapper queryWrapper = createQueryWrapper(type, criterion,
				order, projection, properties);
		return toNativeQuery(queryWrapper);
	}

	@Override
	public Long save(final MAbstractEntity entity) throws ModelSpaceException {
		Long id = null;
		try {
			id = (Long) sessionFactory.getCurrentSession().save(entity);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return id;
	}

	@Override
	public void saveOrUpdate(final MAbstractEntity entity) {
		// Don't use merge here. It creates new copy of the provided entity and
		// detaches the entity from the session. Futhermore it doesn't save new
		// subentites.
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(entity);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
	}

	private Query toNativeQuery(final QueryWrapper queryWrapper) {
		final Query query = sessionFactory.getCurrentSession().createQuery(
				queryWrapper.getQuery());
		for (final Map.Entry<String, ?> entry : queryWrapper.getArguments()
				.entrySet()) {
			final Object value = entry.getValue();
			if (value instanceof Collection<?>) {
				query.setParameterList(entry.getKey(), (Collection<?>) value);
			} else {
				query.setParameter(entry.getKey(), value);
			}
		}
		return query;
	}

	@Override
	public void update(final MAbstractEntity entity) throws ModelSpaceException {
		try {
			sessionFactory.getCurrentSession().update(entity);
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
	}

	@Override
	public List createQuery(final String query) throws ModelSpaceException {
		try {
			return sessionFactory.getCurrentSession().createSQLQuery(query).list();
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
	}

	@Override
	public boolean flush() {
		try {
			if(sessionFactory.getCurrentSession().isDirty()) {
				sessionFactory.getCurrentSession().flush();
				return true;
			}
		} catch (final Exception ex) {
			throw new ModelSpaceException(ex);
		}
		return false;
	}
}
