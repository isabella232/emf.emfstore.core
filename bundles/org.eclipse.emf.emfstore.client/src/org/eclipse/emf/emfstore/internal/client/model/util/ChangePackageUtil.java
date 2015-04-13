/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.util;

import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.FileBasedChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;

/**
 * Change package helper class.
 *
 * @author emueller
 *
 */
public final class ChangePackageUtil {

	private ChangePackageUtil() {

	}

	/**
	 * Creates a new {@link AbstractChangePackage} depending on the client configuration behavior whether
	 * to create in-memory of file-based change packages.
	 *
	 * @return the created change package
	 */
	public static AbstractChangePackage createChangePackage() {
		final boolean useFileBasedChangePackage = Configuration.getClientBehavior().useFileBasedChangePackage();

		if (useFileBasedChangePackage) {
			final FileBasedChangePackage fileBasedChangePackage = VersioningFactory.eINSTANCE
				.createFileBasedChangePackage();
			fileBasedChangePackage.initialize(FileUtil.createLocationForTemporaryChangePackage());
			return fileBasedChangePackage;
		}

		return VersioningFactory.eINSTANCE.createChangePackage();
	}

}
