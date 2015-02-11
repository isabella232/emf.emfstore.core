/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * @author emueller
 *
 */
public class EMFModelUserVerifier extends UserVerifier {

	public EMFModelUserVerifier(ESOrgUnitProvider userProvider) {
		super(userProvider);
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

		return userPassword.equals(password);
	}
}
