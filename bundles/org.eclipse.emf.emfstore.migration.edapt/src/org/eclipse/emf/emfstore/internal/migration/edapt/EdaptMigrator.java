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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edapt.migration.MigrationException;
import org.eclipse.emf.edapt.migration.ReleaseUtils;
import org.eclipse.emf.edapt.migration.execution.Migrator;
import org.eclipse.emf.edapt.migration.execution.MigratorRegistry;
import org.eclipse.emf.edapt.spi.history.Release;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrationException;
import org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrator;

/**
 * {@link EMFStoreMigrator} using Edapt.
 *
 * @author Johannes Faltermeier
 *
 */
public class EdaptMigrator implements EMFStoreMigrator {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrator#canHandle(java.util.Set)
	 */
	public Set<URI> canHandle(Set<URI> uris) {
		final Set<URI> result = new LinkedHashSet<URI>();
		for (final URI uri : uris) {
			if (uri.isFile()) {
				continue;
			}
			result.add(uri);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrator#needsMigration(java.util.Set)
	 */
	public Set<URI> needsMigration(Set<URI> uris) {
		final Set<URI> result = new LinkedHashSet<URI>();

		final Map<Set<String>, Boolean> nsURIsToMigrationStateMap = new LinkedHashMap<Set<String>, Boolean>();
		for (final URI uri : uris) {
			final Set<String> nsURIs = ReleaseUtils.getAllNamespaceURIsFromPrefixes(uri);
			if (nsURIs.isEmpty()) {
				continue;
			}

			if (nsURIsToMigrationStateMap.containsKey(nsURIs)) {
				if (nsURIsToMigrationStateMap.get(nsURIs)) {
					result.add(uri);
				}
				continue;
			}

			final Migrator migrator = getMigrator(nsURIs);
			if (migrator == null) {
				nsURIsToMigrationStateMap.put(nsURIs, false);
				continue;
			}
			final Set<Release> releases = migrator.getRelease(nsURIs);
			final Release newestRelease = getNewestRelease(releases);
			final boolean migrationNeeded = !newestRelease.isLatestRelease();
			nsURIsToMigrationStateMap.put(nsURIs, migrationNeeded);
			if (migrationNeeded) {
				result.add(uri);
			}
		}
		return result;
	}

	/**
	 * Returns a {@link Migrator} which can handle the given namespace URIs.
	 *
	 * @param nameSpaceURIs the uris
	 * @return the {@link Migrator} or null if non was found
	 */
	static Migrator getMigrator(Set<String> nameSpaceURIs) {
		Migrator migrator = null;
		for (final String nsURI : nameSpaceURIs) {
			final Migrator candidate = MigratorRegistry.getInstance().getMigrator(nsURI);
			if (candidate == null) {
				continue;
			}
			if (migrator == null) {
				migrator = candidate;
				continue;
			}
			if (migrator != candidate) {
				/* multiple migrators found */
				ModelUtil.logWarning(MessageFormat
					.format("For the given name-space URIs {0} multiple Edapt Migrators were found.", nameSpaceURIs)); //$NON-NLS-1$
				return null;
			}
		}
		return migrator;
	}

	/**
	 * Returns the newest Release from the given set. It is expected that all releases are from the same history.
	 *
	 * @param releases the releases
	 * @return the newest release
	 */
	static Release getNewestRelease(Set<Release> releases) {
		Release sourceRelease = null;
		for (final Release release : releases) {
			if (sourceRelease == null) {
				sourceRelease = release;
				continue;
			}
			if (release.getNumber() > sourceRelease.getNumber()) {
				sourceRelease = release;
			}
		}
		return sourceRelease;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.migration.EMFStoreMigrator#migrate(java.util.Set,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void migrate(Set<URI> resources, IProgressMonitor monitor) throws EMFStoreMigrationException {
		monitor.beginTask("Migrating resources", resources.size()); //$NON-NLS-1$
		for (final URI uri : resources) {
			final Set<String> nsURIs = ReleaseUtils.getAllNamespaceURIsFromPrefixes(uri);
			final Migrator migrator = getMigrator(nsURIs);
			final Set<Release> releases = migrator.getRelease(nsURIs);
			final Release sourceRelease = getNewestRelease(releases);
			try {
				migrator.migrateAndSave(Collections.singletonList(uri), sourceRelease, null, monitor,
					getResourceSaveOptions());
				ModelUtil.logInfo(MessageFormat.format("Migrating file with URI {0} successfull.", uri)); //$NON-NLS-1$
			} catch (final MigrationException ex) {
				ModelUtil.logInfo(MessageFormat.format("Migrating file with URI {0} failed.", uri)); //$NON-NLS-1$
				throw new EMFStoreMigrationException("Exception during migration", ex); //$NON-NLS-1$
			}
			monitor.worked(1);
		}
	}

	private static Map<String, Object> getResourceSaveOptions() {
		final Map<String, Object> result = new LinkedHashMap<String, Object>();
		final Map<Object, Object> resourceSaveOptions = ModelUtil.getResourceSaveOptions();
		for (final Object key : resourceSaveOptions.keySet()) {
			if (!String.class.isInstance(key)) {
				continue;
			}
			result.put((String) key, resourceSaveOptions.get(key));
		}
		return result;
	}

}
