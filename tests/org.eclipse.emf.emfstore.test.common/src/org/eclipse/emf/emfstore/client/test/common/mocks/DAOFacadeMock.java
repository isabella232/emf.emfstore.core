/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.common.mocks;

import java.io.IOException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;

public class DAOFacadeMock implements ACDAOFacade {

	private final EList<ACUser> users;
	private final EList<ACGroup> groups;
	private final EList<ProjectHistory> projects;

	public DAOFacadeMock() {
		users = new BasicEList<ACUser>();
		groups = new BasicEList<ACGroup>();
		projects = new BasicEList<ProjectHistory>();
	}

	public EList<ACUser> getUsers() {
		return users;
	}

	public EList<ACGroup> getGroups() {
		return groups;
	}

	public void add(ACUser user) {
		users.add(user);
	}

	public void add(ACGroup group) {
		groups.add(group);
	}

	public void remove(ACUser user) {
		users.remove(user);
	}

	public void remove(ACGroup group) {
		groups.remove(group);
	}

	public void save() throws IOException {

	}

	public void add(ProjectHistory history) {
		projects.add(history);
	}

	public EList<ProjectHistory> getProjects() {
		return projects;
	}

	public void remove(ProjectHistory history) {
		projects.remove(history);
	}

}
