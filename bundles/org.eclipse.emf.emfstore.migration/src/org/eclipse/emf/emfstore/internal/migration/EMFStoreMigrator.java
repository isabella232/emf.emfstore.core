/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * koegel
 * jfaltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.migration;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;

/**
 * Migrates models in the given URIs to the most recent version. Users should check if this migrator can handle the
 * given URI by calling canHandle, then checking if a migration is actually needed by calling needsMigration and if so
 * use the migrate-Method to do so.
 *
 * @author koegel
 * @author jfaltermeier
 *
 */
public interface EMFStoreMigrator {

	/**
	 * Checks if this migrator can work with the specified URIs.
	 *
	 * @param uris the physical URIs
	 * @return a set of URIs which <b>cannot</b> be handled. The set is empty of all uris may be handled
	 */
	Set<URI> canHandle(Set<URI> uris);

	/**
	 * Checks whether the models in the specified URIs need a to be migrated.
	 *
	 * @param uris the physical URIs
	 * @return the URIs which <b>need</b> to be migrated. The set is empty if no URIs need to be migrated
	 */
	Set<URI> needsMigration(Set<URI> uris);

	/**
	 * Migrate the models in the given URIs from the given source version to the most recent version.
	 *
	 * @param resources the URIs of the contents to migrate
	 * @param monitor a progress monitor
	 *
	 * @throws EMFStoreMigrationException if the migration fails.
	 */
	void migrate(Set<URI> resources, IProgressMonitor monitor)
		throws EMFStoreMigrationException;

}
