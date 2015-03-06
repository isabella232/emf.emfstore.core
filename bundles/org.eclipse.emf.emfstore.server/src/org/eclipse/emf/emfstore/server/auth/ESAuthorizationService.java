/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.auth;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.Sessions;
import org.eclipse.emf.emfstore.internal.server.core.MethodInvocation;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.server.model.ESGlobalProjectId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESSessionId;

/**
 * @author Edgar
 * @since 1.5
 *
 */
public interface ESAuthorizationService {
	/**
	 * Check if the given session is valid.
	 *
	 * @param sessionId the session id
	 * @throws AccessControlException if the session is invalid
	 */
	void checkSession(ESSessionId sessionId) throws AccessControlException;

	/**
	 * Check if the session is valid for admin access to the given project.
	 *
	 * @param sessionId
	 *            the session id
	 * @param projectId
	 *            the project id. May be {@code null}
	 * @param privileg
	 *            the {@link ESProjectAdminPrivileges} to be checked
	 *
	 * @return {@code true}, if permission is granted via the server admin role, {@code false} otherwise
	 *
	 * @throws AccessControlException if the session is invalid for admin access
	 */
	boolean checkProjectAdminAccess(ESSessionId sessionId, ESGlobalProjectId projectId,
		ESProjectAdminPrivileges privileg)
			throws AccessControlException;

	/**
	 * Check if the session is valid for admin access to the given organizational unit.
	 *
	 * @param sessionId
	 *            the session id
	 * @param orgUnitId
	 *            the ID of an organizational unit
	 *
	 * @return {@code true}, if permission is granted via the server admin role, {@code false} otherwise
	 *
	 * @throws AccessControlException if the session is invalid for admin access
	 */
	boolean checkProjectAdminAccessForOrgUnit(ESSessionId sessionId, ESOrgUnitId orgUnitId)
		throws AccessControlException;

	/**
	 * Check if the session is valid for admin access to the given organizational unit.
	 *
	 * @param sessionId
	 *            the session id
	 * @param orgUnitId
	 *            the ID of an organizational unit
	 * @param projectIds
	 *            the set of {@link ProjectId}s for which to check access for
	 *
	 * @return {@code true}, if permission is granted via the server admin role, {@code false} otherwise
	 *
	 * @throws AccessControlException if the session is invalid for admin access
	 */
	boolean checkProjectAdminAccessForOrgUnit(ESSessionId sessionId, ESOrgUnitId orgUnitId,
		Set<ESGlobalProjectId> projectIds)
		throws AccessControlException;

	/**
	 * Check if the session is valid for admin access to the given project.
	 *
	 * @param sessionId
	 *            the session id
	 * @param projectId
	 *            the project id. May be {@code null}
	 *
	 * @return {@code true}, if permission is granted via the server admin role, {@code false} otherwise
	 *
	 * @throws AccessControlException if the session is invalid for admin access
	 */
	boolean checkProjectAdminAccess(ESSessionId sessionId, ESGlobalProjectId projectId)
		throws AccessControlException;

	/**
	 * Check if the session is valid for server admin access.
	 *
	 * @param sessionId the session id
	 * @throws AccessControlException if the session is invalid for server admin access
	 */
	void checkServerAdminAccess(ESSessionId sessionId) throws AccessControlException;

	/**
	 * Check if the session may read the given model elements in the project.
	 *
	 * @param sessionId session id
	 * @param projectId project id
	 * @param modelElements a set of model elements
	 * @throws AccessControlException if the session may not read any of the model elements
	 */
	void checkReadAccess(ESSessionId sessionId, ESGlobalProjectId projectId, Set<EObject> modelElements)
		throws AccessControlException;

	/**
	 * Check if the session may write the given model elements in the project.
	 *
	 * @param sessionId session id
	 * @param projectId project id
	 * @param modelElements a set of model elements
	 * @throws AccessControlException if the session may not write any of the model elements
	 */
	void checkWriteAccess(ESSessionId sessionId, ESGlobalProjectId projectId, Set<EObject> modelElements)
		throws AccessControlException;

	/**
	 * @param methodInvocation
	 */
	// TODO:
	void checkAccess(MethodInvocation methodInvocation) throws AccessControlException;

	/**
	 * @param sessions
	 * @param orgUnitResolverServive
	 * @param orgUnitProvider
	 */
	void init(Sessions sessions, ESOrgUnitResolver orgUnitResolverServive, ESOrgUnitProvider orgUnitProvider);

	/**
	 * This method looks up the session id on the server and returns the relating user. Please notice that the returned
	 * user also contains roles which are not contained in the original user. These extra roles come from the user's
	 * groups.
	 *
	 * @param sessionId session id
	 * @return ACUser user with roles from resolved user and it's groups
	 * @throws AccessControlException exception
	 */
	// ACUser resolveUser(SessionId sessionId) throws AccessControlException;

	/**
	 * This method looks up the orgUnit id the server and returns the relating user. Please notice that the returned
	 * user also contains roles which are not contained in the original user. These extra roles come from the user's
	 * groups.
	 *
	 * @param orgUnitId OrgUnit id
	 * @return ACUser user with roles from resolved user and it's groups
	 * @throws AccessControlException exception
	 */
	// ACUser resolveUser(ACOrgUnitId orgUnitId) throws AccessControlException;

	/**
	 * Checks whether a given operation may be executed.
	 *
	 * @param op
	 *            the operation the user intends to execute
	 *
	 * @throws AccessControlException in case access is denied
	 */
	// void checkAccess(MethodInvocation op) throws AccessControlException;

}
