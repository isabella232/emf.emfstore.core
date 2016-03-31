/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.migration.edapt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.startup.MigrationManager;
import org.eclipse.emf.emfstore.internal.server.storage.ServerXMIResourceSetProvider;
import org.eclipse.emf.emfstore.internal.server.storage.XMIServerURIConverter;
import org.eclipse.emf.emfstore.migration.edapt.test.model.d.model.D;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;
import org.eclipse.emf.emfstore.server.ESServerURIUtil;
import org.junit.After;
import org.junit.Test;

@SuppressWarnings("restriction")
public class MigrationManagerTest {

	private static Set<File> tempFiles = new LinkedHashSet<File>();

	@After
	public void cleanup() throws IOException {
		for (final File file : tempFiles) {
			if (file.isDirectory()) {
				org.eclipse.emf.emfstore.internal.common.model.util.FileUtil.deleteDirectory(file, true);
			} else {
				file.delete();
			}
		}
		tempFiles.clear();
	}

	private static String copyServerToTempDir(boolean inMemory) {
		try {
			final long currentTimeMillis = System.currentTimeMillis();
			final String path = System.getProperty("java.io.tmpdir");
			final File dir = new File(path + "/esMigration" + currentTimeMillis);
			dir.mkdirs();
			tempFiles.add(dir);

			final File source = getInputStream(inMemory);
			final File destination = new File(dir, "default_dev");

			org.eclipse.emf.emfstore.internal.common.model.util.FileUtil.copyDirectory(source, destination);

			return destination.getAbsolutePath() + "/";
		} catch (final FileNotFoundException ex) {
		} catch (final IOException ex) {
		}
		fail();
		return null;
	}

	private static File getInputStream(boolean inMemory) {
		try {
			final URL url = new URL(MessageFormat
				.format(
					"platform:/plugin/org.eclipse.emf.emfstore.migration.edapt.test/data/fullServer/{0}/default_dev",
					inMemory ? "imcp" : "fbcp"));
			final java.net.URI uri = FileLocator.resolve(url).toURI();
			return new File(uri);
		} catch (final MalformedURLException ex) {
		} catch (final IOException ex) {
		} catch (final URISyntaxException ex) {
		}
		fail();
		return null;
	}

	private static ResourceSet createResourceSet(final String serverHome) {
		final ResourceSet resourceSet = new ServerXMIResourceSetProvider().getResourceSet();
		resourceSet.setURIConverter(new XMIServerURIConverter() {
			@Override
			protected String getServerHome() {
				return serverHome;
			}
		});
		return resourceSet;
	}

	private static ServerSpace loadServerSpace(final String serverHome) {
		final ResourceSet resourceSet = createResourceSet(serverHome);
		final URI serverSpaceURI = ESServerURIUtil.createServerSpaceURI();
		final Resource resource = resourceSet.createResource(serverSpaceURI);
		try {
			resource.load(null);
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
			return null;
		}
		final ServerSpace serverSpace = (ServerSpace) resource.getContents().get(0);
		return serverSpace;
	}

	private static void assertServerspace(ServerSpace serverSpace) {
		/* 1 project */
		assertEquals(1, serverSpace.getProjects().size());
		final ProjectHistory projectHistory = serverSpace.getProjects().get(0);

		{/* version 1: empty project */
			final Version version = projectHistory.getVersions().get(0);
			assertNull(version.getChanges());
			assertNotNull(version.getProjectState());
			assertEquals(0, version.getProjectState().getModelElements().size());
		}

		{/* version 2: Add root element */
			final Version version = projectHistory.getVersions().get(1);
			assertNotNull(version.getChanges());
			assertOperationCount(version, 1);
			assertNull(version.getProjectState());
		}

		{/* version 3: Added single ref in original version */
			final Version version = projectHistory.getVersions().get(2);
			assertNotNull(version.getChanges());
			assertOperationCount(version, 2);
			assertNull(version.getProjectState());
		}

		{/* version 4: Added two refs in original version */
			final Version version = projectHistory.getVersions().get(3);
			assertNotNull(version.getChanges());
			assertOperationCount(version, 3);
			assertNotNull(version.getProjectState());
			assertEquals(4, version.getProjectState().getModelElements().size());
			final D d1 = (D) version.getProjectState().getModelElements().get(0);
			final D d2 = (D) version.getProjectState().getModelElements().get(1);
			final D d31 = (D) version.getProjectState().getModelElements().get(2);
			final D d32 = (D) version.getProjectState().getModelElements().get(3);
			assertEquals(3, d1.getRefs().size());
			assertEquals(0, d1.getRefs().indexOf(d2));
			assertEquals(1, d1.getRefs().indexOf(d31));
			assertEquals(2, d1.getRefs().indexOf(d32));
		}

		{/* version 5: Set single att */
			final Version version = projectHistory.getVersions().get(4);
			assertNotNull(version.getChanges());
			assertOperationCount(version, 4);
			assertNotNull(version.getProjectState());
			assertEquals(4, version.getProjectState().getModelElements().size());
			final D d1 = (D) version.getProjectState().getModelElements().get(0);
			final D d2 = (D) version.getProjectState().getModelElements().get(1);
			final D d31 = (D) version.getProjectState().getModelElements().get(2);
			final D d32 = (D) version.getProjectState().getModelElements().get(3);
			assertEquals("d1", d1.getName());
			assertEquals("d2", d2.getName());
			assertEquals("d31", d31.getName());
			assertEquals("d32", d32.getName());
		}

		{/* version 6: Multi att */
			final Version version = projectHistory.getVersions().get(5);
			assertNotNull(version.getChanges());
			assertOperationCount(version, 2);
			assertNotNull(version.getProjectState());
			assertEquals(4, version.getProjectState().getModelElements().size());
			final D d31 = (D) version.getProjectState().getModelElements().get(2);
			final D d32 = (D) version.getProjectState().getModelElements().get(3);
			assertEquals(2, d31.getAtts().size());
			assertEquals(2, d32.getAtts().size());
		}
	}

	private static void assertOperationCount(final Version version, final int expected) {
		final ESCloseableIterable<AbstractOperation> operations = version.getChanges().operations();
		final Iterator<AbstractOperation> iterator = operations.iterable().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		operations.close();
		assertEquals(expected, count);
	}

	private static void fixFilePaths(ServerSpace serverSpace) {
		for (final Version version : serverSpace.getProjects().get(0).getVersions()) {
			final AbstractChangePackage changes = version.getChanges();
			if (!FileBasedChangePackage.class.isInstance(changes)) {
				continue;
			}
			final FileBasedChangePackage fileBasedChangePackage = FileBasedChangePackage.class.cast(changes);
			final URI fileURI = serverSpace.eResource().getResourceSet().getURIConverter()
				.normalize(fileBasedChangePackage.eResource().getURI());
			fileBasedChangePackage.setFilePath(fileURI.toFileString() + FileBasedChangePackageImpl.FILE_OP_INDEX);
		}
	}

	@Test
	public void testServerWithInMemoryChangePackage() throws FatalESException {
		/* setup */
		final String serverHome = copyServerToTempDir(true);
		final ResourceSet resourceSet = createResourceSet(serverHome);
		/* act */
		MigrationManager.migrate(resourceSet, true);
		/* assert */
		assertServerspace(loadServerSpace(serverHome));
	}

	@Test
	public void testServerWithFileBasedChangePackage() throws FatalESException {
		/* setup */
		final String serverHome = copyServerToTempDir(false);
		final ResourceSet resourceSet = createResourceSet(serverHome);
		/* act */
		MigrationManager.migrate(resourceSet, false);
		/* assert */
		final ServerSpace serverSpace = loadServerSpace(serverHome);
		fixFilePaths(serverSpace);
		assertServerspace(serverSpace);
	}

}
