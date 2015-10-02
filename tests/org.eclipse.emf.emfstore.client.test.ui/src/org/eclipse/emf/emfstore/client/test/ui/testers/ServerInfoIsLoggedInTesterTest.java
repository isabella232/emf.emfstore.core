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
package org.eclipse.emf.emfstore.client.test.ui.testers;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.exceptions.ESServerStartFailedException;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.ui.testers.ServerInfoIsLoggedInTester;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class ServerInfoIsLoggedInTesterTest extends ESTestWithLoggedInUser {

	@Override
	@Before
	public void before() {
		startEMFStore();
		super.before();
	}

	@Test
	public void isLoggedIn() throws ESServerStartFailedException, ESException {
		final ServerInfoIsLoggedInTester tester = new ServerInfoIsLoggedInTester();
		final ServerInfo serverInfo = ESServerImpl.class.cast(getServer()).toInternalAPI();
		assertTrue(tester.test(serverInfo, "serverInfoIsLoggedIn", null, Boolean.TRUE));
	}

	@Test
	public void isNotLoggedIn() throws ESException, ESServerStartFailedException {
		final ServerInfoIsLoggedInTester tester = new ServerInfoIsLoggedInTester();
		final ServerInfo serverInfo = ESServerImpl.class.cast(getServer()).toInternalAPI();
		stopEMFStore();
		assertTrue(tester.test(serverInfo, "serverInfoIsLoggedIn", null, Boolean.FALSE));
		// after expects the server to be running
		before();
	}
}
