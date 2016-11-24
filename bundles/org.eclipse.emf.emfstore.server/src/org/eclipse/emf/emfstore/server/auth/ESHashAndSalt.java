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
 * Default implementation of a {@link ESPasswordHashGenerator.ESHashAndSalt ESHashAndSalt}.
 *
 * @author Johannes Faltermeier
 * @since 1.9
 *
 */
public final class ESHashAndSalt implements ESPasswordHashGenerator.ESHashAndSalt {

	private final String hash;
	private final String salt;

	private ESHashAndSalt(String hash, String salt) {
		this.hash = hash;
		this.salt = salt;
	}

	/**
	 * Factory method for creating {@link ESHashAndSalt}s.
	 *
	 * @param hash the hash
	 * @param salt the salt
	 * @return the instance
	 */
	public static ESHashAndSalt create(String hash, String salt) {
		return new ESHashAndSalt(hash, salt);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt#getHash()
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.auth.ESPasswordHashGenerator.ESHashAndSalt#getSalt()
	 */
	public String getSalt() {
		return salt;
	}

}
