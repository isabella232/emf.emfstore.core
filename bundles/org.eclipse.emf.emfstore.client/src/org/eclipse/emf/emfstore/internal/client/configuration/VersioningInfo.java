/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk
 * Edgar Mueller
 * Maximilian Koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.configuration;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.client.provider.ESClientVersionProvider;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionElement;
import org.eclipse.emf.emfstore.common.extensionpoint.ESExtensionPoint;
import org.eclipse.emf.emfstore.internal.server.model.ClientVersionInfo;
import org.osgi.framework.Bundle;

/**
 * Contains runtime version information about the currently used client.
 *
 * @author emueller
 * @author ovonwesen
 * @author mkoegel
 */
public class VersioningInfo {

	private static final String CLIENT_NAME = "emfstore eclipse client"; //$NON-NLS-1$

	/**
	 * Get the client version as specified
	 * in the org.eclipse.emf.emfstore.internal.client manifest file.
	 *
	 * @return the client version number
	 */
	@SuppressWarnings("cast")
	public ClientVersionInfo getClientVersion() {
		final ClientVersionInfo clientVersionInfo = org.eclipse.emf.emfstore.internal.server.model.ModelFactory.eINSTANCE
			.createClientVersionInfo();
		clientVersionInfo.setName(CLIENT_NAME);

		String versionId;
		final ESExtensionElement version = new ESExtensionPoint("org.eclipse.emf.emfstore.client.clientVersion") //$NON-NLS-1$
			.setThrowException(false).getFirst();

		if (version != null) {
			final ESClientVersionProvider versionProvider = version.getClass("class", ESClientVersionProvider.class); //$NON-NLS-1$
			clientVersionInfo.setName(versionProvider.getName());
			clientVersionInfo.setVersion(versionProvider.getVersion());
			return clientVersionInfo;
		}

		final Bundle emfStoreBundle = Platform.getBundle("org.eclipse.emf.emfstore.client"); //$NON-NLS-1$
		versionId = (String) emfStoreBundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		clientVersionInfo.setVersion(versionId);

		return clientVersionInfo;
	}

	/**
	 * Determine if this is a release version or not.
	 *
	 * @return {@code true} if it is a release version, {@code false} otherwise
	 */
	public boolean isReleaseVersion() {
		return !isInternalReleaseVersion() && !getClientVersion().getVersion().endsWith("qualifier"); //$NON-NLS-1$
	}

	/**
	 * Determines if this is an internal release or not.
	 *
	 * @return {@code true} if it is an internal release, {@code false} otherwise
	 */
	public boolean isInternalReleaseVersion() {
		return getClientVersion().getVersion().endsWith("internal"); //$NON-NLS-1$
	}

	/**
	 * Determines if this is an developer version or not.
	 *
	 * @return {@code true} if it is a developer version, {@code false} otherwise
	 */
	public boolean isDeveloperVersion() {
		return !isReleaseVersion() && !isInternalReleaseVersion();
	}

}
