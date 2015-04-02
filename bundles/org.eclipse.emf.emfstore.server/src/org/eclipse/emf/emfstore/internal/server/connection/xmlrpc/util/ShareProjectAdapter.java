/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc.util;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;

/**
 * Utility class that keeps track of projects that a given session has shared.
 *
 * @author emueller
 *
 */
public final class ShareProjectAdapter extends AdapterImpl {

	private final Set<ProjectId> projectIds;

	private ShareProjectAdapter() {
		projectIds = new LinkedHashSet<ProjectId>();
	}

	/**
	 * Add a {@link ProjectId}.
	 *
	 * @param projectId
	 *            the {@link ProjectId} to be added
	 */
	public void addProjectId(ProjectId projectId) {
		projectIds.add(ModelUtil.clone(projectId));
	}

	/**
	 * Remove a {@link ProjectId}.
	 *
	 * @param projectId
	 *            the {@link ProjectId} to be removed
	 * @return {@code true}, if a project has been removed, {@code false} otherwise
	 */
	public boolean removeProject(ProjectId projectId) {
		return projectIds.remove(projectId);
	}

	/**
	 * Tries to retrieve a {@link ShareProjectAdapter} from the given {@link SessionId}.
	 *
	 * @param sessionId
	 *            the {@link SessionId} from which to retrieve the adapter
	 * @return the adapter, if any, otherwise {@code null}
	 */
	private static ShareProjectAdapter find(SessionId sessionId) {
		final EList<Adapter> eAdapters = sessionId.eAdapters();
		for (final Adapter adapter : eAdapters) {
			if (ShareProjectAdapter.class.isInstance(adapter)) {
				return (ShareProjectAdapter) adapter;
			}
		}

		return null;
	}

	/**
	 * Creates a {@link ShareProjectAdapter} and attaches it to the given {@link SessionId}.
	 *
	 * @param sessionId
	 *            the {@link SessionId} the adapter should be attached to
	 * @return the created adapter
	 */
	private static ShareProjectAdapter create(SessionId sessionId) {
		ShareProjectAdapter adapter;
		adapter = new ShareProjectAdapter();
		sessionId.eAdapters().add(adapter);
		return adapter;
	}

	/**
	 * Attaches an adapter with the given {@link ProjectId} to the session.
	 *
	 * @param sessionId
	 *            the session the adapter should be attached to
	 * @param projectId
	 *            the project id that should be attached to the session
	 */
	public static void attachTo(SessionId sessionId, final ProjectId projectId) {
		ShareProjectAdapter adapter = find(sessionId);
		if (adapter == null) {
			adapter = ShareProjectAdapter.create(sessionId);
		}
		adapter.addProjectId(projectId);
	}
}
