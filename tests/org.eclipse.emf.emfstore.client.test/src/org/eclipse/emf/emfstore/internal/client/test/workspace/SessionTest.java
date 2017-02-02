/*******************************************************************************
 * Copyright (c) 2011-2017 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.workspace;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SessionTest extends ESTestWithLoggedInUser {

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void stableSessionIdIfLoggedIn() throws ESException {
		final ESSessionId sessionId = getUsersession().getSessionId();
		getUsersession().refresh();
		final ESSessionId sessionId2 = getUsersession().getSessionId();
		assertEquals(sessionId, sessionId2);
	}
}
