/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.exception.DomainModelException;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class HibernateEntityConverter {
	public static final String GETTER_PREFIX = "get";

	public static final String SETTER_PREFIX = "set";

	public static void convertHibernateCollections(
			final Collection<?> collection, final Class<?> collectionType) {
		for (final Object entity : collection) {
			if (entity instanceof MAbstractEntity) {
				convertHibernateCollections(entity, collectionType);
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void convertHibernateCollections(final Object entity,
			final Class<?> collectionType) {
		if (entity == null) {
			return;
		}
		final Class<?> type = entity.getClass();
		for (final Method getter : type.getMethods()) {
			if (getter.getParameterTypes().length > 0
					|| !getter.getName().startsWith("get")
					|| Modifier.isStatic(getter.getModifiers())
					|| getter.getAnnotation(Transient.class) != null) {
				continue;
			}
			final Class<?> returnType = getter.getReturnType();
			Object rawValue = null;
			if (Collection.class.isAssignableFrom(returnType)
					|| MAbstractEntity.class.isAssignableFrom(returnType)
					|| returnType.getAnnotation(Embeddable.class) != null) {
				try {
					rawValue = getter.invoke(entity);
				} catch (final Exception e) {
					throw new ModelSpaceException("Cannot get value of "
							+ getter, e);
				}
			}
			if (MAbstractEntity.class.isAssignableFrom(returnType)
					|| returnType.getAnnotation(Embeddable.class) != null) {
				if (rawValue != null) {
					convertHibernateCollections(rawValue, collectionType);
				}
			} else if (Collection.class.isAssignableFrom(returnType)) {
				try {
					Collection<?> value = (Collection<?>) rawValue;

					if (value != null
							&& collectionType
									.isAssignableFrom(value.getClass())) {
						if (value instanceof Set) {
							value = new HashSet(value);
						} else if (value instanceof List) {
							value = new ArrayList(value);
						} else {
							throw new ModelSpaceException(
									value.getClass().getName()
											+ " not supported. (implement me if you want)");
						}
						convertHibernateCollections(value, collectionType);
						getSetter(getter).invoke(entity, value);
					}
				} catch (final Exception e) {
					throw new ModelSpaceException(
							"Cannot update hibernate persistent collections", e);
				}
			}
		}
	}

	public static String getEntityAnnotationName(
			final Class<? extends MAbstractEntity> type) {
		String entityName = type.getSimpleName();
		// entity name could be explicitly set in Entity annotation
		// like @Entity(name = "MPolicySendEmailAction")
		final Entity entityAnnotation = type.getAnnotation(Entity.class);
		if (entityAnnotation != null
				&& SimpleUtils.isNotNullAndNotEmpty(entityAnnotation.name())) {
			entityName = entityAnnotation.name();
		}

		return entityName;
	}

	/**
	 * Find all bean properties of {@link Collection} type
	 * 
	 * @param type
	 * @return <getter, setter> map
	 */
	public static Method getSetter(final Method getter) {
		final String getterName = getter.getName();
		final String setterName = SETTER_PREFIX
				+ getterName.substring(3, 4).toUpperCase()
				+ getterName.substring(4);
		Method setter;
		try {
			setter = getter.getDeclaringClass().getMethod(setterName,
					getter.getReturnType());
		} catch (final Exception e) {
			throw new DomainModelException("Cannot find setter for "
					+ getter.getDeclaringClass().getName() + "." + getterName);
		}
		return setter;
	}

}
