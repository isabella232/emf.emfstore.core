/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.auth;

import java.util.List;

import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESRole;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * @author emueller
 * @since 1.5
 *
 */
public interface ESOrgUnitResolver {

	/**
	 * Resolves the user behind the given {@link ESAuthenticationInformation}.
	 *
	 * @param authInfo
	 *            the authentication information to be resolved
	 * @return the resolved {@link ESUser}
	 * @throws AccessControlException in case of missing access right
	 */
	ESUser resolveRoles(ESAuthenticationInformation authInfo) throws AccessControlException;

	/**
	 * Resolves the ID of an {@link ESUser}.
	 *
	 * @param orgUnitId
	 *            the organization unit ID to be resolved
	 * @return the resolved {@link ESUser}
	 * @throws AccessControlException in case of missing access right
	 */
	ESUser resolveUser(ESOrgUnitId orgUnitId) throws AccessControlException;

	/**
	 * Returns all roles that can be obtained via group membership.
	 *
	 * @param orgUnit
	 *            the organizational unit for which to determine the roles
	 * @return a list of {@link ESRole}s
	 */
	List<ESRole> getRolesFromGroups(ESOrgUnit orgUnit);

	/**
	 * Returns the group member of the organizational unit the given {@link ESOrgUnitId} belongs to.
	 *
	 * @param orgUnitId
	 *            the ID of an organizational unit
	 * @return a list of {@link ESGroup}s
	 */
	List<ESGroup> getGroups(ESOrgUnitId orgUnitId);

	/**
	 * Returns the group membership of the organizational unit.
	 *
	 * @param orgUnit
	 *            an organizational unit
	 * @return a list of {@link ESGroup}s
	 */
	List<ESGroup> getGroups(ESOrgUnit orgUnit);

	/**
	 * Resolves the given user and returns a copy of it with all roles.
	 *
	 * @param user
	 *            the user to be resolved
	 * @return a copy of the user with resolved roles
	 */
	ESUser copyAndResolveUser(ESUser user);

	/**
	 * Initializes this service.
	 *
	 * @param orgUnitProvider
	 *            the {@link ESOrgUnitProvider} for obtaining organizational units
	 */
	void init(ESOrgUnitProvider orgUnitProvider);
}
