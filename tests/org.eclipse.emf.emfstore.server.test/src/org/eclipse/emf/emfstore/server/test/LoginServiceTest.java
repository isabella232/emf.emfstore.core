/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.common.mocks.ConnectionMock;
import org.eclipse.emf.emfstore.internal.client.configuration.VersioningInfo;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author emueller
 *
 */
public class LoginServiceTest extends ESTestWithLoggedInUserMock {

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void verifyUser() {
		final ConnectionMock mock = (ConnectionMock) ESWorkspaceProviderImpl.getInstance().getConnectionManager();
		final String password = KeyStoreManager.getInstance().encrypt("super", getServerInfo()); //$NON-NLS-1$
		final boolean isValid = mock.getAccessControl().getLoginService().verifyUser("super", password, //$NON-NLS-1$
			new VersioningInfo().getClientVersion().toAPI());
		assertTrue(isValid);
	}
}
