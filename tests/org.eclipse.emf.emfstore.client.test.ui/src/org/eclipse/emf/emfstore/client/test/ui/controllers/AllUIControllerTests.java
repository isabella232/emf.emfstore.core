/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import org.eclipse.emf.emfstore.internal.client.configuration.Behavior;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.google.common.base.Optional;

/**
 * Test Suite for running all UI controllers tests.
 *
 * @author emueller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	NoLocalChangesCommitControllerTest.class,
	LocalProjectNeedsToBeUpdatedCommitControllerTest.class,
	UIAddTagControllerTest.class,
	UIAskForBranchCheckoutControllerTest.class,
	UIBranchControllersTest.class,
	UICheckoutControllerTest.class,
	UICreateRemoteProjectControllerTest.class,
	UIDeleteRemoteProjectControllerTest.class,
	UIMergeControllerTest.class,
	UIRevertCommitControllerTest.class,
	UIRevertCommitControllerTest2.class,
	UIServerControllerTest.class,
	UISessionControllerTest.class,
	UIShareProjectControllerTest.class,
	UIUpdateProjectControllerTest.class,
	UIUpdateProjectToVersionControllerTest.class,
	UIPagedUpdateProjectControllerTest.class,
	UIShowHistoryControllerTest.class,
	UIShowHistoryControllerForElementTest.class,
	UIUndoLastOperationControllerTest.class
})
public class AllUIControllerTests {

	private static final Optional<Integer> MIN_CHANGEPACKAGE_FRAGMENT_SIZE = Optional.of(1);

	private static Behavior clientBehavior = Configuration.getClientBehavior();
	private static Optional<Integer> clientFragmentSize;
	private static Optional<Integer> serverFragmentSize;

	@BeforeClass
	public static void beforeClass() {
		clientFragmentSize = clientBehavior.getChangePackageFragmentSize();
		serverFragmentSize = ServerConfiguration.getChangePackageFragmentSize();

		Configuration.getClientBehavior().setChangePackageFragmentSize(
			MIN_CHANGEPACKAGE_FRAGMENT_SIZE);
		ServerConfiguration.setChangePackageFragmentSize(
			MIN_CHANGEPACKAGE_FRAGMENT_SIZE);
	}

	@AfterClass
	public static void afterClass() {
		Configuration.getClientBehavior().setChangePackageFragmentSize(clientFragmentSize);
		ServerConfiguration.setChangePackageFragmentSize(serverFragmentSize);
	}

}
