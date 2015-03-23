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
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.MethodInvocation;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.core.helper.EmfStoreMethod.MethodId;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectHistory;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ProjectAdminRole;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ServerAdmin;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGlobalProjectIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESGroupImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESOrgUnitIdImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESProjectHistoryImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESAuthorizationService;
import org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.model.ESGlobalProjectId;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESProjectHistory;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * @author Edgar
 *
 */
public class DefaultESAuthorizationService implements ESAuthorizationService {

	/**
	 * Contains possible access levels.
	 */
	private enum AccessLevel {
		PROJECT_READ, PROJECT_WRITE, PROJECT_ADMIN, SERVER_ADMIN, NONE
	}

	private EnumMap<MethodId, AccessLevel> accessMap;
	private Sessions sessions;
	private ESOrgUnitResolver orgUnitResolver;
	private ESOrgUnitProvider orgUnitProvider;

	private void initAccessMap() {
		if (accessMap != null) {
			return;
		}
		accessMap = new EnumMap<MethodId, AccessLevel>(MethodId.class);

		addAccessMapping(AccessLevel.NONE,
			MethodId.GETVERSION);

		addAccessMapping(AccessLevel.PROJECT_READ,
			MethodId.GETPROJECT,
			MethodId.GETEMFPROPERTIES,
			MethodId.GETHISTORYINFO,
			MethodId.GETCHANGES,
			MethodId.RESOLVEVERSIONSPEC,
			MethodId.DOWNLOADFILECHUNK);

		addAccessMapping(AccessLevel.PROJECT_WRITE,
			MethodId.SETEMFPROPERTIES,
			MethodId.TRANSMITPROPERTY,
			MethodId.UPLOADFILECHUNK,
			MethodId.CREATEVERSION,
			MethodId.GETBRANCHES);

		addAccessMapping(AccessLevel.PROJECT_ADMIN,
			MethodId.DELETEPROJECT,
			MethodId.REMOVETAG,
			MethodId.ADDTAG);

		addAccessMapping(AccessLevel.SERVER_ADMIN,
			MethodId.IMPORTPROJECTHISTORYTOSERVER,
			MethodId.EXPORTPROJECTHISTORYFROMSERVER,
			MethodId.REGISTEREPACKAGE);

		// TODO: extract
		if (ServerConfiguration.isProjectAdminPrivileg(ESProjectAdminPrivileges.ShareProject)) {
			addAccessMapping(AccessLevel.PROJECT_ADMIN,
				MethodId.CREATEPROJECT,
				MethodId.CREATEEMPTYPROJECT);
		} else {
			addAccessMapping(AccessLevel.SERVER_ADMIN,
				MethodId.CREATEPROJECT,
				MethodId.CREATEEMPTYPROJECT);
		}

		addAccessMapping(AccessLevel.NONE, MethodId.GETPROJECTLIST, MethodId.RESOLVEUSER);
	}

	private void addAccessMapping(AccessLevel type, MethodId... operationTypes) {
		for (final MethodId opType : operationTypes) {
			accessMap.put(opType, type);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId)
	 */
	public boolean checkProjectAdminAccess(ESSessionId sessionId, ESGlobalProjectId projectId)
		throws AccessControlException {
		checkSession(sessionId);

		final ACUser user = sessions.getUser(sessionId);
		// final ACUser user = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final List<Role> roles = new ArrayList<Role>();
		roles.addAll(user.getRoles());
		final List<Role> internalRoles = APIUtil.toInternal(
			orgUnitResolver.getRolesFromGroups(user.toAPI()));
		roles.addAll(internalRoles);

		for (final Role role : roles) {
			if (isServerAdminRole(role)) {
				return true;
			}
		}

		for (final Role role : roles) {
			if (projectId == null && isProjectAdminRole(role)) {
				return false;
			}

			final ProjectId internalId = ESGlobalProjectIdImpl.class.cast(projectId).toInternalAPI();
			if (role.canAdministrate(internalId)) {
				return false;
			}
		}
		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	private boolean isServerAdminRole(Role role) {
		return ServerAdmin.class.isInstance(role);
	}

	private boolean isProjectAdminRole(Role role) {
		return ProjectAdminRole.class.isInstance(role);
	}

	private boolean hasServerAdminRole(ACOrgUnit<?> orgUnit) {
		final List<Role> roles = orgUnit.getRoles();
		for (final Role role : roles) {
			if (isServerAdminRole(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkServerAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId)
	 */
	public void checkServerAdminAccess(ESSessionId sessionId) throws AccessControlException {
		sessions.isValid(sessionId);
		final ACUser user = sessions.getUser(sessionId);
		// final ACUser user = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final List<Role> roles = new ArrayList<Role>();
		roles.addAll(user.getRoles());
		final List<Role> rolesFromGroups = APIUtil.toInternal(
			orgUnitResolver.getRolesFromGroups(user.toAPI()));
		roles.addAll(rolesFromGroups);
		for (final Role role : roles) {
			if (role instanceof ServerAdmin) {
				return;
			}
		}
		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkSession(org.eclipse.emf.emfstore.server.model.ESSessionId)
	 */
	public void checkSession(ESSessionId sessionId) throws AccessControlException {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccessForOrgUnit(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESOrgUnitId)
	 */
	public boolean checkProjectAdminAccessForOrgUnit(ESSessionId sessionId, ESOrgUnitId orgUnitId)
		throws AccessControlException {

		cleanupPARole(orgUnitId);
		final List<Role> allRoles = getAllRoles(orgUnitId);
		final Set<ProjectId> involvedProjects = new LinkedHashSet<ProjectId>();
		final ACUser user = sessions.getUser(sessionId);
		final boolean hasServerAdminRole = hasServerAdminRole(user);

		for (final Role role : allRoles) {
			if ((isServerAdminRole(role) || isProjectAdminRole(role)) && !hasServerAdminRole) {
				throw new AccessControlException(Messages.AccessControlImpl_Not_Allowed_To_Remove_Other_Admin);
			}
			involvedProjects.addAll(role.getProjects());
		}

		final Set<ESGlobalProjectId> globalIds = new LinkedHashSet<ESGlobalProjectId>();

		for (final ProjectId projectId : involvedProjects) {
			globalIds.add(projectId.toAPI());
		}

		return checkProjectAdminAccessForOrgUnit(
			sessionId,
			orgUnitId,
			globalIds);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccessForOrgUnit(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESOrgUnitId, java.util.Set)
	 */
	public boolean checkProjectAdminAccessForOrgUnit(ESSessionId sessionId, ESOrgUnitId orgUnitId,
		Set<ESGlobalProjectId> projectIds)
		throws AccessControlException {

		cleanupPARole(orgUnitId);
		final ACUser user = sessions.getUser(sessionId);
		// final ACUser user = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final boolean hasServerAdminRole = hasServerAdminRole(user);

		if (hasServerAdminRole) {
			return true;
		}

		ProjectAdminRole paRole = null;
		for (final Role role : user.getRoles()) {
			if (isProjectAdminRole(role)) {
				paRole = (ProjectAdminRole) role;
				break;
			}
		}

		final Set<ProjectId> ids = new LinkedHashSet<ProjectId>();
		for (final ESGlobalProjectId esGlobalProjectId : projectIds) {
			ids.add(ESGlobalProjectIdImpl.class.cast(esGlobalProjectId).toInternalAPI());
		}

		// TODO: paRole should never be null here
		if (paRole.getProjects().containsAll(ids)) {
			return false;
		}

		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkProjectAdminAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId,
	 *      org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges)
	 */
	// TODO: second parameter is optional
	public boolean checkProjectAdminAccess(ESSessionId sessionId, ESGlobalProjectId projectId,
		ESProjectAdminPrivileges privileg)
		throws AccessControlException {
		sessions.isValid(sessionId);

		final ACUser user = sessions.getUser(sessionId);
		// final ACUser user = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final List<Role> roles = new ArrayList<Role>();
		roles.addAll(user.getRoles());
		final List<Role> internalRoles =
			APIUtil.toInternal(
				orgUnitResolver.getRolesFromGroups(user.toAPI()));
		roles.addAll(internalRoles);
		for (final Role role : roles) {
			if (isServerAdminRole(role)) {
				return true;
			}
		}

		for (final Role role : roles) {
			if (isProjectAdminRole(role)) {

				if (!ServerConfiguration.isProjectAdminPrivileg(privileg)) {
					throw new AccessControlException(Messages.AccessControlImpl_PARole_Missing_Privilege);
				}

				if (projectId == null) {
					return false;
				}

				final ProjectAdminRole paRole = ProjectAdminRole.class.cast(role);
				final ProjectId projectId2 = ESGlobalProjectIdImpl.class.cast(projectId).toInternalAPI();
				if (!paRole.canAdministrate(projectId2)) {
					throw new AccessControlException(Messages.AccessControlImpl_PARole_Missing_Privilege);
				}

				return false;
			}
		}
		throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkReadAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId, java.util.Set)
	 */
	public void checkReadAccess(ESSessionId sessionId, ESGlobalProjectId projectId, Set<EObject> modelElements)
		throws AccessControlException {
		sessions.isValid(sessionId);
		final ACUser user = sessions.getUser(sessionId);
		// final ACUser user = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final List<Role> roles = new ArrayList<Role>();
		roles.addAll(user.getRoles());
		final List<Role> internalRoles =
			APIUtil.toInternal(
				orgUnitResolver.getRolesFromGroups(user.toAPI()));
		roles.addAll(internalRoles);

		final ProjectId internalAPI = ESGlobalProjectIdImpl.class.cast(projectId).toInternalAPI();

		// MK: remove access control simplification
		if (!canRead(roles, internalAPI, null)) {
			throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
			// for (ModelElement modelElement : modelElements) {
			// if (!canRead(roles, projectId, modelElement)) {
			// throw new AccessControlException();
			// }
		}
	}

	/**
	 * Check if the given list of roles can write to the model element in the
	 * project.
	 *
	 * @param roles
	 *            a list of roles
	 * @param projectId
	 *            a project id
	 * @param modelElement
	 *            a model element
	 * @return true if one of the roles can write
	 * @throws AccessControlException
	 */
	private boolean canWrite(List<Role> roles, ProjectId projectId, EObject modelElement) throws AccessControlException {
		for (final Role role : roles) {
			if (role.canModify(projectId, modelElement) || role.canCreate(projectId, modelElement)
				|| role.canDelete(projectId, modelElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the given list of roles can read the model element in the
	 * project.
	 *
	 * @param roles
	 *            a list of roles
	 * @param projectId
	 *            a project id
	 * @param modelElement
	 *            a model element
	 * @return true if one of the roles can read
	 * @throws AccessControlException
	 */
	private boolean canRead(List<Role> roles, ProjectId projectId, EObject modelElement) throws AccessControlException {
		for (final Role role : roles) {
			if (role.canRead(projectId, modelElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkWriteAccess(org.eclipse.emf.emfstore.server.model.ESSessionId,
	 *      org.eclipse.emf.emfstore.server.model.ESGlobalProjectId, java.util.Set)
	 */
	public void checkWriteAccess(ESSessionId sessionId, ESGlobalProjectId projectId, Set<EObject> modelElements)
		throws AccessControlException {
		checkSession(sessionId);
		final ACUser user = sessions.getUser(sessionId);
		// final ACUser user = (ACUser) ESUserImpl.class.cast(esUser).toInternalAPI();
		final List<Role> roles = new ArrayList<Role>();
		roles.addAll(user.getRoles());
		final List<Role> internalRoles =
			APIUtil.toInternal(
				orgUnitResolver.getRolesFromGroups(user.toAPI()));
		roles.addAll(internalRoles);
		// MK: remove access control simplification

		final ProjectId internalAPI = ESGlobalProjectIdImpl.class.cast(projectId).toInternalAPI();

		if (!canWrite(roles, internalAPI, null)) {
			throw new AccessControlException(Messages.AccessControlImpl_Insufficient_Rights);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#checkAccess(org.eclipse.emf.emfstore.internal.server.core.MethodInvocation)
	 */
	public void checkAccess(MethodInvocation op) throws AccessControlException {
		initAccessMap();
		final AccessLevel accessType = accessMap.get(op.getType());
		if (accessType == null) {
			// no access type means "no access"
			throw new AccessControlException(Messages.AccessControlImpl_No_Access);
		}
		switch (accessType) {
		case PROJECT_READ:
			ProjectId projectId = getProjectIdFromParameters(op);
			checkReadAccess(
				op.getSessionId().toAPI(),
				projectId == null ? null : projectId.toAPI(),
				null);
			break;
		case PROJECT_WRITE:
			projectId = getProjectIdFromParameters(op);
			checkWriteAccess(
				op.getSessionId().toAPI(),
				projectId == null ? null : projectId.toAPI(),
				null);
			break;
		case PROJECT_ADMIN:
			projectId = getProjectIdFromParameters(op);
			checkProjectAdminAccess(
				op.getSessionId().toAPI(),
				projectId == null ? null : projectId.toAPI());
			break;
		case SERVER_ADMIN:
			checkServerAdminAccess(op.getSessionId().toAPI());
			break;
		case NONE:
			break;
		default:
			throw new AccessControlException(Messages.AccessControlImpl_Unknown_Access_Type);
		}
	}

	private ProjectId getProjectIdFromParameters(MethodInvocation op) {
		for (final Object obj : op.getParameters()) {
			if (obj instanceof ProjectId) {
				return (ProjectId) obj;
			}
		}
		return null;
		// throw new IllegalArgumentException("the operation MUST have a project id");
	}

	/**
	 * Removes any orphan {@link ProjectId}s from the given {@link ProjectAdminRole}, i.e.
	 * all invalid {@link ProjectId}s will be removed.
	 *
	 * @param orgUnitId
	 *
	 * @param paRole the {@link ProjectAdminRole} to be cleaned up
	 *
	 * @throws AccessControlException in case the requested orgUnit does not exist
	 */
	private void cleanupPARole(ESOrgUnitId esOrgUnitId) throws AccessControlException {

		ProjectAdminRole paRole = null;
		final ACOrgUnitId orgUnitId = ESOrgUnitIdImpl.class.cast(esOrgUnitId).toInternalAPI();
		final List<Role> roles = getAllRoles(esOrgUnitId);

		for (final Role role : roles) {
			if (ProjectAdminRole.class.isInstance(role)) {
				paRole = (ProjectAdminRole) role;
				break;
			}
		}

		if (paRole == null) {
			return;
		}

		final List<ESProjectHistory> externalProjects = orgUnitProvider.getProjects();
		final List<ProjectHistory> projects = new ArrayList<ProjectHistory>();
		for (final ESProjectHistory projectHistory : externalProjects) {
			projects.add(
				ESProjectHistoryImpl.class.cast(projectHistory).toInternalAPI());
		}

		final Set<ProjectId> validProjectIds = new LinkedHashSet<ProjectId>();
		final Set<ProjectId> invalidProjectIdsOfRole = new LinkedHashSet<ProjectId>();
		for (final ProjectHistory projectHistory : projects) {
			validProjectIds.add(projectHistory.getProjectId());
		}
		for (final ProjectId projectId : paRole.getProjects()) {
			if (!validProjectIds.contains(projectId)) {
				invalidProjectIdsOfRole.add(projectId);
			}
		}
		paRole.getProjects().removeAll(invalidProjectIdsOfRole);
		if (paRole.getProjects().size() == 0) {
			getOrgUnit(orgUnitId).getRoles().remove(paRole);
		}
	}

	private List<Role> getAllRoles(ESOrgUnitId esOrgUnitId) throws AccessControlException {

		final ACOrgUnitId orgUnitId = ESOrgUnitIdImpl.class.cast(esOrgUnitId).toInternalAPI();
		final ACOrgUnit<?> orgUnit = getOrgUnit(orgUnitId);
		final ESOrgUnit esOrgUnit = orgUnit.toAPI();
		final List<ESGroup> groups2 = orgUnitResolver.getGroups(esOrgUnit);
		final List<ACGroup> groups = APIUtil.toInternal(groups2);
		final ArrayList<Role> roles = new ArrayList<Role>();
		for (final ACGroup group : groups) {
			roles.addAll(group.getRoles());
		}
		roles.addAll(orgUnit.getRoles());
		return roles;
	}

	private ACOrgUnit<?> getOrgUnit(ACOrgUnitId orgUnitId) throws AccessControlException {
		synchronized (MonitorProvider.getInstance().getMonitor()) {
			for (final ESUser user : orgUnitProvider.getUsers()) {
				final ACUser internalAPI = (ACUser) ESUserImpl.class.cast(user).toInternalAPI();
				if (internalAPI.getId().equals(orgUnitId)) {
					return internalAPI;
				}
			}
			for (final ESGroup group : orgUnitProvider.getGroups()) {
				final ACGroup internalAPI = (ACGroup) ESGroupImpl.class.cast(group).toInternalAPI();
				if (internalAPI.getId().equals(orgUnitId)) {
					return internalAPI;
				}
			}
			throw new AccessControlException(Messages.AccessControlImpl_Given_OrgUnit_Does_Not_Exist);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESAuthorizationService#init(org.eclipse.emf.emfstore.internal.server.accesscontrol.Sessions,
	 *      org.eclipse.emf.emfstore.server.auth.ESOrgUnitResolver,
	 *      org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider)
	 */
	public void init(Sessions sessions, ESOrgUnitResolver orgUnitResolverServive, ESOrgUnitProvider orgUnitProvider) {
		this.sessions = sessions;
		orgUnitResolver = orgUnitResolverServive;
		this.orgUnitProvider = orgUnitProvider;
	}
}
