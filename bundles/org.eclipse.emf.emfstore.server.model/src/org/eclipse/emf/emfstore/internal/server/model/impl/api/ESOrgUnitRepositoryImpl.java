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
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository;
import org.eclipse.emf.emfstore.server.model.ESProjectHistory;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * Default implementation of an {@link ESOrgUnitRepository}.
 *
 * @author emueller
 *
 */
public class ESOrgUnitRepositoryImpl implements ESOrgUnitRepository {

	private final ACDAOFacade facade;

	/**
	 * Constructor.
	 *
	 * @param facade
	 *            the internally available access control DAO backed by EMFStore
	 */
	public ESOrgUnitRepositoryImpl(ACDAOFacade facade) {
		this.facade = facade;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#getUsers()
	 */
	public Set<ESUser> getUsers() {
		return new LinkedHashSet<ESUser>(APIUtil.mapToAPI(ESUser.class, facade.getUsers()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#getGroups()
	 */
	public Set<ESGroup> getGroups() {
		return new LinkedHashSet<ESGroup>(APIUtil.mapToAPI(ESGroup.class, facade.getGroups()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#getProjects()
	 */
	public List<ESProjectHistory> getProjects() {
		return APIUtil.toExternal(facade.getProjects());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#removeGroup(org.eclipse.emf.emfstore.server.model.ESGroup)
	 */
	public void removeGroup(ESGroup group) {
		facade.remove((ACGroup) ESGroupImpl.class.cast(group).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#removeUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void removeUser(ESUser user) {
		facade.remove((ACUser) ESUserImpl.class.cast(user).toInternalAPI());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#addUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void addUser(ESUser user) {
		facade.add((ACUser) ESUserImpl.class.cast(user).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#addGroup(org.eclipse.emf.emfstore.server.model.ESGroup)
	 */
	public void addGroup(ESGroup group) {
		facade.add((ACGroup) ESGroupImpl.class.cast(group).toInternalAPI());

	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository#save()
	 */
	public void save() throws IOException {
		facade.save();
	}

}
