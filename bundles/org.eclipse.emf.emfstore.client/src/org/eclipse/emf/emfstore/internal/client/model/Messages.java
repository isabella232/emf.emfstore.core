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
package org.eclipse.emf.emfstore.internal.client.model;

import org.eclipse.osgi.util.NLS;

/**
 * Messages related to internal model usage of the client.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.client.model.messages"; //$NON-NLS-1$
	public static String ESWorkspaceProviderImpl_Create_Workspace_Failed;
	public static String ESWorkspaceProviderImpl_Migration_Failed;
	public static String ESWorkspaceProviderImpl_Migration_Of_Project_Failed;
	public static String ESWorkspaceProviderImpl_ModelElement_Has_No_Project;
	public static String ESWorkspaceProviderImpl_ModelElement_Is_Null;
	public static String ESWorkspaceProviderImpl_Project_Is_Null;
	public static String ESWorkspaceProviderImpl_Project_Not_Contained_By_ProjectSpace;
	public static String ESWorkspaceProviderImpl_Workspace_Loading_Failed;
	public static String ESWorkspaceProviderImpl_WorkspaceInit_Failed;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
