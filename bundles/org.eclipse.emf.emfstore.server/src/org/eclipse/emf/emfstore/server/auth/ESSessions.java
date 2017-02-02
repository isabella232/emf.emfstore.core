/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.auth;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.server.accesscontrol.Messages;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.ACUserContainer;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.SessionTimedOutException;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAuthenticationInformationImpl;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESSessionId;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Preconditions;

/**
 * Utility class for handling session management.
 *
 * @author emueller
 * @since 1.5
 *
 */
public class ESSessions {

	/**
	 * Map holding valid session IDs and the respective users.
	 *
	 * @since 1.9
	 */
	protected final Map<SessionId, ACUserContainer> sessionUserMap;

	/**
	 * Default constructor.
	 */
	public ESSessions() {
		sessionUserMap = new LinkedHashMap<SessionId, ACUserContainer>();
	}

	/**
	 * Add the information of the given {@link ESAuthenticationInformation} to the session map.
	 *
	 * @param authenticationInformation
	 *            the {@link AuthenticationInformation} containing the valid session ID
	 */
	public void add(ESAuthenticationInformation authenticationInformation) {

		final AuthenticationInformation authInfo = ESAuthenticationInformationImpl.class
			.cast(authenticationInformation).toInternalAPI();

		sessionUserMap.put(
			authInfo.getSessionId(),
			new ACUserContainer(authInfo.getResolvedACUser()));
	}

	/**
	 * Checks whether the given session ID belongs to a known session.
	 *
	 * @param sessionId
	 *            the ID of the session to be checked
	 * @throws SessionTimedOutException in case the session is unknown
	 */
	public void isValid(ESSessionId sessionId) throws SessionTimedOutException {
		if (!sessionUserMap.containsKey(toInternalSession(sessionId))) {
			throw new SessionTimedOutException(Messages.AccessControlImpl_SessionID_Unknown);
		}
	}

	/**
	 * Returns the user associated with the given session ID and checks if the
	 * user has an active session.
	 *
	 * @param sessionId
	 *            the session ID of the user who should be retrieved
	 * @return the user associated with the given session ID.
	 * @throws AccessControlException in case the session active session check fails
	 */
	public ESUser resolveUser(ESSessionId sessionId) throws AccessControlException {
		Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
		final ACUserContainer container = sessionUserMap.get(toInternalSession(sessionId));

		if (container == null) {
			return null;
		}

		return container.getUser().toAPI();
	}

	/**
	 * Returns the user associated with the given session ID and checks if the
	 * user has an active session.
	 *
	 * @param sessionId
	 *            the session ID of the user who should be retrieved
	 * @return the user associated with the given session ID.
	 * @throws AccessControlException in case the session active session check fails
	 */
	public ESOrgUnitId resolveToOrgUnitId(ESSessionId sessionId) throws AccessControlException {
		Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
		final SessionId session = ESSessionIdImpl.class.cast(sessionId).toInternalAPI();
		final ACUserContainer container = sessionUserMap.get(session);

		if (container == null) {
			return null;
		}

		return container.getUser().getId().toAPI();
	}

	/**
	 * Returns the user associated with the given session ID.
	 *
	 * @param sessionId
	 *            the session ID of the user who should be retrieved
	 * @return the user associated with the given session ID.
	 */
	public ESUser getRawUser(ESSessionId sessionId) {
		Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
		return sessionUserMap.get(toInternalSession(sessionId)).getRawUser().toAPI();
	}

	/**
	 * Removes the session with the given ID from the sessions map.
	 *
	 * @param sessionId
	 *            the ID of the session to be removed
	 */
	public void remove(ESSessionId sessionId) {
		Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
		sessionUserMap.remove(toInternalSession(sessionId));
	}

	/**
	 * Resolves a given Session ID string to a valid {@link SessionId} object.
	 *
	 * @param sessionId
	 *            the session ID to be resolved
	 * @return the resolved session or {@code null}, if no session
	 *         with the given session ID exists
	 */
	public ESSessionId resolveSessionById(String sessionId) {
		final Set<Entry<SessionId, ACUserContainer>> entrySet = sessionUserMap.entrySet();
		for (final Entry<SessionId, ACUserContainer> entry : entrySet) {
			if (entry.getKey().getId().equals(sessionId)) {
				return entry.getKey().toAPI();
			}
		}

		return null;
	}

	private static SessionId toInternalSession(ESSessionId sessionId) {
		Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
		return ESSessionIdImpl.class.cast(sessionId).toInternalAPI();
	}
}
