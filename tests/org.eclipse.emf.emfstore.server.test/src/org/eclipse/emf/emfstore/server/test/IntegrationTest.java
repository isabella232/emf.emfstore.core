/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests that utilize the actual EMFStore middleware.
 *
 * @author emueller
 *
 */
public class IntegrationTest extends ESTestWithLoggedInUser {

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void test() throws ESException {
		final TestElement testElement = Create.testElement("foo"); //$NON-NLS-1$
		share(getUsersession(), getLocalProject());

		final ESLocalProject clonedProject = getLocalProject().getRemoteProject().checkout("clonedProject", //$NON-NLS-1$
			new NullProgressMonitor());

		final PrimaryVersionSpec baseVersion = getProjectSpace().getBaseVersion();
		Add.toProject(getLocalProject(), testElement);
		Add.toProject(getLocalProject(), Create.testElement("bar")); //$NON-NLS-1$

		getLocalProject().commit(new NullProgressMonitor());

		final PrimaryVersionSpec targetVersion = getProjectSpace().getBaseVersion();
		final List<AbstractChangePackage> changes = getProjectSpace().getChanges(targetVersion, baseVersion);

		clonedProject.update(new NullProgressMonitor());

		assertThat(changes.size(), equalTo(1));
		assertThat(getLocalProject().getBaseVersion().getIdentifier(), equalTo(1));

	}
}
