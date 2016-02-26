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
package org.eclipse.emf.emfstore.internal.client.test.workspace;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

public class WorkspaceTest extends ESTestWithLoggedInUser {

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	private Optional<Integer> changePackageFragmentSize;

	@Override
	@Before
	public void before() {
		changePackageFragmentSize = Configuration.getClientBehavior().getChangePackageFragmentSize();
		super.before();
	}

	@Override
	@After
	public void after() {
		super.after();
		Configuration.getClientBehavior().setChangePackageFragmentSize(changePackageFragmentSize);
	}

	@Test
	public void restartServerAndUpdateOutdatedClient() throws ESException {
		Configuration.getClientBehavior().setChangePackageFragmentSize(Optional.of(1));
		final TestElement testElement = Create.testElement("foo"); //$NON-NLS-1$
		Add.toProject(getLocalProject(), testElement);
		final ESModelElementId testElementId = getLocalProject().getModelElementId(testElement);
		share(getUsersession(), getLocalProject());

		final ESLocalProject clonedProject = getLocalProject()
			.getRemoteProject()
			.checkout("clonedProject", new NullProgressMonitor());

		// perform some changes to enfore splittign
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				testElement.setName("bar");
				testElement.getContainedElements().add(Create.testElement());
				testElement.getContainedElements().add(Create.testElement());
				testElement.getContainedElements().add(Create.testElement());
			}
		});
		getLocalProject().commit(new NullProgressMonitor());

		restartEMFStore();

		clonedProject.update(new NullProgressMonitor());
		final TestElement copiedTestElement = (TestElement) clonedProject.getModelElement(testElementId);

		assertThat(copiedTestElement.getName(), equalTo("bar"));
	}

	@Test
	public void restartClientAndCheckBaseVersion() throws ESException {
		final TestElement testElement = Create.testElement("foo"); //$NON-NLS-1$
		Add.toProject(getLocalProject(), testElement);
		share(getUsersession(), getLocalProject());

		final ESLocalProject clonedProject = getLocalProject()
			.getRemoteProject()
			.checkout("clonedProject", new NullProgressMonitor());

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				testElement.setName("bar");
			}
		});
		getLocalProject().commit(new NullProgressMonitor());

		clonedProject.update(new NullProgressMonitor());

		final ProjectSpace cloned = ESLocalProjectImpl.class.cast(clonedProject).toInternalAPI();
		final URI uri = cloned.eResource().getURI();
		final URI normalizedUri = cloned.getResourceSet().getURIConverter().normalize(uri);
		final ResourceSetImpl resourceSetImpl = new ResourceSetImpl();
		final Resource resource = resourceSetImpl.getResource(normalizedUri, true);
		final ProjectSpace loadedProjectSpace = (ProjectSpace) resource.getContents().get(0);

		assertThat(loadedProjectSpace.getBaseVersion().getIdentifier(), equalTo(1));
	}
}
