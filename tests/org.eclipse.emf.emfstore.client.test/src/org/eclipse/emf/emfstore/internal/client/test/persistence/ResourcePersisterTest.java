/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithSharedProject;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author emueller
 *
 */
public class ResourcePersisterTest extends ESTestWithSharedProject {

	private static final String ANOTHER_PROJECT = "AnotherProject";
	private static IProgressMonitor npm = new NullProgressMonitor();
	private Resource anotherProjectResource;

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	public void before() {
		super.before();
		try {
			final ESRemoteProject remoteProject = getServer().createRemoteProject(
				getUsersession(),
				ANOTHER_PROJECT,
				nullProgressMonitor());
			final ESLocalProject anotherLocalProject = remoteProject.checkout("AnotherProject", nullProgressMonitor());
			final ProjectSpace internalAPI = ((ESLocalProjectImpl) anotherLocalProject).toInternalAPI();
			anotherProjectResource = internalAPI.eResource();
		} catch (final ESException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void saveOnlyAffectedProject() throws ESException {
		final long timeStamp = anotherProjectResource.getTimeStamp();
		// perform change and commit
		Add.toProject(getLocalProject(), Create.player());
		getLocalProject().commit(new NullProgressMonitor());

		assertEquals(anotherProjectResource.getTimeStamp(), timeStamp);
	}

	private static IProgressMonitor nullProgressMonitor() {
		return npm;
	}
}
