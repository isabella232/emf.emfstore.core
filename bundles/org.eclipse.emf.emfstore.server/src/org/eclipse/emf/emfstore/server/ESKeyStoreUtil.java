/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Marco.vanMeegen - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server;

import java.security.KeyStore;

import org.eclipse.emf.emfstore.internal.server.connection.ServerKeyStoreManager;
import org.eclipse.emf.emfstore.internal.server.exceptions.ServerKeyStoreException;
import org.eclipse.emf.emfstore.server.exceptions.ESInitSSLException;

/**
 * 
 * Utility to expose SSL parameters to ESWebServerProviders.
 * 
 * @author Marco.vanMeegen
 * @since 1.3
 */
public final class ESKeyStoreUtil {

	private ESKeyStoreUtil() {

	}

	/**
	 * Returns the keystore used by the server.
	 * 
	 * @return the server's keystore
	 * @throws ESInitSSLException in case an error occurs while fetching the keystore
	 */
	public static KeyStore getKeyStore() throws ESInitSSLException {
		try {
			return ServerKeyStoreManager.getInstance().getKeyStore();
		} catch (final ServerKeyStoreException ex) {
			// remap to external exception
			throw new ESInitSSLException(ex.getMessage(), ex.getCause());
		}

	}

	/**
	 * Returns the password of the keystore.
	 * 
	 * @return the password of the keystore
	 */
	public static String getKeyStorePassword() {
		return new String(ServerKeyStoreManager.getInstance().getKeyStorePassword());
	}
}
