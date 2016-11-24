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

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.AccessControl;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator;
import org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * @author emueller
 *
 */
public class EMFModelUserVerifier extends UserVerifier {

	/**
	 * Constructor.
	 *
	 * @param orgUnitProvider
	 *            provides access to users and groups
	 */
	public EMFModelUserVerifier(ESOrgUnitProvider orgUnitProvider) {
		super(orgUnitProvider);
		migrateToHashedPasswordIfNeeded(orgUnitProvider);

	}

	private void migrateToHashedPasswordIfNeeded(ESOrgUnitProvider orgUnitProvider) {
		if (!ServerConfiguration.isUserPasswordMigrationRequired()) {
			return;
		}
		final ESPasswordHashGenerator passwordHashGenerator = AccessControl.getESPasswordHashGenerator();
		final Set<ACUser> users = APIUtil.toInternal(orgUnitProvider.getUsers());
		for (final ACUser user : users) {
			if (user.getPassword() == null) {
				continue;
			}
			final ESHashAndSalt hashAndSalt = passwordHashGenerator.hashPassword(user.getPassword());
			user.setPassword(hashAndSalt.getHash() + ESHashAndSalt.SEPARATOR + hashAndSalt.getSalt());
		}
		try {
			orgUnitProvider.save();
		} catch (final IOException ex) {
			ModelUtil.logException("Migration of user passwords failed", ex); //$NON-NLS-1$
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.PasswordVerifier#verifyPassword(org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	protected boolean verifyPassword(String username, String password)
		throws AccessControlException {
		final ACUser resolvedUser = findUser(username);
		if (resolvedUser == null) {
			// TODO: throw UserNotFoundException? -> Signature
			return false;
		}

		final String userPassword = resolvedUser.getPassword();

		if (userPassword == null) {
			if (StringUtils.isBlank(password)) {
				// no password set
				return true;
			}

			return false;
		}

		final ESPasswordHashGenerator passwordHashGenerator = AccessControl.getESPasswordHashGenerator();
		final int separatorIndex = userPassword.indexOf(ESHashAndSalt.SEPARATOR);
		final String hash = userPassword.substring(0, separatorIndex);
		final String salt = userPassword.substring(separatorIndex + 1);
		return passwordHashGenerator.verifyPassword(password, hash, salt);

	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESUserVerifier#init(org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider)
	 */
	public void init(ESOrgUnitProvider orgUnitProvider) {

	}
}
