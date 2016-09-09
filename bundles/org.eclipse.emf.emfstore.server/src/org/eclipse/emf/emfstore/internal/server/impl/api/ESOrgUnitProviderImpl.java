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
import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository;
import org.eclipse.emf.emfstore.server.model.ESProjectHistory;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * @author emueller
 *
 */
public class ESOrgUnitProviderImpl implements ESOrgUnitProvider {

	private ESOrgUnitRepository repository;

	/**
	 * Value constructor.
	 *
	 * @param repository
	 *            the repository for organizational units backed by EMFStore
	 */
	public ESOrgUnitProviderImpl(ESOrgUnitRepository repository) {
		this.repository = repository;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#getUsers()
	 */
	public Set<ESUser> getUsers() {
		return repository.getUsers();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#getGroups()
	 */
	public Set<ESGroup> getGroups() {
		return repository.getGroups();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#getProjects()
	 */
	public List<ESProjectHistory> getProjects() {
		return repository.getProjects();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#init(org.eclipse.emf.emfstore.server.model.ESOrgUnitRepository)
	 */
	public void init(ESOrgUnitRepository repository) {
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#removeGroup(org.eclipse.emf.emfstore.server.model.ESGroup)
	 */
	public void removeGroup(ESGroup group) {
		repository.removeGroup(group);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#removeUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void removeUser(ESUser user) {
		repository.removeUser(user);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#addUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void addUser(ESUser user) {
		repository.addUser(user);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#addGroup(org.eclipse.emf.emfstore.server.model.ESGroup)
	 */
	public void addGroup(ESGroup group) {
		repository.addGroup(group);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider#save()
	 */
	public void save() throws IOException {
		repository.save();
	}

}
