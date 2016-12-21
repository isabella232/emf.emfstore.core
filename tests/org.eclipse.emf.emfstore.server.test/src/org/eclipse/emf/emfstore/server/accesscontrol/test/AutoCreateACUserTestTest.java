/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.accesscontrol.test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Assert that it is not possible to log in without password when auto-create authenticated users is set to true outside
 * of LDAP auth.
 */
public class AutoCreateACUserTestTest extends ESTestWithLoggedInUserMock {

	private static final String NOT_EXISTING_USER_NAME = "FOO_USER"; //$NON-NLS-1$

	private static void startEMFStoreWithAutoCreateProperty() {
		final Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put(ServerConfiguration.AUTHENTICATION_CREATE_AUTHENTICATED_USERS, Boolean.TRUE.toString());
		startEMFStore(properties);
	}

	@BeforeClass
	public static void beforeClass() {
		startEMFStoreWithAutoCreateProperty();
	}

	@Test(expected = ESException.class)
	public void testAutoCreateACUser() throws ESException {
		/* act */
		getServer().login(NOT_EXISTING_USER_NAME, ""); //$NON-NLS-1$
	}
}
