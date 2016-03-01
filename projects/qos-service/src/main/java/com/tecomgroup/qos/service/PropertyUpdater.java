/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.tecomgroup.qos.PropertiesContainer;
import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MProperty.PropertyType;
import com.tecomgroup.qos.modelspace.ModelSpace;

/**
 * The updater of the {@link MProperty} objects.
 * 
 * @author kunilov.p
 * 
 */
public class PropertyUpdater {

	@Autowired
	private ModelSpace modelSpace;
	@Autowired
	private TransactionTemplate transactionTemplate;

	private final static Logger LOGGER = Logger
			.getLogger(PropertyUpdater.class);

	/**
	 * Updates the properties of its container.
	 * 
	 * @param containerWithOldProperties
	 *            The container with the properties to update.
	 * @param containerWithNewProperties
	 *            The container with new properties.
	 * @param propertyType
	 *            The type of properties to update.
	 * @return true if some properties were updated otherwise false.
	 * 
	 */
	public boolean updateProperties(
			final PropertiesContainer containerWithOldProperties,
			final PropertiesContainer containerWithNewProperties,
			final PropertyType propertyType) {
		boolean propertiesAreUpdated = false;

		final Map<String, MProperty> propertiesToUpdate = new HashMap<String, MProperty>();
		if (containerWithOldProperties.getProperties() != null) {
			for (final MProperty oldProperty : containerWithOldProperties
					.getProperties()) {
				if (oldProperty.hasPropertyType(propertyType)) {
					propertiesToUpdate.put(oldProperty.getName(), oldProperty);
				}
			}
		}

		final Map<String, MProperty> newProperties = new HashMap<String, MProperty>();
		if (containerWithNewProperties.getProperties() != null) {
			for (final MProperty updatedProperty : containerWithNewProperties
					.getProperties()) {
				if (updatedProperty.hasPropertyType(propertyType)) {
					newProperties.put(updatedProperty.getName(),
							updatedProperty);
				}
			}
		}

		// remove properties
		final Set<String> removedPropertyNames = new HashSet<String>(
				propertiesToUpdate.keySet());
		removedPropertyNames.removeAll(newProperties.keySet());
		for (final String propertyName : removedPropertyNames) {
			final MProperty propertyToRemove = propertiesToUpdate
					.get(propertyName);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					containerWithOldProperties.removeProperty(propertyToRemove);
					if (containerWithOldProperties instanceof MAbstractEntity) {
						modelSpace
								.saveOrUpdate((MAbstractEntity) containerWithOldProperties);
					}
					modelSpace.delete(propertyToRemove);
				}
			});
			LOGGER.info("Remove property: " + propertyToRemove
					+ " from container: " + containerWithOldProperties);
			propertiesAreUpdated = true;
		}

		// add new properties
		final Set<String> newPropertyNames = new HashSet<String>(
				newProperties.keySet());
		newPropertyNames.removeAll(propertiesToUpdate.keySet());
		for (final String propertyName : newPropertyNames) {
			final MProperty propertyToAdd = newProperties.get(propertyName);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					modelSpace.save(propertyToAdd);
					containerWithOldProperties.addProperty(propertyToAdd);
					if (containerWithOldProperties instanceof MAbstractEntity) {
						modelSpace
								.saveOrUpdate((MAbstractEntity) containerWithOldProperties);
					}
				}
			});
			LOGGER.info("Add new property: " + propertyToAdd
					+ " to container: " + containerWithOldProperties);
			propertiesAreUpdated = true;
		}

		// update properties
		final Set<String> updatedPropertyNames = new HashSet<String>(
				propertiesToUpdate.keySet());
		updatedPropertyNames.retainAll(newProperties.keySet());
		for (final String propertyName : updatedPropertyNames) {
			final MProperty oldProperty = propertiesToUpdate.get(propertyName);
			final MProperty updatedProperty = newProperties.get(propertyName);
			final boolean propertyIsUpdated = oldProperty
					.updateSimpleFields(updatedProperty);

			if (propertyIsUpdated) {
				transactionTemplate
						.execute(new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								modelSpace.saveOrUpdate(oldProperty);
							}
						});
				LOGGER.info("Update property: " + oldProperty
						+ " of the container: " + containerWithOldProperties);
				propertiesAreUpdated = true;
			}
		}

		return propertiesAreUpdated;
	}

}
