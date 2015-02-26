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
package org.eclipse.emf.emfstore.internal.client.model.controller;

import org.eclipse.osgi.util.NLS;

/**
 * Controllers related messages.
 * 
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.model.controller.messages"; //$NON-NLS-1$
	public static String CommitController_BranchAlreadyExists;
	public static String CommitController_CheckingChanges;
	public static String CommitController_ChecksumComputationFailed;
	public static String CommitController_CommitCancelled_InvalidChecksum;
	public static String CommitController_CommitingChanges;
	public static String CommitController_ComputingChecksum;
	public static String CommitController_EmptyBranchName;
	public static String CommitController_FinalizingCommit;
	public static String CommitController_GatheringChanges;
	public static String CommitController_InvalidChecksum;
	public static String CommitController_NoMessage;
	public static String CommitController_PresentingChanges;
	public static String CommitController_ResolvingNewVersion;
	public static String CommitController_SendingChangesToServer;
	public static String CommitController_SendingFilesToServer;
	public static String ShareController_Finalizing_Share;
	public static String ShareController_Initial_Commit;
	public static String ShareController_Preparing_Share;
	public static String ShareController_Project_At;
	public static String ShareController_Settings_Attributes;
	public static String ShareController_Sharing_Project;
	public static String ShareController_Sharing_Project_With_Server;
	public static String ShareController_Uploading_Files;
	public static String UpdateController_ApplyingChanges;
	public static String UpdateController_ChangePackagesRemoved;
	public static String UpdateController_CheckingForConflicts;
	public static String UpdateController_ConflictsDetected;
	public static String UpdateController_FetchingChanges;
	public static String UpdateController_IncomingOperationsNotFullyConsumed;
	public static String UpdateController_IncomingOperationsOnlyPartlyMatch;
	public static String UpdateController_IncomingOpsNotConsumed;
	public static String UpdateController_IncomingOpsPartlyMatched;
	public static String UpdateController_LocalChanges_SaveFailed;
	public static String UpdateController_ProjectSpace_SaveFailed;
	public static String UpdateController_PullingUpBaseVersion;
	public static String UpdateController_ResolvingNewVersion;
	public static String UpdateController_TempFileCreationFailed;
	public static String UpdateController_UpdatingProject;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
