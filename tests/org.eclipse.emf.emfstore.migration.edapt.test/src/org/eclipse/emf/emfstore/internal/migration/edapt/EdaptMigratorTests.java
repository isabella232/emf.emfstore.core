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
package org.eclipse.emf.emfstore.internal.migration.edapt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edapt.migration.execution.Migrator;
import org.eclipse.emf.edapt.spi.history.History;
import org.eclipse.emf.edapt.spi.history.HistoryFactory;
import org.eclipse.emf.edapt.spi.history.Release;
import org.eclipse.emf.emfstore.common.ESSystemOutProgressMonitor;
import org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrationException;
import org.junit.After;
import org.junit.Test;

public class EdaptMigratorTests {

	private static final String CHANGEPACKAGE_1_UCP = "changepackage-1.ucp";
	private static final String PROJECTSTATE_0_UPS = "projectstate-0.ups";
	private static final String PROJECTSTATE_1_UPS = "projectstate-1.ups";
	private static final String EXPECTED_XMI = "expected.xmi";

	private static final String A_NSURI_REL_1 = "http://eclipse.org/emf/emfstore/migration/edapt/test/a";
	private static final String B_NSURI_REL_1 = "http://eclipse.org/emf/emfstore/migration/edapt/test/b";
	private static final String C_NSURI_REL_1 = "http://eclipse.org/emf/emfstore/migration/edapt/test/c";

	private static Set<File> tempFiles = new LinkedHashSet<File>();

	@After
	public void cleanup() {
		for (final File file : tempFiles) {
			file.delete();
		}
		tempFiles.clear();
	}

	private static URI copyFileToTempDir(int testDir, String fileName) {
		try {
			final InputStream inputStream = getInputStream(testDir, fileName);
			final File tempFile = File.createTempFile("emfstore", ".xmi");
			tempFiles.add(tempFile);
			tempFile.deleteOnExit();
			final OutputStream outputStream = new FileOutputStream(tempFile);
			final byte[] buf = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
			return URI.createFileURI(tempFile.getAbsolutePath());
		} catch (final FileNotFoundException ex) {
		} catch (final IOException ex) {
		}
		fail();
		return null;
	}

	private static InputStream getInputStream(int testDir, String fileName) {
		try {
			final URL url = new URL(MessageFormat
				.format("platform:/plugin/org.eclipse.emf.emfstore.migration.edapt.test/data/{0}/{1}", testDir,
					fileName));
			final InputStream inputStream = url.openConnection().getInputStream();
			return inputStream;
		} catch (final MalformedURLException ex) {
		} catch (final IOException ex) {
		}
		fail();
		return null;
	}

	private static void assertStreamEqual(final URI uri, int testDir, String fileName) {
		try {
			final InputStream inputStream1 = getInputStream(testDir, fileName);
			final FileInputStream inputStream2 = new FileInputStream(uri.toFileString());
			assertTrue(IOUtils.contentEquals(inputStream1, inputStream2));
			inputStream1.close();
			inputStream2.close();
		} catch (final FileNotFoundException ex) {
			fail(ex.getMessage());
		} catch (final IOException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testCanHandle() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI fileURI = URI.createFileURI("foo");
		final URI nonFileURI = URI.createPlatformPluginURI("bar", false);
		final LinkedHashSet<URI> urisToCheck = new LinkedHashSet<URI>(Arrays.asList(fileURI, nonFileURI));

		/* act */
		final Set<URI> result = edaptMigrator.canHandle(urisToCheck);

		/* assert */
		assertEquals(1, result.size());
		assertSame(nonFileURI, result.iterator().next());
	}

	@Test
	public void testGetMigratorWithNullMigrator() {
		/* setup */
		final LinkedHashSet<String> nameSpaceURIs = new LinkedHashSet<String>(
			Arrays.asList(A_NSURI_REL_1, "foobar"));

		/* act */
		final Migrator migrator = EdaptMigrator.getMigrator(nameSpaceURIs);

		/* assert */
		assertNotNull(migrator);
	}

	@Test
	public void testGetMigratorWithMultipleMigrators() {
		/* setup */
		final LinkedHashSet<String> nameSpaceURIs = new LinkedHashSet<String>(
			Arrays.asList(A_NSURI_REL_1, B_NSURI_REL_1));

		/* act */
		final Migrator migrator = EdaptMigrator.getMigrator(nameSpaceURIs);

		/* assert */
		assertNull(migrator);
	}

	@Test
	public void testGetMigratorWithMultipleURIsSameMigrators() {
		/* setup */
		final LinkedHashSet<String> nameSpaceURIs = new LinkedHashSet<String>(
			Arrays.asList(B_NSURI_REL_1, C_NSURI_REL_1));

		/* act */
		final Migrator migrator = EdaptMigrator.getMigrator(nameSpaceURIs);

		/* assert */
		assertNotNull(migrator);
	}

	@Test
	public void testGetNewestRelease() {
		/* setup */
		final History history = HistoryFactory.eINSTANCE.createHistory();
		final Release release1 = HistoryFactory.eINSTANCE.createRelease();
		final Release release2 = HistoryFactory.eINSTANCE.createRelease();
		final Release release3 = HistoryFactory.eINSTANCE.createRelease();
		final Release release4 = HistoryFactory.eINSTANCE.createRelease();
		final Release release5 = HistoryFactory.eINSTANCE.createRelease();
		history.getReleases().addAll(Arrays.asList(release1, release2, release3, release4, release5));

		/* act */
		final Release newestRelease = EdaptMigrator
			.getNewestRelease(new LinkedHashSet<Release>(Arrays.asList(release3, release2, release4)));

		/* assert */
		assertSame(release4, newestRelease);
	}

	@Test
	public void testNeedsMigrationRelease1ProjectState() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri = copyFileToTempDir(1, PROJECTSTATE_1_UPS);

		/* act */
		final Set<URI> result = edaptMigrator.needsMigration(Collections.singleton(uri));

		/* assert */
		assertEquals(1, result.size());
		assertSame(uri, result.iterator().next());
	}

	@Test
	public void testNeedsMigrationRelease1ChangePackage() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri = copyFileToTempDir(2, CHANGEPACKAGE_1_UCP);

		/* act */
		final Set<URI> result = edaptMigrator.needsMigration(Collections.singleton(uri));

		/* assert */
		assertEquals(1, result.size());
		assertSame(uri, result.iterator().next());
	}

	@Test
	public void testNeedsMigrationRelease2ProjectState() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri = copyFileToTempDir(3, PROJECTSTATE_1_UPS);

		/* act */
		final Set<URI> result = edaptMigrator.needsMigration(Collections.singleton(uri));

		/* assert */
		assertEquals(0, result.size());
	}

	@Test
	public void testNeedsMigrationRelease2ChangePackage() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri = copyFileToTempDir(4, CHANGEPACKAGE_1_UCP);

		/* act */
		final Set<URI> result = edaptMigrator.needsMigration(Collections.singleton(uri));

		/* assert */
		assertEquals(0, result.size());
	}

	@Test
	public void testNeedsMigrationMultipleURIsSameMigrator() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri1 = copyFileToTempDir(1, PROJECTSTATE_1_UPS);
		final URI uri2 = copyFileToTempDir(1, PROJECTSTATE_1_UPS);
		final URI uri3 = copyFileToTempDir(3, PROJECTSTATE_1_UPS);
		final URI uri4 = copyFileToTempDir(3, PROJECTSTATE_1_UPS);

		/* act */
		final Set<URI> result = edaptMigrator
			.needsMigration(new LinkedHashSet<URI>(Arrays.asList(uri1, uri2, uri3, uri4)));

		/* assert */
		assertEquals(2, result.size());
		assertTrue(result.contains(uri1));
		assertTrue(result.contains(uri2));
	}

	@Test
	public void testNeedsMigrationNoMigrator() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri1 = copyFileToTempDir(5, PROJECTSTATE_0_UPS);

		/* act */
		final Set<URI> result = edaptMigrator.needsMigration(new LinkedHashSet<URI>(Arrays.asList(uri1)));

		/* assert */
		assertEquals(0, result.size());
	}

	@Test
	public void testNeedsMigrationNoNSURIs() {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final URI uri1 = URI.createFileURI("foobar");

		/* act */
		final Set<URI> result = edaptMigrator.needsMigration(new LinkedHashSet<URI>(Arrays.asList(uri1)));

		/* assert */
		assertEquals(0, result.size());
	}

	@Test
	public void testMigrateProjectState() throws EMFStoreMigrationException {
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final ESSystemOutProgressMonitor monitor = new ESSystemOutProgressMonitor();
		final URI uri = copyFileToTempDir(1, PROJECTSTATE_1_UPS);

		/* act */
		edaptMigrator.migrate(Collections.singleton(uri), monitor);

		/* assert */
		assertStreamEqual(uri, 1, EXPECTED_XMI);
	}

	@Test
	public void testMigrateChangePackage() throws EMFStoreMigrationException {
		/* this test is nice-to-have only since it involves the changepackage migration */
		/* setup */
		final EdaptMigrator edaptMigrator = new EdaptMigrator();
		final ESSystemOutProgressMonitor monitor = new ESSystemOutProgressMonitor();
		final URI uri = copyFileToTempDir(2, CHANGEPACKAGE_1_UCP);

		/* act */
		edaptMigrator.migrate(Collections.singleton(uri), monitor);

		/* assert */
		// TODO id changes, but for change packages it is only important that they may be loaded again
		/* assertStreamEqual(uri, 2, EXPECTED_XMI); */
	}

	// TODO
	/* setup */

	/* act */

	/* assert */

}
