/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * Calls all available verifiers and tries to verify the given credentials.
 *
 * @author wesendon
 */
public class UserVerifierChain extends UserVerifier {

	// TODO: wrong type
	private final ArrayList<UserVerifier> verifiers;

	/**
	 * Constructs an empty verifier chain.
	 *
	 * @param orgUnitProvider
	 *            an {@link ESOrgUnitProvider} for finding users
	 */
	public UserVerifierChain(ESOrgUnitProvider orgUnitProvider) {
		super(orgUnitProvider);
		verifiers = new ArrayList<UserVerifier>();
	}

	/**
	 * Returns the list of verifier. can be used to add and remove verifier.
	 *
	 * @return list of verifier
	 */
	public List<UserVerifier> getVerifiers() {
		return verifiers;
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
		for (final UserVerifier verifier : verifiers) {
			if (verifier.verifyPassword(username, password)) {
				return true;
			}
		}
		return false;
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
