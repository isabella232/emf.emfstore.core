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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}
}
