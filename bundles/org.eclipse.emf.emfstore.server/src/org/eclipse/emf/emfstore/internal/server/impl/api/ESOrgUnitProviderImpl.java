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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * @author Edgar
 *
 */
public class ESOrgUnitProviderImpl implements ESOrgUnitProvider {

	private final ACDAOFacade daoFacade;

	/**
	 * @param daoFacade
	 */
	public ESOrgUnitProviderImpl(ACDAOFacade daoFacade) {
		this.daoFacade = daoFacade;
	}

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
	public EList<ProjectHistory> getProjects() {
		// TODO Auto-generated method stub
		return daoFacade.getProjects();
	}

}
