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
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers;

import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.core.MonitorProvider;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESClientVersionInfo;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Optional;

/**
 * @author emueller
 *
 */
public abstract class UserVerifier extends PasswordVerifier {

	private final ESOrgUnitProvider orgUnitProvider;

	/**
	 * Default constructor.
	 *
	 * @param orgUnitProvider
	 *            an {@link ESOrgUnitProvider} for finding users
	 */
	public UserVerifier(ESOrgUnitProvider orgUnitProvider) {
		this.orgUnitProvider = orgUnitProvider;
	}

	/**
	 * Tries to login the given user.
	 *
	 * @param username
	 *            the user name as determined by the client
	 * @param password
	 *            the password as entered by the client
	 * @param clientVersionInfo
	 *            the version of the client
	 * @return an {@link AuthenticationInformation} instance holding information about the
	 *         logged-in session
	 *
	 * @throws AccessControlException in case the login fails
	 */
	public ESAuthenticationInformation verifyUser(String username, String password,
		ESClientVersionInfo clientVersionInfo) throws AccessControlException {

		checkClientVersion(clientVersionInfo);
		final String preparedPassword = preparePassword(password);
		final String superUser = ServerConfiguration.getProperties()
			.getProperty(ServerConfiguration.SUPER_USER, ServerConfiguration.SUPER_USER_DEFAULT);

		if (verifySuperUser(username, preparedPassword)) {
			return createAuthInfo(username);
		} else if (!username.equals(superUser) && verifyPassword(username, preparedPassword)) {
			return createAuthInfo(username);
		}

		throw new AccessControlException();
	}

	private ESAuthenticationInformation createAuthInfo(String username) throws AccessControlException {
		final AuthenticationInformation createAuthenticationInfo = createAuthenticationInfo();
		createAuthenticationInfo.setResolvedACUser(ModelUtil.clone(findUser(username)));
		return createAuthenticationInfo.toAPI();
	}

	/**
	 * Find the user with the given user name.
	 *
	 * @param username
	 *            the name of the user to be found
	 * @return the found user
	 * @throws AccessControlException in case the user hasn't been found
	 */
	protected ACUser findUser(String username) throws AccessControlException {

		final Boolean ignoreCase = Boolean.parseBoolean(ServerConfiguration.getProperties().getProperty(
			ServerConfiguration.AUTHENTICATION_MATCH_USERS_IGNORE_CASE, Boolean.FALSE.toString()));

		synchronized (MonitorProvider.getInstance().getMonitor()) {
			final Optional<ACUser> user = findExistingUser(orgUnitProvider, username, ignoreCase);
			if (user.isPresent()) {
				return user.get();
			}
			throw new AccessControlException();
		}
	}

	/**
	 * Checks the given {@link ESOrgUnitProvider} to find a user with the given username.
	 *
	 * @param orgUnitProvider the provider
	 * @param username the username to find
	 * @param ignoreCase <code>true</code> if the username is not case-sensitive, <code>false</code> otherwise
	 * @return the user, if found
	 * @since 1.8.1
	 */
	protected static final Optional<ACUser> findExistingUser(ESOrgUnitProvider orgUnitProvider, String username,
		final Boolean ignoreCase) {
		final Set<ESUser> users = orgUnitProvider.getUsers();
		final Set<ACUser> internal = APIUtil.toInternal(users);
		for (final ACUser user : internal) {
			if (ignoreCase) {
				if (user.getName().equalsIgnoreCase(username)) {
					return Optional.of(user);
				}
			} else {
				if (user.getName().equals(username)) {
					return Optional.of(user);
				}
			}
		}
		return Optional.absent();
	}

}
