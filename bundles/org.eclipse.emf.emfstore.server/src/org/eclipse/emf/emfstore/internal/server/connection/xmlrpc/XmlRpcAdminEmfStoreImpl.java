/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * wesendon
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.connection.xmlrpc;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.internal.server.AdminEmfStore;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * XML RPC connection interface for adminemfstore.
 *
 * @author wesendon
 */
public class XmlRpcAdminEmfStoreImpl implements AdminEmfStore {

	private AdminEmfStore getAdminEmfStore() {
		return XmlRpcAdminConnectionHandler.getAdminEmfStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException {
		getAdminEmfStore().addMember(sessionId, group, member);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participant, EClass roleClass)
		throws ESException {
		getAdminEmfStore().addParticipant(sessionId, projectId, participant, roleClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId, String name, String description)
		throws ESException {
		getAdminEmfStore().changeOrgUnit(sessionId, orgUnitId, name, description);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#changeUser(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void changeUser(SessionId sessionId, ACOrgUnitId userId, String name, String password) throws ESException {
		getAdminEmfStore().changeUser(sessionId, userId, name, password);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnit, EClass role)
		throws ESException {
		getAdminEmfStore().changeRole(sessionId, projectId, orgUnit, role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ACOrgUnitId createGroup(SessionId sessionId, String name) throws ESException {
		return getAdminEmfStore().createGroup(sessionId, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ACOrgUnitId createUser(SessionId sessionId, String name) throws ESException {
		return getAdminEmfStore().createUser(sessionId, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteGroup(SessionId sessionId, ACOrgUnitId group) throws ESException {
		getAdminEmfStore().deleteGroup(sessionId, group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUser(SessionId sessionId, ACOrgUnitId user) throws ESException {
		getAdminEmfStore().deleteUser(sessionId, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ACGroup> getGroups(SessionId sessionId) throws ESException {
		return getAdminEmfStore().getGroups(sessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ACGroup> getGroups(SessionId sessionId, ACOrgUnitId user) throws ESException {
		return getAdminEmfStore().getGroups(sessionId, user);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ACOrgUnit> getMembers(SessionId sessionId, ACOrgUnitId groupId) throws ESException {
		return getAdminEmfStore().getMembers(sessionId, groupId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ACOrgUnit getOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId) throws ESException {
		return getAdminEmfStore().getOrgUnit(sessionId, orgUnitId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ACOrgUnit> getOrgUnits(SessionId sessionId) throws ESException {
		return getAdminEmfStore().getOrgUnits(sessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ACOrgUnit> getParticipants(SessionId sessionId, ProjectId projectId) throws ESException {
		return getAdminEmfStore().getParticipants(sessionId, projectId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectInfo> getProjectInfos(SessionId sessionId) throws ESException {
		return getAdminEmfStore().getProjectInfos(sessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Role getRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnit) throws ESException {
		return getAdminEmfStore().getRole(sessionId, projectId, orgUnit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ACUser> getUsers(SessionId sessionId) throws ESException {
		return getAdminEmfStore().getUsers(sessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeGroup(SessionId sessionId, ACOrgUnitId user, ACOrgUnitId group) throws ESException {
		getAdminEmfStore().removeGroup(sessionId, user, group);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException {
		getAdminEmfStore().removeMember(sessionId, group, member);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participant)
		throws ESException {
		getAdminEmfStore().removeParticipant(sessionId, projectId, participant);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#assignRole(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, org.eclipse.emf.ecore.EClass)
	 */
	@Override
	public void assignRole(SessionId sessionId, ACOrgUnitId orgUnitId, EClass roleClass) throws ESException {
		getAdminEmfStore().assignRole(sessionId, orgUnitId, roleClass);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#addInitialParticipant(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, org.eclipse.emf.ecore.EClass)
	 */
	@Override
	public void addInitialParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId,
		EClass roleClass) throws ESException {
		getAdminEmfStore().addInitialParticipant(sessionId, projectId, participantId, roleClass);

	}
}
