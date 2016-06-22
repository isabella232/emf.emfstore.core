/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithSharedProject;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for Bug 470553.
 *
 * @author emueller
 *
 */
public class ProjectLastUpdatedTest extends ESTestWithSharedProject {

	private ESLocalProject clonedProject;

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	@Before
	public void before() {
		super.before();
		final TestElement testElement = Create.testElement();
		Add.toProject(getLocalProject(), testElement);
		try {
			clonedProject = getLocalProject().getRemoteProject().checkout("Copy", new NullProgressMonitor());
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void lastUpdatedIsSetAfterCheckout() {
		assertNotNull(clonedProject.getLastUpdated());
	}

	@Test
	public void lastUpdatedDidChange() throws ESException {
		Add.toProject(getLocalProject(), Create.testElement());
		ProjectUtil.commit(getLocalProject());
		final Date before = clonedProject.getLastUpdated();
		clonedProject.update(new NullProgressMonitor());
		final Date after = clonedProject.getLastUpdated();
		assertFalse(before.equals(after));
	}

}
