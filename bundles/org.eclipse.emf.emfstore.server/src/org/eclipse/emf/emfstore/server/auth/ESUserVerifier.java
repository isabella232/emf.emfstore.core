/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.auth;

import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESClientVersionInfo;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * Controller for the Authentication of users.
 *
 * @author emueller
 * @since 1.5
 */
public interface ESUserVerifier {

	/**
	 * Tries to login the given user.
	 *
	 * @param username
	 *            the user name as determined by the client
	 * @param password
	 *            the password as entered by the client
	 * @param clientVersionInfo
	 *            the version of the client
	 * @return an {@link ESAuthenticationInformation} instance holding information about the
	 *         logged-in session. Note that the return {@link ESAuthenticationInformation} must
	 *         hold an {@link org.eclipse.emf.emfstore.server.model.ESUser ESUser}
	 *
	 * @throws AccessControlException in case the login fails
	 */
	ESAuthenticationInformation verifyUser(String username, String password,
		ESClientVersionInfo clientVersionInfo)
		throws AccessControlException;

	/**
	 * Initializes this verifier.
	 *
	 * @param orgUnitProvider
	 *            the {@link ESOrgUnitProvider} that can be used to obtain organizational units
	 */
	void init(ESOrgUnitProvider orgUnitProvider);

}
