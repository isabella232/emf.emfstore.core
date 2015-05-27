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
package org.eclipse.emf.emfstore.server.auth;

/**
 * Enum for all available {@link org.eclipse.emf.emfstore.server.auth.ESAuthenticationControlType}s.
 *
 * @author emueller
 * @since 1.5
 *
 */
public enum ESAuthenticationControlType {

	/**
	 * A verifier that uses a simple property file for authentication.
	 */
	spfv {
		/**
		 * {@inheritDoc}
		 *
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "spfv"; //$NON-NLS-1$
		}
	},
	/**
	 * A verifier that uses LDAP for authentication.
	 */
	ldap {
		/**
		 * {@inheritDoc}
		 *
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "ldap"; //$NON-NLS-1$
		}
	},
	/**
	 * A verifier that uses the password attribute of an
	 * {@link org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser} for authentication.
	 */
	model {
		/**
		 * {@inheritDoc}
		 *
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return "model"; //$NON-NLS-1$
		}
	}
}
