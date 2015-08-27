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
package org.eclipse.emf.emfstore.internal.client.test.persistence;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.junit.Test;

public class ResourceCrossContainmentTest extends ESTest {

	@Test
	public void addModelElementAlreadyContainedInResourceToProject() throws IOException {
		final ResourceSetImpl resourceSet = new ResourceSetImpl();
		final File tempFile = File.createTempFile("test", ".xmi");
		final Resource resource = resourceSet.createResource(URI.createFileURI(tempFile.getAbsolutePath()));
		final Player player = Create.player();
		resource.getContents().add(player);
		getLocalProject().getModelElements().add(player);
		assertEquals(player.eContainer(), getProject());
		assertEquals(getProject().eResource(), player.eResource());
	}

}
