/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.migration;

import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPointException;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;

/**
 * Controller for migrating models in EMFStore. Manages all registered migrators.
 * 
 * @author koegel
 */
public final class EMFStoreMigratorUtil {

	private static final String MIGRATOR_CLASS = "migratorClass"; //$NON-NLS-1$
	private static EMFStoreMigrator migrator;

	private EMFStoreMigratorUtil() {
		// private constructor of utility class
	}

	/**
	 * Check if any migrators are registered.
	 * 
	 * @return true, if migrators are available.
	 */
	public static boolean isMigratorAvailable() {
		if (migrator != null) {
			return true;
		}
		try {
			migrator = loadMigrator();
		} catch (final EMFStoreMigrationException e) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieve a migrator. Will default to the first loadable migrator if multiple migrators are available.
	 * 
	 * @return the migrator
	 * @throws EMFStoreMigrationException if no migrators are available or can be loaded.
	 */
	public static EMFStoreMigrator getEMFStoreMigrator() throws EMFStoreMigrationException {

		if (migrator != null) {
			return migrator;
		}
		return loadMigrator();
	}

	private static EMFStoreMigrator loadMigrator() throws EMFStoreMigrationException {
		final ESExtensionPoint extensionPoint = new ESExtensionPoint("org.eclipse.emf.emfstore.migration.migrator", //$NON-NLS-1$
			true);
		if (extensionPoint.size() > 1) {
			ModelUtil
				.logWarning(Messages.EMFStoreMigratorUtil_MultipMigratorsFound);
		}
		try {
			return extensionPoint.getFirst().getClass(MIGRATOR_CLASS, EMFStoreMigrator.class);
		} catch (final ESExtensionPointException e) {
			throw new EMFStoreMigrationException(Messages.EMFStoreMigratorUtil_NoMigratorFournd);
		}
	}

}
