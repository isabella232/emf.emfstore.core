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
package org.eclipse.emf.emfstore.internal.server.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.internal.server.exceptions.SessionTimedOutException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectId;
import org.eclipse.emf.emfstore.internal.server.model.ServerSpace;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESUserImpl;
import org.eclipse.emf.emfstore.server.auth.ESProjectAdminPrivileges;
import org.eclipse.emf.emfstore.server.model.ESGlobalProjectId;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * Super class of all EmfstoreInterfaces. Emfstore interfaces performs sanity checks, runs accesscontrol and then
 * delegates the method call to the relating subinterface which actually implements the functionality. Using
 * {@link InternalCommand} it is possible to access the interface without accesscontrol.
 *
 * @see AbstractSubEmfstoreInterface
 * @see org.eclipse.emf.emfstore.internal.server.EMFStoreInterface
 * @author wesendon
 */
public abstract class AbstractEmfstoreInterface {

	private final HashMap<Class<? extends AbstractSubEmfstoreInterface>, AbstractSubEmfstoreInterface> subInterfaces;
	private boolean accessControlDisabled;
	private final ServerSpace serverSpace;
	private final AccessControl accessControl;

	/**
	 * Default constructor.
	 *
	 * @param serverSpace the server space
	 * @param accessControl access control
	 * @throws FatalESException if initialization fails
	 */
	public AbstractEmfstoreInterface(ServerSpace serverSpace, AccessControl accessControl)
		throws FatalESException {
		if (serverSpace == null || accessControl == null) {
			throw new FatalESException();
		}
		this.serverSpace = serverSpace;
		this.accessControl = accessControl;
		accessControlDisabled = false;
		subInterfaces = new LinkedHashMap<Class<? extends AbstractSubEmfstoreInterface>, AbstractSubEmfstoreInterface>();
		initSubInterfaces();
		for (final AbstractSubEmfstoreInterface subInterface : subInterfaces.values()) {
			subInterface.initSubInterface();
		}
	}

	/**
	 * Implement this method in order to add subinterfaces. Therefor use the
	 * {@link #addSubInterface(AbstractSubEmfstoreInterface)} method.
	 *
	 * @throws FatalESException in case of failure
	 */
	protected abstract void initSubInterfaces() throws FatalESException;

	/**
	 * Adds a subinterface to the parent interface. If the subinterface exists already, the present instance is
	 * overwritten by the new one.
	 *
	 * @param subInterface the subinterface
	 */
	protected void addSubInterface(AbstractSubEmfstoreInterface subInterface) {
		if (subInterface != null) {
			getSubInterfaces().put(subInterface.getClass(), subInterface);
		}
	}

	/**
	 * Returns list of subinterfaces.
	 *
	 * @return list of subinterfaces
	 */
	protected HashMap<Class<? extends AbstractSubEmfstoreInterface>, AbstractSubEmfstoreInterface> getSubInterfaces() {
		return subInterfaces;
	}

	/**
	 * Returns the requested subinterface if available.
	 *
	 * @param <T> subinterface type
	 * @param clazz subinterface class type
	 * @return subinterface
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getSubInterface(Class<T> clazz) {
		return (T) subInterfaces.get(clazz);
	}

	/**
	 * Returns the serverspace. Please always use a monitor ({@link #getMonitor()}) when operating on the serverspace.
	 *
	 * @return serverspace
	 */
	protected ServerSpace getServerSpace() {
		return serverSpace;
	}

	/**
	 * Return a monitor object which should be used when operating on the serverspace.
	 *
	 * @return monitor object
	 */
	protected Object getMonitor() {
		return MonitorProvider.getInstance().getMonitor();
	}

	/**
	 * Returns the authorizationControl.
	 *
	 * @return authorizationControl
	 */
	protected AccessControl getAccessControl() {
		return accessControl;
	}

	/**
	 * Checks if the given session is valid.
	 *
	 * @param sessionId
	 *            the session to be check
	 * @throws SessionTimedOutException
	 *             in case the session is invalid
	 */
	protected synchronized void checkSession(SessionId sessionId) throws SessionTimedOutException {
		getAccessControl().getSessions().isValid(sessionId.toAPI());
	}

	/**
	 * Checks read access.
	 *
	 * @see AuthorizationControl#checkReadAccess(SessionId, ProjectId, Set)
	 * @param sessionId sessionid
	 * @param projectId project id
	 * @param modelElements modelelemnts
	 * @throws AccessControlException access exception
	 */
	protected synchronized void checkReadAccess(SessionId sessionId, ProjectId projectId, Set<EObject> modelElements)
		throws AccessControlException {
		if (accessControlDisabled) {
			return;
		}
		getAccessControl().getAuthorizationService().checkReadAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			modelElements);
	}

	/**
	 * Checks write access.
	 *
	 * @see AuthorizationControl#checkWriteAccess(SessionId, ProjectId, Set)
	 * @param sessionId sessionid
	 * @param projectId project id
	 * @param modelElements modelelemnts
	 * @throws AccessControlException access exception
	 */
	protected synchronized void checkWriteAccess(SessionId sessionId, ProjectId projectId, Set<EObject> modelElements)
		throws AccessControlException {
		if (accessControlDisabled) {
			return;
		}
		getAccessControl().getAuthorizationService().checkWriteAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			modelElements);
	}

	protected SessionId resolveSessionById(String sessionId) {
		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId);
		return APIUtil.toInternal(SessionId.class, resolvedSession);
	}

	protected ACUser resolveUserBySessionId(String sessionId) throws AccessControlException {
		final ESSessionId resolvedSession = getAccessControl().getSessions().resolveSessionById(sessionId);
		final ESUser resolvedUser = getAccessControl().getSessions().resolveUser(resolvedSession);
		return (ACUser) ESUserImpl.class.cast(resolvedUser).toInternalAPI();
	}

	/**
	 * Checks project administrator access.
	 *
	 * @see AuthorizationControl#checkProjectAdminAccess(SessionId, ProjectId)
	 * @param sessionId
	 *            a valid session id
	 * @param projectId
	 *            project id
	 * @param privilege project administrator privilege to be checked
	 * @throws AccessControlException access exception
	 */
	protected synchronized void checkProjectAdminAccess(SessionId sessionId, ProjectId projectId,
		ESProjectAdminPrivileges privilege)
		throws AccessControlException {
		if (accessControlDisabled) {
			return;
		}
		getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI(),
			privilege);
	}

	/**
	 * Checks project administrator access for the given organizational unit.
	 *
	 * @see AuthorizationControl#checkProjectAdminAccess(SessionId, ProjectId)
	 * @param sessionId
	 *            a valid session id
	 * @param projectId
	 *            project id
	 * @param orgUnitId
	 *            the ID of the organizational unit
	 * @return if {@code true}, access was granted via the server administrator role, otherwise
	 *         access has been granted by the project administrator role
	 * @throws AccessControlException
	 *             in case the caller has no access at all
	 */
	protected synchronized boolean checkProjectAdminAccessForOrgUnit(SessionId sessionId, ProjectId projectId,
		ACOrgUnitId orgUnitId) throws AccessControlException {

		if (accessControlDisabled) {
			return true;
		}

		return getAccessControl().getAuthorizationService().checkProjectAdminAccessForOrgUnit(
			sessionId.toAPI(),
			orgUnitId.toAPI(),
			Collections.<ESGlobalProjectId> singleton(projectId.toAPI()));
	}

	/**
	 * Checks project administrator access.
	 *
	 * @see AuthorizationControl#checkProjectAdminAccess(SessionId, ProjectId)
	 * @param sessionId
	 *            a valid session id
	 * @param privilege
	 *            project administrator privilege to be checked
	 * @return if {@code true}, access was granted via the server administrator role, otherwise
	 *         access has been granted by the project administrator role
	 * @throws AccessControlException access exception
	 */
	protected synchronized boolean checkProjectAdminAccess(SessionId sessionId, ESProjectAdminPrivileges privilege)
		throws AccessControlException {
		if (accessControlDisabled) {
			return true;
		}
		return getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null,
			privilege);
	}

	/**
	 * Checks project administrator access.
	 *
	 * @see AuthorizationControl#checkProjectAdminAccess(SessionId, ProjectId)
	 * @param sessionId
	 *            a valid session id
	 * @return if {@code true}, access was granted via the server administrator role, otherwise
	 *         access has been granted by the project administrator role
	 * @throws AccessControlException access exception
	 */
	protected synchronized boolean checkProjectAdminAccess(SessionId sessionId)
		throws AccessControlException {
		if (accessControlDisabled) {
			return true;
		}
		return getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			null);
	}

	/**
	 * Checks project admin access.
	 *
	 * @see AuthorizationControl#checkProjectAdminAccess(SessionId, ProjectId)
	 * @param sessionId
	 *            a valid session id
	 * @param projectId project id
	 * @return if {@code true}, access was granted via the server administrator role, otherwise
	 *         access has been granted by the project administrator role
	 * @throws AccessControlException access exception
	 */
	protected synchronized boolean checkProjectAdminAccess(SessionId sessionId, ProjectId projectId)
		throws AccessControlException {
		if (accessControlDisabled) {
			return true;
		}

		return getAccessControl().getAuthorizationService().checkProjectAdminAccess(
			sessionId.toAPI(),
			projectId.toAPI());
	}

	/**
	 * Checks server admin access.
	 *
	 * @see AuthorizationControl#checkServerAdminAccess(SessionId)
	 * @param sessionId sessionid
	 * @throws AccessControlException access exception
	 */
	protected synchronized void checkServerAdminAccess(SessionId sessionId) throws AccessControlException {
		if (accessControlDisabled) {
			return;
		}
		getAccessControl().getAuthorizationService().checkServerAdminAccess(sessionId.toAPI());
	}

	/**
	 * Applies a sanity check {@link #sanityCheckObject(Object)} to all given objects. Elements will be checked in the
	 * same order as the input. This allows you to check attributes as well. E.g.: <code>sanityCheckObjects(element,
	 * element.getAttribute())</code>. Due to the order, it is important to enter the element BEFORE the attribute,
	 * otherwise a NPE would occur, if the element would be null.
	 *
	 * @param objects objects to check
	 * @throws InvalidInputException is thrown if the check fails
	 */
	protected void sanityCheckObjects(Object... objects) throws InvalidInputException {
		for (final Object object : objects) {
			sanityCheckObject(object);
		}
	}

	/**
	 * Checks whether a given object is null. Further sanity checks could be added. <strong>Note:</strong> Maybe we
	 * should use specialized sanity checks for EObjects or other types.
	 *
	 * @param object object to check
	 * @throws InvalidInputException is thrown if the check fails
	 */
	private void sanityCheckObject(Object object) throws InvalidInputException {
		if (object == null) {
			throw new InvalidInputException();
		}
	}

	/**
	 * Runs an internal command, in order to avoid accesscontrol.
	 *
	 * @param command internal command
	 */
	public synchronized void runCommand(InternalCommand<? extends AbstractEmfstoreInterface> command) {
		accessControlDisabled = true;
		command.setInterface(this);
		command.doExecute();
		accessControlDisabled = false;
	}
}
