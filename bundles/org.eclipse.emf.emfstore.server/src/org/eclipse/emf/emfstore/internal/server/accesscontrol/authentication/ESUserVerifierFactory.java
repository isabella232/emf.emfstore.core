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
 * Edgar Mueller - refactorings and singleton access
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication;

import java.util.Properties;

import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.EMFModelUserVerifier;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.LDAPUserVerifier;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.SimplePropertyFileUserVerifier;
import org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.UserVerifierChain;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.internal.server.exceptions.InvalidPropertyException;
import org.eclipse.emf.emfstore.server.auth.ESAuthenticationControlType;
import org.eclipse.emf.emfstore.server.auth.ESUserVerifier;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitProvider;

/**
 * Default authentication control factory.
 *
 * @author wesendon
 */
public final class ESUserVerifierFactory {

	private static ESUserVerifierFactory instance = new ESUserVerifierFactory();

	private ESUserVerifierFactory() {
		// private ctor
	}

	/**
	 * The singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static ESUserVerifierFactory getInstance() {
		return instance;
	}

	/**
	 * Creates an {@link ESUserVerifier} based on the given {@link ESAuthenticationControlType}.
	 *
	 * @param authenticationControlType
	 *            the requested type of {@link ESUserVerifier}
	 * @param orgUnitProvider
	 *            provides access to users and groups if necessary
	 * @return the requested {@link ESUserVerifier}
	 * @throws FatalESException in case no fitting {@link ESUserVerifier} can be found or the initialization fails
	 */
	public ESUserVerifier createUserVerifier(
		ESAuthenticationControlType authenticationControlType, ESOrgUnitProvider orgUnitProvider)
		throws FatalESException {

		if (authenticationControlType.equals(ESAuthenticationControlType.ldap)) {
			final UserVerifierChain chain = new UserVerifierChain(orgUnitProvider);
			final Properties properties = ServerConfiguration.getProperties();
			int count = 1;
			while (count != -1) {

				final String ldapUrl = properties.getProperty(ServerConfiguration.AUTHENTICATION_LDAP_PREFIX + "." //$NON-NLS-1$
					+ count
					+ "." + ServerConfiguration.AUTHENTICATION_LDAP_URL); //$NON-NLS-1$
				final String ldapBase = properties.getProperty(ServerConfiguration.AUTHENTICATION_LDAP_PREFIX + "." //$NON-NLS-1$
					+ count
					+ "." + ServerConfiguration.AUTHENTICATION_LDAP_BASE); //$NON-NLS-1$
				final String searchDn = properties.getProperty(ServerConfiguration.AUTHENTICATION_LDAP_PREFIX + "." //$NON-NLS-1$
					+ count
					+ "." + ServerConfiguration.AUTHENTICATION_LDAP_SEARCHDN); //$NON-NLS-1$
				final String authUser = properties.getProperty(ServerConfiguration.AUTHENTICATION_LDAP_PREFIX + "." //$NON-NLS-1$
					+ count
					+ "." + ServerConfiguration.AUTHENTICATION_LDAP_AUTHUSER); //$NON-NLS-1$
				final String authPassword = properties.getProperty(ServerConfiguration.AUTHENTICATION_LDAP_PREFIX + "." //$NON-NLS-1$
					+ count + "." + ServerConfiguration.AUTHENTICATION_LDAP_AUTHPASS); //$NON-NLS-1$

				if (ldapUrl != null && ldapBase != null && searchDn != null) {
					final LDAPUserVerifier ldapVerifier = new LDAPUserVerifier(orgUnitProvider,
						ldapUrl,
						ldapBase,
						searchDn,
						authUser,
						authPassword);
					chain.getVerifiers().add(ldapVerifier);
					count++;
				} else {
					count = -1;
				}
			}

			return chain;

		} else if (authenticationControlType.equals(ESAuthenticationControlType.spfv)) {

			return new SimplePropertyFileUserVerifier(orgUnitProvider, ServerConfiguration.getProperties()
				.getProperty(
					ServerConfiguration.AUTHENTICATION_SPFV_FILEPATH, ServerConfiguration.getDefaultSPFVFilePath()));

		} else if (authenticationControlType.equals(ESAuthenticationControlType.model)) {
			return new EMFModelUserVerifier(orgUnitProvider);

		} else {
			throw new InvalidPropertyException();
		}
	}

}
