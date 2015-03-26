/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
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

import org.eclipse.osgi.util.NLS;

/**
 * Verifiers related messages.
 *
 * @author emueller
 * @generated
 */
public final class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.emf.emfstore.internal.server.accesscontrol.authentication.verifiers.messages"; //$NON-NLS-1$
	public static String LDAPVerifier_DistinguishedNameNotFound;
	public static String LDAPVerifier_InvalidResults;
	public static String LDAPVerifier_LDAPDirectoryNotFound;
	public static String LDAPVerifier_LoginFailed;
	public static String LDAPVerifier_SearchFailed;
	public static String SimplePropertyFileVerifier_CouldNotLoadPasswordFile;
	public static String SimplePropertyFileVerifier_HashMayNotBeNull;
	public static String VersionVerifier_AcceptedVersions;
	public static String VersionVerifier_ClientVersion;
	public static String VersionVerifier_NoClientVersionReceived;
	public static String VersionVerifier_NoServerVersionsSupplied;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
