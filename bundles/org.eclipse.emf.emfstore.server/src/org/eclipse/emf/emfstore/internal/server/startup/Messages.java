/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.startup;

import org.eclipse.osgi.util.NLS;

/**
 * @author emueller
 * @generated
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.server.startup.messages"; //$NON-NLS-1$
	public static String EmfStoreValidator_ChangeOperation_Has_No_ModelElementId;
	public static String EmfStoreValidator_CheckingModelElementIds;
	public static String EmfStoreValidator_CheckingProject;
	public static String EmfStoreValidator_Errors;
	public static String EmfStoreValidator_ModelElement_Has_No_ModelElementId;
	public static String EmfStoreValidator_ProjectGenerationCompare;
	public static String EmfStoreValidator_ProjectVersionCompareFailed;
	public static String EmfStoreValidator_ResolvingAllElements;
	public static String EmfStoreValidator_Validation;
	public static String EmfStoreValidator_ValidationDuration;
	public static String EmfStoreValidator_ValidationFailed;
	public static String ServerHrefMigrator_BackupFailed;
	public static String ServerHrefMigrator_ErrorDuringBackup;
	public static String ServerHrefMigrator_ErrorDuringMigration;
	public static String ServerHrefMigrator_HistoryFileDoeNotExists;
	public static String ServerHrefMigrator_MigrationFailed;
	public static String ServerHrefMigrator_SkipMigration;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
