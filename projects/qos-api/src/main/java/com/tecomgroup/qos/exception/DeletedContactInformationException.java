/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.exception;

import java.util.Set;

/**
 * @author sviyazov.a
 * 
 */
public class DeletedContactInformationException extends QOSException {

	private static final long serialVersionUID = -3449253853027353738L;

	private Set<String> deletedKeys;

	public DeletedContactInformationException() {
		super();
	}

	public DeletedContactInformationException(final Set<String> deletedContacts) {
		super();
		deletedKeys = deletedContacts;
	}

	public Set<String> getDeletedKeys() {
		return deletedKeys;
	}

	public void setDeletedKeys(final Set<String> deletedKeys) {
		this.deletedKeys = deletedKeys;
	}
}
