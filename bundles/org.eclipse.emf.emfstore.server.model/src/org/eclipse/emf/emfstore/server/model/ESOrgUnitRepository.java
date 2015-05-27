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
package org.eclipse.emf.emfstore.server.model;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Repository for organizational units.
 *
 * @author emueller
 * @since 1.5
 *
 */
public interface ESOrgUnitRepository {

	/**
	 * Returns a set of all available {@link ESUser}s.
	 *
	 * @return a set of users
	 */
	Set<ESUser> getUsers();

	/**
	 * Returns a set of all available {@link ESGroup}s.
	 *
	 * @return a set of groups
	 */
	Set<ESGroup> getGroups();

	/**
	 * Returns a list of all available project histories.
	 *
	 * @return a list of {@link ESProjectHistory}
	 */
	List<ESProjectHistory> getProjects();

	/**
	 * Removes the given group.
	 *
	 * @param group
	 *            the group to be removed
	 */
	void removeGroup(ESGroup group);

	/**
	 * Removes the given user.
	 *
	 * @param user
	 *            the user to be removed
	 */
	void removeUser(ESUser user);

	/**
	 * Adds the given user.
	 *
	 * @param user
	 *            the user to be added
	 */
	void addUser(ESUser user);

	/**
	 * Adds the given group.
	 *
	 * @param group
	 *            the group to be added
	 */
	void addGroup(ESGroup group);

	/**
	 * Saves the state of the repository.
	 * Depending on the actual implementation this method may remain empty.
	 * This method exists mainly for performance reasons.
	 *
	 * @throws IOException in case saving fails
	 */
	void save() throws IOException;
}