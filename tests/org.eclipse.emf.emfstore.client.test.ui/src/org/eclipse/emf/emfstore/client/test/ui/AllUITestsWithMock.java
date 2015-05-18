/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.ui.conflictdetection.BidirectionalConflictMergeTest;
import org.eclipse.emf.emfstore.client.test.ui.controllers.AllUIControllerTests;
import org.eclipse.emf.emfstore.internal.client.configuration.Behavior;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.google.common.base.Optional;

/**
 * Runs all UI tests.
 *
 * @author emueller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AllUIControllerTests.class,
	BidirectionalConflictMergeTest.class })
public class AllUITestsWithMock extends ESTestWithLoggedInUserMock {

	public static final int TIMEOUT = 20000;

	private static Behavior clientBehavior = Configuration.getClientBehavior();
	private static Optional<Integer> clientFragmentSize;
	private static Optional<Integer> serverFragmentSize;

	@BeforeClass
	public static void beforeClass() {
		clientFragmentSize = clientBehavior.getChangePackageFragmentSize();
		serverFragmentSize = ServerConfiguration.getChangePackageFragmentSize();

		Configuration.getClientBehavior().setChangePackageFragmentSize(
			Optional.<Integer> absent());
		ServerConfiguration.setChangePackageFragmentSize(
			Optional.<Integer> absent());
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
		Configuration.getClientBehavior().setChangePackageFragmentSize(clientFragmentSize);
		ServerConfiguration.setChangePackageFragmentSize(serverFragmentSize);
	}
}
