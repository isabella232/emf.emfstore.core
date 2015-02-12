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
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.ACUserContainer;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.exceptions.SessionTimedOutException;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESSessionId;

import com.google.common.base.Preconditions;

/**
 * Utility class for handling session management.
 *
 * @author emueller
 *
 */
public class Sessions {

	private final Map<SessionId, ACUserContainer> sessionUserMap;

	/**
	 * Default constructor.
	 */
	public Sessions() {
		sessionUserMap = new LinkedHashMap<SessionId, ACUserContainer>();
	}

	/**
	 * Add the information of the given {@link ESAuthenticationInformation} to the session map.
	 *
	 * @param authenticationInformation
	 *            the {@link AuthenticationInformation} containing the valid session ID
	 */
	@SuppressWarnings("restriction")
	public void add(ESAuthenticationInformation authenticationInformation) {

		final AuthenticationInformation authInfo =
			org.eclipse.emf.emfstore.internal.server.model.impl.api.ESAuthenticationInformationImpl.class
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
	 * @throws AccessControlException in case the session is unknown
	 */
	public void isValid(ESSessionId sessionId) throws AccessControlException {
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
	public ACUser getUser(ESSessionId sessionId) throws AccessControlException {
		try {
			Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
			return sessionUserMap.get(toInternalSession(sessionId)).getUser();
		} catch (final AccessControlException e) {
			sessionUserMap.remove(sessionId);
			throw e;
		}
	}

	/**
	 * Returns the user associated with the given session ID.
	 *
	 * @param sessionId
	 *            the session ID of the user who should be retrieved
	 * @return the user associated with the given session ID.
	 */
	public ACUser getRawUser(ESSessionId sessionId) {
		Preconditions.checkNotNull(sessionId, "sessionId must not be null"); //$NON-NLS-1$
		return sessionUserMap.get(toInternalSession(sessionId)).getRawUser();
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

	@SuppressWarnings("restriction")
	private static SessionId toInternalSession(ESSessionId sessionId) {
		return org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl.class
			.cast(sessionId).toInternalAPI();
	}
}
