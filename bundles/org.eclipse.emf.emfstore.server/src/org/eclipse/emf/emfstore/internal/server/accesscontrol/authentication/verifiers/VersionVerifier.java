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

import java.util.regex.Pattern;

import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.ClientVersionOutOfDateException;
import org.eclipse.emf.emfstore.internal.server.model.ClientVersionInfo;

/**
 * <p>
 * Utility class to verify the client version. When specifying the allowed versions, clients may either specify an array
 * containing all allowed version numbers or specify a single wildcard version.
 * </p>
 * 
 * <p>
 * A wildcard version is specified via the '*' letter. All other letters known from regular expressions are not
 * considered.
 * </p>
 * 
 * <p>
 * For instance the wildcard version 1.2.3.x would accept all 1.2.3 versions independently of the actual qualifier where
 * {@code x} stand for a qualifier. Multiple wildcard versions are <b>not</b> allowed
 * </p>
 * 
 * 
 * @author emueller
 * 
 */
public final class VersionVerifier {

	private VersionVerifier() {
	}

	/**
	 * Verifies the client version against a set of allowed versions.
	 * 
	 * @param versions
	 *            the list of accepted versions or a single wildcard version like '1.2.3.*'
	 * 
	 * @param clientVersionInfo
	 *            the client version to verify
	 * @throws ClientVersionOutOfDateException
	 *             in case the client version is out of date
	 */
	public static void verify(String[] versions, ClientVersionInfo clientVersionInfo)
		throws ClientVersionOutOfDateException {

		if (clientVersionInfo == null) {
			throw new ClientVersionOutOfDateException(Messages.VersionVerifier_NoClientVersionReceived);
		}

		if (versions == null) {
			final String msg = Messages.VersionVerifier_NoServerVersionsSupplied;
			ModelUtil.logWarning(msg, new ClientVersionOutOfDateException(msg));
			return;
		}

		if (isWildcardVersion(versions)) {
			matchesWildcard(versions[0], clientVersionInfo);
		} else {
			for (final String version : versions) {
				if (matchesClientVersion(version, clientVersionInfo) || matchesAny(version)) {
					return;
				}
			}

			final StringBuffer acceptedVersions = new StringBuffer();

			for (final String str : versions) {
				if (versions.length == 1) {
					acceptedVersions.append(str + ". "); //$NON-NLS-1$
				} else {
					acceptedVersions.append(str + ", "); //$NON-NLS-1$
				}
			}

			acceptedVersions.replace(acceptedVersions.length() - 2, acceptedVersions.length(), "."); //$NON-NLS-1$

			throw new ClientVersionOutOfDateException(Messages.VersionVerifier_ClientVersion
				+ clientVersionInfo.getVersion()
				+ " - " //$NON-NLS-1$
				+ Messages.VersionVerifier_AcceptedVersions + acceptedVersions);
		}
	}

	private static void matchesWildcard(final String version, final ClientVersionInfo clientVersionInfo)
		throws ClientVersionOutOfDateException {
		final int index = version.lastIndexOf('*');
		final String quoted = Pattern.quote(version.substring(0, index));
		final String end = Pattern.quote(version.substring(index + 1, version.length()));
		if (!clientVersionInfo.getVersion().matches(quoted + ".*" + end)) { //$NON-NLS-1$
			throw new ClientVersionOutOfDateException("Client version: " + clientVersionInfo.getVersion() //$NON-NLS-1$
				+ " - " //$NON-NLS-1$
				+ "Accepted versions: " + version); //$NON-NLS-1$
		}
	}

	private static boolean isWildcardVersion(String[] versions) {
		return versions.length == 1 && versions[0].contains("*"); //$NON-NLS-1$
	}

	private static boolean matchesClientVersion(final String version, final ClientVersionInfo clientVersionInfo) {
		return version.equals(clientVersionInfo.getVersion());
	}

	private static boolean matchesAny(final String version) {
		return version.equals(ServerConfiguration.ACCEPTED_VERSIONS_ANY);
	}
}
