/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.auth;

/**
 * A PasswordHash Generator is able to generate a hash using a newly created salt for a given password. Moreover it is
 * able to verify if a password matches a stored hash/salt.
 *
 * @author Johannes Faltermeier
 * @since 1.9
 *
 */
public interface ESPasswordHashGenerator {

	/**
	 * This method will hash the password using a newly generated salt. Hash and salt will be returned.
	 *
	 * @param password the password to hash
	 * @return a {@link ESHashAndSalt}
	 */
	ESHashAndSalt hashPassword(String password);

	/**
	 * Verifies if the given password matches the hash/salt.
	 *
	 * @param password the password to verify
	 * @param hash the saved hash
	 * @param salt the saved salt
	 * @return <code>true</code> if password matches, <code>false</code> otherwise
	 */
	boolean verifyPassword(String password, String hash, String salt);

	/**
	 * Wrapper interface for a hash/salt pair.
	 *
	 * @author Johannes Faltermeier
	 *
	 */
	interface ESHashAndSalt {
		/**
		 * A separator char helping to separate hashes and salts.
		 */
		String SEPARATOR = " "; //$NON-NLS-1$

		/**
		 * @return the hash
		 */
		String getHash();

		/**
		 *
		 * @return the salt
		 */
		String getSalt();
	}

}
