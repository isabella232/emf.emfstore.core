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
 * @author Edgar
 * @since 1.5
 *
 */
// TODO: javadoc
public interface ESOrgUnitResolver {

	ESUser resolveRoles(ESAuthenticationInformation authInfo) throws AccessControlException;

	ESUser resolve(ESOrgUnitId orgUnitId) throws AccessControlException;

	List<ESRole> getRolesFromGroups(ESOrgUnit orgUnit);

	List<ESGroup> getGroups(ESOrgUnitId orgUnitId);

	List<ESGroup> getGroups(ESOrgUnit orgUnit);

	/**
	 * @param api
	 * @return
	 */
	ESUser copyAndResolveUser(ESUser api);

	/**
	 * @param orgUnitProvider
	 */
	void init(ESOrgUnitProvider orgUnitProvider);
}
