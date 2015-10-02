/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.internal.client.importexport.ExportImportControllerExecutor;
import org.eclipse.emf.emfstore.internal.client.importexport.ExportImportControllerFactory;
import org.eclipse.emf.emfstore.internal.client.importexport.impl.ExportImportDataUnits;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.After;
import org.junit.Test;

public class ImportExportTest extends ESTest {

	private File temp;

	@After
	@Override
	public void after() {
		if (temp != null) {
			FileUtils.deleteQuietly(temp);
			temp = null;
		}
		super.after();
	}

	@Test
	public void testExportImportChangesController() throws IOException {
		final ProjectSpace copiedProjectSpace = ESWorkspaceProviderImpl.getInstance().getWorkspace()
			.createLocalProject("Copy").toInternalAPI();
		copiedProjectSpace.setProject(ModelUtil.clone(getProject()));
		final TestElement testElement = Create.testElement("A");
		ProjectUtil.addElement(getLocalProject(), testElement);
		assertTrue(getProjectSpace().getLocalChangePackage().size() > 0);

		// TODO: assert file extension is correct

		temp = File.createTempFile("changes", ExportImportDataUnits.Change.getExtension());
		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Export.getExportChangesController(getProjectSpace()));

		// TODO: assert file was written

		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Import.getImportChangesController(copiedProjectSpace));

		assertTrue(ModelUtil.areEqual(getProjectSpace().getProject(), copiedProjectSpace.getProject()));
	}

	@Test
	public void testExportImportProjectController() throws IOException {
		final TestElement testElement = Create.testElement("A");
		ProjectUtil.addElement(getLocalProject(), testElement);
		assertTrue(getProjectSpace().getLocalChangePackage().size() > 0);

		// TODO: assert file extension is correct

		temp = File.createTempFile("project", ExportImportDataUnits.Project.getExtension());
		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Export.getExportProjectController(getProjectSpace()));

		// TODO: assert file was written

		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Import.getImportProjectController("importedProject"));

		ProjectSpace newProjectSpace = null;

		for (final ESProject project : ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects()) {
			if (project.getProjectName().equals("importedProject")) {
				newProjectSpace = getProjectSpace();
				break;
			}
		}

		assertTrue(newProjectSpace != null);
	}

	@Test
	public void testDuplicateImportOfProjectSpace() throws IOException {
		temp = File.createTempFile("projectSpace", ExportImportDataUnits.ProjectSpace.getExtension());
		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Export.getExportProjectSpaceController(getProjectSpace()));

		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Import.getImportProjectSpaceController());
		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Import.getImportProjectSpaceController());

		assertEquals(3, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());
		final ProjectSpace projectSpace = ESWorkspaceProviderImpl.getInstance().getInternalWorkspace()
			.getProjectSpaces().get(1);
		final ProjectSpace projectSpace2 = ESWorkspaceProviderImpl.getInstance().getInternalWorkspace()
			.getProjectSpaces().get(2);
		assertFalse(projectSpace.getIdentifier().equals(projectSpace2.getIdentifier()));
	}

	@Test
	public void testExportImportProjectSpaceController() throws IOException {
		final TestElement testElement = Create.testElement("A");
		ProjectUtil.addElement(getLocalProject(), testElement);
		assertTrue(getProjectSpace().getLocalChangePackage().size() > 0);

		// TODO: assert file extension is correct

		temp = File.createTempFile("projectSpace", ExportImportDataUnits.ProjectSpace.getExtension());
		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Export.getExportProjectSpaceController(getProjectSpace()));

		// TODO: assert file was written
		assertEquals(1, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		new ExportImportControllerExecutor(temp, new NullProgressMonitor())
			.execute(ExportImportControllerFactory.Import.getImportProjectSpaceController());

		assertEquals(2, ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().size());

		final ESLocalProject a = ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().get(0);
		final ESLocalProject b = ESWorkspaceProviderImpl.getInstance().getWorkspace().getLocalProjects().get(1);

		assertEquals(a.getModelElements().size(), b.getModelElements().size());
	}
}
