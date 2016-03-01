/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MUser;

public interface UserProperties extends PropertyAccess<MUser> {
	@Path("disabled")
	ValueProvider<MUser, Boolean> disabled();

	@Path("firstName")
	ValueProvider<MUser, String> firstName();

	@Path("login")
	ModelKeyProvider<MUser> key();

	@Path("lastName")
	ValueProvider<MUser, String> lastName();

	@Path("ldapAuthenticated")
	ValueProvider<MUser, Boolean> ldapAuthenticated();

	@Path("login")
	ValueProvider<MUser, String> login();

	@Path("secondName")
	ValueProvider<MUser, String> secondName();
}