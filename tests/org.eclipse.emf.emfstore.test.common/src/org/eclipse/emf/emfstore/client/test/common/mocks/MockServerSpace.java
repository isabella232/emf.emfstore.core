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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;
import org.eclipse.emf.emfstore.internal.server.model.impl.ServerSpaceImpl;

public class MockServerSpace extends ServerSpaceImpl {

	private final ACDAOFacade daoFacade;

	public MockServerSpace(ACDAOFacade daoFacade) {
		this.daoFacade = daoFacade;
	}

	@Override
	public void add(ACUser user) {
		daoFacade.add(user);
	}

	@Override
	public EList<ACUser> getUsers() {
		return daoFacade.getUsers();
	}

	@Override
	public void remove(ACUser group) {
		daoFacade.remove(group);
	}

	@Override
	public void add(ACGroup group) {
		daoFacade.add(group);
	}

	@Override
	public EList<ACGroup> getGroups() {
		return daoFacade.getGroups();
	}

	@Override
	public void remove(ACGroup group) {
		daoFacade.remove(group);
	}

	@Override
	public void add(ProjectHistory history) {
		daoFacade.add(history);
	}

	@Override
	public EList<ProjectHistory> getProjects() {
		return daoFacade.getProjects();
	}

	@Override
	public void remove(ProjectHistory history) {
		daoFacade.remove(history);
	}

}