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

import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;

/**
 * Provides access to known users and groups of EMFStore.
 *
 * @author emueller
 * @since 1.5
 *
 */
public interface ESOrgUnitProvider {

	/**
	 * Returns a set of all known users.
	 *
	 * @return a set of all known users.
	 */
	Set<ESUser> getUsers();

	/**
	 * Returns a set of all known groups.
	 *
	 * @return a set of all known groups.
	 */
	// TODO existieren wirklich
	Set<ESGroup> getGroups();

	/**
	 * @return
	 */
	EList<ProjectHistory> getProjects();
}
