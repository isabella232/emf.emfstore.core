/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.dao;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;

/**
 * @author emueller
 *
 */
public interface ACUserDAO extends DAO {

	/**
	 * Add a user.
	 *
	 * @param user
	 *            the user to be added
	 */
	void add(ACUser user);

	/**
	 * Returns all users.
	 *
	 * @return a list containing all users
	 */
	EList<ACUser> getUsers();

	/**
	 * Removes a user.
	 *
	 * @param user
	 *            the user to be removed
	 */
	void remove(ACUser user);

}
