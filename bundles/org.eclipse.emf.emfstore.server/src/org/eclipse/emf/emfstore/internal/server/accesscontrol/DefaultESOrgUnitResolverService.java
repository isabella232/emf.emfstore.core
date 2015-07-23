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
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGroupImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOrgUnitIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOrgUnitImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESRole;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Preconditions;

/**
 * Default implementation of an {@link ESOrgUnitResolver}.
 *
 * @author emueller
 *
 */
public class DefaultESOrgUnitResolverService implements ESOrgUnitResolver {

	private ESOrgUnitProvider orgUnitProvider;

	/**
	 *
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#getRolesFromGroups(org.eclipse.emf.emfstore.server.model.ESOrgUnit)
	 */
	public List<ESRole> getRolesFromGroups(ESOrgUnit orgUnit) {
		final ArrayList<ESRole> roles = new ArrayList<ESRole>();
		for (final ESGroup group : getGroups(orgUnit)) {
			roles.addAll(group.getRoles());
		}
		return roles;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#resolveRoles(org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation)
	 */
	public ESUser resolveRoles(ESAuthenticationInformation authInfo) throws AccessControlException {
		return copyAndResolveUser(authInfo.getUser());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#copyAndResolveUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public ESUser copyAndResolveUser(ESUser esUser) {
		final ACUser tmpUser = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final ACUser user = ModelUtil.clone(tmpUser);
		final List<ESRole> rolesFromGroups = getRolesFromGroups(esUser);
		final List<Role> internal = APIUtil.toInternal(rolesFromGroups);
		for (final Role role : internal) {
			user.getRoles().add(ModelUtil.clone(role));
		}

		final List<ESGroup> groups = getGroups(esUser);
		final List<ACGroup> internal2 = APIUtil.toInternal(groups);
		for (final ACGroup group : internal2) {
			if (user.getEffectiveGroups().contains(group)) {
				continue;
			}
			final ACGroup copy = ModelUtil.clone(group);
			user.getEffectiveGroups().add(copy);
			copy.getMembers().clear();
		}

		return user.toAPI();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws AccessControlException
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#resolveUser(org.eclipse.emf.emfstore.server.model.ESOrgUnitId)
	 */
	public ESUser resolveUser(ESOrgUnitId orgUnitId) throws AccessControlException {

		Preconditions.checkNotNull(orgUnitId);

		synchronized (MonitorProvider.getInstance().getMonitor()) {
			final Set<ESUser> users = orgUnitProvider.getUsers();
			final Set<ACUser> internal = APIUtil.toInternal(users);
			for (final ACUser user : internal) {
				if (user.getId().equals(ESOrgUnitIdImpl.class.cast(orgUnitId).toInternalAPI())) {
					return user.toAPI();
				}
			}
			throw new AccessControlException();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#getGroups(org.eclipse.emf.emfstore.server.model.ESOrgUnitId)
	 */
	public List<ESGroup> getGroups(ESOrgUnitId orgUnitId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#getGroups(org.eclipse.emf.emfstore.server.model.ESOrgUnit)
	 */
	public List<ESGroup> getGroups(ESOrgUnit esOrgUnit) {

		final ACOrgUnit<?> orgUnit = (ACOrgUnit<?>) ESOrgUnitImpl.class.cast(esOrgUnit).toInternalAPI();

		synchronized (MonitorProvider.getInstance().getMonitor()) {

			final ArrayList<ESGroup> groups = new ArrayList<ESGroup>();

			for (final ESGroup esGroup : orgUnitProvider.getGroups()) {
				final ACGroup group = (ACGroup) ESGroupImpl.class.cast(esGroup).toInternalAPI();
				if (group.getMembers().contains(orgUnit)) {
					groups.add(group.toAPI());
					for (final ESGroup esG : getGroups(group.toAPI())) {
						final ACGroup g = (ACGroup) ESGroupImpl.class.cast(esG).toInternalAPI();
						if (groups.contains(g)) {
							continue;
						}
						groups.add(g.toAPI());
					}
				}
			}

			return groups;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver#init(org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider)
	 */
	public void init(ESOrgUnitProvider orgUnitProvider) {
		this.orgUnitProvider = orgUnitProvider;
	}
}
