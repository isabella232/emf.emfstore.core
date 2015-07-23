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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.common.util.ServerUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.BeforeClass;
import org.junit.Test;

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

	@Test
	public void testAutoCreateACUser() throws ESException {
		/* act */
		getServer().login(NOT_EXISTING_USER_NAME, ""); //$NON-NLS-1$
		/* assert */
		final ACUser user = ServerUtil.getUser(getUsersession(), NOT_EXISTING_USER_NAME);
		assertNotNull(user);
		assertEquals(NOT_EXISTING_USER_NAME, user.getName());
	}
}
