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
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;

/**
 * @author emueller
 *
 */
public interface ACGroupDAO extends DAO {

	/**
	 * Add a group.
	 *
	 * @param group
	 *            the group to be added
	 */
	void add(ACGroup group);

	/**
	 * Returns all groups.
	 *
	 * @return a list containing all groups
	 */
	EList<ACGroup> getGroups();

	/**
	 * Removes a group.
	 *
	 * @param group
	 *            the group to be removed
	 */
	void remove(ACGroup group);

}
