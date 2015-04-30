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
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import org.eclipse.osgi.util.NLS;

/**
 * EMFStore sub-interfaces related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.server.core.subinterfaces.messages"; //$NON-NLS-1$
	public static String ChangePackageFragmentAdapter_SaveChangePackageFailed;
	public static String ChangePackageFragmentProviderAdapter_NoFragmentsFound;
	public static String ChangePackageFragmentUploadAdapter_ChangePackageAlreadyComplete;
	public static String ChangePackageFragmentUploadAdapter_NoChangePackageFragmentsFound;
	public static String EMFStorePropertiesSubInterfaceImpl_Project_Does_Not_Exist;
	public static String EMFStorePropertiesSubInterfaceImpl_Properties_Not_Set;
	public static String EPackageSubInterfaceImpl_Registration_Success_1;
	public static String EPackageSubInterfaceImpl_Registration_Success_2;
	public static String EPackageSubInterfaceImpl_RegistrationFailed_AlreadyRegistered;
	public static String EPackageSubInterfaceImpl_RegistrationFailed_Persistence;
	public static String FileTransferSubInterfaceImpl_File_Inaccessible;
	public static String FileTransferSubInterfaceImpl_Locate_Cache_Failed;
	public static String FileTransferSubInterfaceImpl_Locate_Tmp_Failed;
	public static String FileTransferSubInterfaceImpl_Move_Failed;
	public static String ProjectPropertiesSubInterfaceImpl_Property_Not_Set;
	public static String ProjectPropertiesSubInterfaceImpl_User_Does_Not_Exist;
	public static String ProjectSubInterfaceImpl_Null;
	public static String ProjectSubInterfaceImpl_ProjectDoesNotExist;
	public static String ProjectSubInterfaceImpl_ProjectResources_Not_Deleted;
	public static String ProjectSubInterfaceImpl_ProjectState_Not_Found;
	public static String VersionSubInterfaceImpl_BranchName_Reserved_1;
	public static String VersionSubInterfaceImpl_BranchName_Reserved_2;
	public static String VersionSubInterfaceImpl_ChangePackageFragmentProviderAdapterMissing;
	public static String VersionSubInterfaceImpl_ChecksumComputationFailed;
	public static String VersionSubInterfaceImpl_EmptyBranch_Not_Allowed;
	public static String VersionSubInterfaceImpl_FileBasedChangePackageNotAllowed;
	public static String VersionSubInterfaceImpl_Invalid_Source_Or_Target;
	public static String VersionSubInterfaceImpl_InvalidBranchOrVersion;
	public static String VersionSubInterfaceImpl_InvalidPath;
	public static String VersionSubInterfaceImpl_InvalidVersionRequested;
	public static String VersionSubInterfaceImpl_NextVersionInvalid;
	public static String VersionSubInterfaceImpl_ShuttingServerDown;
	public static String VersionSubInterfaceImpl_TargetBranchCombination_Invalid;
	public static String VersionSubInterfaceImpl_TotalTimeForCommit;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
