/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate.listener;

import java.util.Date;

import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.domain.MLoggedEntity;

/**
 * Sets current timestamp every time entity is being persisted.
 * 
 * @author novohatskiy.r
 * 
 */
@Component
public class ModificationTrackingSaveOrUpdateEventListener
		implements
			SaveOrUpdateEventListener {

	private static final long serialVersionUID = -3096347443736638985L;

	@Override
	public void onSaveOrUpdate(final SaveOrUpdateEvent event) {
		if (event.getObject() instanceof MLoggedEntity) {
			final MLoggedEntity loggedEntity = (MLoggedEntity) event
					.getObject();
			if ((loggedEntity.getId() != null)
					|| (loggedEntity.getId() == null && loggedEntity
							.getModificationDateTime() == null)) {
				loggedEntity.setModificationDateTime(new Date());
			}
		}
	}

}