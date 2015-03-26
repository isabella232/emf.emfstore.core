/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller, Neil Mackenzie - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;

/**
 * This class maintains a mapping for each workspace provider to its ID.
 */
public final class WorkspaceLocator {

	private WorkspaceLocator() {

	}

	private static final Map<String, ESWorkspaceProviderImpl> WORKSPACE_PROVIDER_MAP =
		new LinkedHashMap<String, ESWorkspaceProviderImpl>();

	/**
	 * Creates a workspace and associates it with the given ID.
	 *
	 * @param workspaceProviderId
	 *            the identifier that will be associated with the workspace
	 * @return the created workspace
	 */
	public static ESWorkspaceProviderImpl createWorkspaceProviderFor(String workspaceProviderId) {
		final ESWorkspaceProviderImpl ws = createWorkspace(workspaceProviderId);
		WORKSPACE_PROVIDER_MAP.put(workspaceProviderId, ws);
		return ws;
	}

	/**
	 * Creates a workspace with a particular ID.
	 *
	 * @param workspaceProviderId
	 *            the workspace identifier
	 * @return the created workspace
	 */
	private static ESWorkspaceProviderImpl createWorkspace(String workspaceProviderId) {
		final ESWorkspaceProviderImpl ws = new ESWorkspaceProviderImpl();
		ws.setName(workspaceProviderId);
		try {
			ws.initialize();
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final RuntimeException e) {
			// END SURPRESS CATCH EXCEPTION
			ModelUtil.logException(Messages.ESWorkspaceProviderImpl_WorkspaceInit_Failed, e);
			throw e;
		}
		ws.notifyPostWorkspaceInitiators();
		return ws;
	}

	/**
	 * Checks whether a workspace provier exists for the given identifier.
	 *
	 * @param workspaceProviderId
	 *            the workspace provider identifier
	 * @return the resulting boolean
	 */
	public static boolean hasId(String workspaceProviderId) {
		for (final String id : WORKSPACE_PROVIDER_MAP.keySet()) {
			if (id.equals(workspaceProviderId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieves the workspace associated with the given ID.
	 * If no such workspace can be found, one will be created.
	 *
	 * @param workspaceProviderId
	 *            the workspace ID
	 * @return the associated workspace provider, if any
	 */
	public static ESWorkspaceProviderImpl getWorkspaceById(String workspaceProviderId) {
		for (final Map.Entry<String, ESWorkspaceProviderImpl> entry : WORKSPACE_PROVIDER_MAP.entrySet()) {
			final String id = entry.getKey();
			if (id.equals(workspaceProviderId)) {
				return entry.getValue();
			}
		}

		return createWorkspaceProviderFor(workspaceProviderId);
	}
}
