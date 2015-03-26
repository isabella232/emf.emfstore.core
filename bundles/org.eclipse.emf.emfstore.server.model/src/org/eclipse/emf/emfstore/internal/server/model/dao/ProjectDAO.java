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
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;

/**
 * @author emueller
 *
 */
public interface ProjectDAO {

	/**
	 * Add a project history.
	 *
	 * @param history
	 *            the history to be added
	 */
	void add(ProjectHistory history);

	/**
	 * Returns all project histories.
	 *
	 * @return a list containing all project histories
	 */
	EList<ProjectHistory> getProjects();

	/**
	 * Removes a project history.
	 *
	 * @param history
	 *            the project history to be removed
	 */
	void remove(ProjectHistory history);

}
