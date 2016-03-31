/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 * koegel
 * jfaltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.startup;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.common.ESSystemOutProgressMonitor;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrationException;
import org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrator;
import org.eclipse.emf.emfstore.internal.migration.EMFStoreMigratorUtil;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.Version;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.FileBasedChangePackageImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.Direction;
import org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent.SerializedOperationEmitter;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;
import org.eclipse.emf.emfstore.server.ESServerURIUtil;

import com.google.common.base.Optional;

/**
 * Applies migrator to files on server.
 *
 * @author koegel
 * @author wesendon
 * @author jfaltermeier
 */
@SuppressWarnings("restriction")
public final class MigrationManager {

	private MigrationManager() {
	}

	/**
	 * Performs a migration of the server space.
	 *
	 * @param resourceSet a resource set which contains all necessary converters/handler and may be safely used by the
	 *            migrator.
	 * @param inMemoryChangePackage <code>true</code> if server is configured to use in memory change packages,
	 *            <code>false</code> otherwise
	 * @throws FatalESException in case of a fatal state
	 */
	public static void migrate(ResourceSet resourceSet, boolean inMemoryChangePackage) throws FatalESException {
		/* check if migrator is available */
		if (!EMFStoreMigratorUtil.isMigratorAvailable()) {
			return;
		}

		/* get migrator */
		EMFStoreMigrator migrator = null;
		try {
			migrator = EMFStoreMigratorUtil.getEMFStoreMigrator();
		} catch (final EMFStoreMigrationException ex1) {
			throw new FatalESException(
				"No EMFStore Migrator could be created even though a migrator should be available."); //$NON-NLS-1$
		}

		/* load server space in order to access versions */
		final URI serverSpaceURI = ESServerURIUtil.createServerSpaceURI();
		final Resource resource = resourceSet.createResource(serverSpaceURI);
		try {
			resource.load(null);
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
			return;
		}
		final ServerSpace serverSpace = (ServerSpace) resource.getContents().get(0);

		/* get uris which might have to be migrated */
		final Set<URI> urisToMigrate = new LinkedHashSet<URI>();
		final Map<URI, List<URI>> fileBasedCPToTempFiles = new LinkedHashMap<URI, List<URI>>();
		fillURIsToMigrate(resourceSet.getURIConverter(), serverSpace, urisToMigrate, fileBasedCPToTempFiles,
			inMemoryChangePackage);

		/* check if all URIs may be handled by the migrator */
		final Set<URI> urisWhichCantBeHandled = migrator.canHandle(urisToMigrate);
		if (!urisWhichCantBeHandled.isEmpty()) {
			urisToMigrate.removeAll(urisWhichCantBeHandled);
			for (final URI uri : urisWhichCantBeHandled) {
				ModelUtil.logInfo(MessageFormat.format(
					"No migrator found for URI {0}. If a migration is needed for this URI you may encounter problems.", //$NON-NLS-1$
					uri));
			}
		}

		/* get URIs that actually need migration */
		final Set<URI> urisWhichNeedMigration = migrator.needsMigration(urisToMigrate);

		if (urisWhichNeedMigration.isEmpty()) {
			return;
		}

		/* perform the actual migration */
		try {
			migrator.migrate(urisWhichNeedMigration, new ESSystemOutProgressMonitor());
		} catch (final EMFStoreMigrationException ex) {
			ModelUtil.logException(ex);
		}

		/*
		 * fix file based changepackages (every operation was migrated on its own in a temp file. these need to be
		 * reassembled)
		 */
		fixFileBasedCPs(fileBasedCPToTempFiles);

	}

	private static void fixFileBasedCPs(final Map<URI, List<URI>> fileBasedCPToTempFiles) {
		for (final URI fileBasedCPURI : fileBasedCPToTempFiles.keySet()) {
			/* create a new file based changepackage and add all migrated operations to it */
			final FileBasedChangePackage changePackage = VersioningFactory.eINSTANCE.createFileBasedChangePackage();
			changePackage.initialize(fileBasedCPURI.toFileString());
			for (final URI tempOperationFileURI : fileBasedCPToTempFiles.get(fileBasedCPURI)) {
				try {
					final AbstractOperation operation = ModelUtil.loadEObjectFromResource(
						OperationsPackage.eINSTANCE.getAbstractOperation(),
						tempOperationFileURI, false);
					changePackage.add(operation);
				} catch (final IOException ex) {
					ModelUtil.logException(ex);
				}
			}

			/* copy temp file to regular file */
			try {
				FileUtils.copyFile(new File(changePackage.getTempFilePath()),
					new File(changePackage.getFilePath()));
			} catch (final IOException ex) {
				ModelUtil.logException(ex);
			}
		}
	}

	private static void fillURIsToMigrate(
		URIConverter uriConverter,
		final ServerSpace serverSpace,
		Set<URI> urisToMigrate,
		Map<URI, List<URI>> fileBasedCPToTempFiles,
		boolean inMemoryChangePackage) {
		/* loop over all versions and check if changepackage/projectstate resources are available */
		for (final ProjectHistory project : serverSpace.getProjects()) {
			for (final Version version : project.getVersions()) {
				final URI versionURI = EcoreUtil.getURI(version);
				final URI projectStateURI = ESServerURIUtil.createProjectStateURI(versionURI);
				if (uriConverter.exists(projectStateURI, null)) {
					urisToMigrate.add(uriConverter.normalize(projectStateURI));
				}
				final URI changePackageURI = ESServerURIUtil.createChangePackageURI(versionURI);
				if (!inMemoryChangePackage) {
					fillURIsToMigrateForFileBasedCP(uriConverter, changePackageURI, urisToMigrate,
						fileBasedCPToTempFiles);
				} else {
					if (uriConverter.exists(changePackageURI, null)) {
						urisToMigrate.add(uriConverter.normalize(changePackageURI));
					}
				}
			}
		}
	}

	private static void fillURIsToMigrateForFileBasedCP(
		URIConverter uriConverter,
		URI changePackageURI,
		Set<URI> urisToMigrate,
		Map<URI, List<URI>> fileBasedCPToTempFiles) {
		final URI normalizedFileBasedChangePackageURI = uriConverter.normalize(changePackageURI);
		final URI operationsFile = URI.createURI(
			normalizedFileBasedChangePackageURI.toString() + FileBasedChangePackageImpl.FILE_OP_INDEX);
		try {
			final File operationsJavaFile = new File(operationsFile.toFileString());
			if (!operationsJavaFile.exists()) {
				/* no file -> nothing to add */
				return;
			}
			fileBasedCPToTempFiles.put(operationsFile, new ArrayList<URI>());
			final SerializedOperationEmitter operationEmitter = new SerializedOperationEmitter(
				Direction.Forward, operationsJavaFile);
			Optional<String> operation = operationEmitter.tryEmit();
			while (operation.isPresent()) {
				final File tempFile = File.createTempFile("esMigration", ".xmi"); //$NON-NLS-1$//$NON-NLS-2$
				tempFile.deleteOnExit();
				FileUtils.writeStringToFile(tempFile,
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + operation.get(), //$NON-NLS-1$
					"UTF-8"); //$NON-NLS-1$
				final URI tempFileURI = URI.createFileURI(tempFile.getAbsolutePath());
				urisToMigrate.add(tempFileURI);
				fileBasedCPToTempFiles.get(operationsFile).add(tempFileURI);
				operation = operationEmitter.tryEmit();
			}
			operationEmitter.close();
		} catch (final IOException ex) {
			ModelUtil.logException(ex);
		}
	}
}
