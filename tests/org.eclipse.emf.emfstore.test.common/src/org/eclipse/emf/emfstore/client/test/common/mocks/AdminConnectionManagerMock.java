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

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.AbstractConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.AdminConnectionManager;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.core.AdminEmfStoreImpl;
import org.eclipse.emf.emfstore.internal.server.exceptions.ConnectionException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.dao.ACDAOFacade;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

public class AdminConnectionManagerMock extends AbstractConnectionManager<Object> implements AdminConnectionManager {

	private final AdminEmfStoreImpl adminEmfStore;

	public AdminConnectionManagerMock(ACDAOFacade daoFacade, AccessControl accessControl,
		ServerSpace serverSpace)
		throws FatalESException {

		adminEmfStore = new AdminEmfStoreImpl(daoFacade, serverSpace, accessControl);
	}

	public void initConnection(ServerInfo serverInfo, SessionId id) throws ConnectionException {
		// final XmlRpcClientManager clientManager = new
		// XmlRpcClientManager(XmlRpcAdminConnectionHandler.ADMINEMFSTORE);
		// clientManager.initConnection(serverInfo);
		// map.put(id, clientManager);
		addConnectionProxy(id, new Object());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getProjectInfos(org.eclipse.emf.emfstore.internal.server.model.SessionId)
	 */
	public List<ProjectInfo> getProjectInfos(SessionId sessionId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getProjectInfos(sessionId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getGroups(org.eclipse.emf.emfstore.internal.server.model.SessionId)
	 */
	public List<ACGroup> getGroups(SessionId sessionId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getGroups(sessionId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getUsers(org.eclipse.emf.emfstore.internal.server.model.SessionId)
	 */
	public List<ACUser> getUsers(SessionId sessionId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getUsers(sessionId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getOrgUnits(org.eclipse.emf.emfstore.internal.server.model.SessionId)
	 */
	public List<ACOrgUnit<?>> getOrgUnits(SessionId sessionId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getOrgUnits(sessionId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getOrgUnit(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public ACOrgUnit<?> getOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getOrgUnit(sessionId, orgUnitId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#createGroup(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      java.lang.String)
	 */
	public ACOrgUnitId createGroup(SessionId sessionId, String name) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.createGroup(sessionId, name);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#deleteGroup(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public void deleteGroup(SessionId sessionId, ACOrgUnitId groupId) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.deleteGroup(sessionId, groupId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getGroups(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public List<ACGroup> getGroups(SessionId sessionId, ACOrgUnitId orgUnitId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getGroups(sessionId, orgUnitId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#removeGroup(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public void removeGroup(SessionId sessionId, ACOrgUnitId user, ACOrgUnitId group) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.removeGroup(sessionId, user, group);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getMembers(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public List<ACOrgUnit<?>> getMembers(SessionId sessionId, ACOrgUnitId groupId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getMembers(sessionId, groupId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#addMember(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public void addMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.addMember(sessionId, group, member);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#removeMember(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public void removeMember(SessionId sessionId, ACOrgUnitId group, ACOrgUnitId member) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.removeMember(sessionId, group, member);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#createUser(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      java.lang.String)
	 */

	public ACOrgUnitId createUser(SessionId sessionId, String name) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.createUser(sessionId, name);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#deleteUser(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public void deleteUser(SessionId sessionId, ACOrgUnitId userId) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.deleteUser(sessionId, userId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#changeOrgUnit(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, java.lang.String,
	 *      java.lang.String)
	 */
	public void changeOrgUnit(SessionId sessionId, ACOrgUnitId orgUnitId, String name, String description)
		throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.changeOrgUnit(sessionId, orgUnitId, name, description);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#changeUser(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, java.lang.String,
	 *      java.lang.String)
	 */
	public void changeUser(SessionId sessionId, ACOrgUnitId userId, String name, String password) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.changeUser(sessionId, userId, name, password);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getParticipants(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId)
	 */
	public List<ACOrgUnit<?>> getParticipants(SessionId sessionId, ProjectId projectId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getParticipants(sessionId, projectId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#addParticipant(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, org.eclipse.emf.ecore.EClass)
	 */
	public void addParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId, EClass roleClass)
		throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.addParticipant(sessionId, projectId, participantId, roleClass);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#removeParticipant(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public void removeParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId)
		throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.removeParticipant(sessionId, projectId, participantId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#getRole(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId)
	 */
	public Role getRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnitId) throws ESException {
		getConnectionProxy(sessionId);
		return adminEmfStore.getRole(sessionId, projectId, orgUnitId);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#changeRole(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, org.eclipse.emf.ecore.EClass)
	 */
	public void changeRole(SessionId sessionId, ProjectId projectId, ACOrgUnitId orgUnitId, EClass role)
		throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.changeRole(sessionId, projectId, orgUnitId, role);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#assignRole(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, org.eclipse.emf.ecore.EClass)
	 */
	public void assignRole(SessionId sessionId, ACOrgUnitId orgUnitId, EClass roleClass) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.assignRole(sessionId, orgUnitId, roleClass);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.AdminEmfStore#addInitialParticipant(org.eclipse.emf.emfstore.internal.server.model.SessionId,
	 *      org.eclipse.emf.emfstore.internal.server.model.ProjectId,
	 *      org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId, org.eclipse.emf.ecore.EClass)
	 */
	public void addInitialParticipant(SessionId sessionId, ProjectId projectId, ACOrgUnitId participantId,
		EClass roleClass) throws ESException {
		getConnectionProxy(sessionId);
		adminEmfStore.addInitialParticipant(sessionId, projectId, participantId, roleClass);

	}

}
