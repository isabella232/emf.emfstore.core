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
package org.eclipse.emf.emfstore.client.changetracking.test.notification;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * Test case for Bug 450069 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=450069).
 * 
 * @author emueller
 * 
 */
public class ProjectTest extends ESTest {

	@Test
	public void changeCrossReferenceToDeletedModelElementWithinProject() {
		final TestElement foo = Create.testElement();
		final TestElement bar = Create.testElement();
		final TestElement baz = Create.testElement();

		bar.getContainedElements().add(baz);

		Add.toProject(getLocalProject(), foo, bar);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		clearOperations();

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getProject().getModelElements().remove(bar);
				foo.getReferences().add(baz);
			}
		});

		assertTrue(getProject().contains(baz));

		final ChangePackage localChangePackage = getProjectSpace().getLocalChangePackage();
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				localChangePackage.reverse()
					.apply(getProject());
			}
		});

		assertTrue(ModelUtil.areEqual(getProject(), clonedProjectSpace.getProject()));
	}
}
