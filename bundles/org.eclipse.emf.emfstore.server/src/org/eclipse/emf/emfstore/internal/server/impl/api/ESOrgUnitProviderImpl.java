/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.impl.api;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGroupImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESProjectHistory;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * @author emueller
 *
 */
public class ESOrgUnitProviderImpl implements ESOrgUnitProvider {

	private ACDAOFacade daoFacade;

	/**
	 *
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#getUsers()
	 */
	public Set<ESUser> getUsers() {
		return new LinkedHashSet<ESUser>(
			APIUtil.mapToAPI(ESUser.class, daoFacade.getUsers()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#getGroups()
	 */
	public Set<ESGroup> getGroups() {
		return new LinkedHashSet<ESGroup>(
			APIUtil.mapToAPI(ESGroup.class, daoFacade.getGroups()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#getProjects()
	 */
	public List<ESProjectHistory> getProjects() {
		return APIUtil.toExternal(daoFacade.getProjects());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#init(org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade)
	 */
	public void init(ACDAOFacade daoFacade) {
		this.daoFacade = daoFacade;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#removeGroup(org.eclipse.emf.emfstore.server.model.ESGroup)
	 */
	public void removeGroup(ESGroup group) {
		daoFacade.remove((ACGroup) ESGroupImpl.class.cast(group).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#removeUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void removeUser(ESUser user) {
		daoFacade.remove((ACUser) ESUserImpl.class.cast(user).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#addUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void addUser(ESUser user) {
		daoFacade.add((ACUser) ESUserImpl.class.cast(user).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#addGroup(org.eclipse.emf.emfstore.server.model.ESGroup)
	 */
	public void addGroup(ESGroup group) {
		daoFacade.add((ACGroup) ESGroupImpl.class.cast(group).toInternalAPI());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#save()
	 */
	public void save() throws IOException {
		daoFacade.save();
	}

}
