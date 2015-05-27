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
package org.eclipse.emf.emfstore.internal.server.core.subinterfaces;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.internal.server.core.AbstractEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.core.AbstractSubEmfstoreInterface;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.server.auth.ESMethod;
import org.eclipse.emf.emfstore.server.auth.ESMethod.MethodId;
import org.osgi.framework.Bundle;

/**
 * Returns the EMFStore version.
 *
 * @author emueller
 */
public class EMFStoreVersionSubInterface extends AbstractSubEmfstoreInterface {

	/**
	 * Constructor.
	 *
	 * @param parentInterface
	 *            the parent interface
	 * @throws FatalESException
	 *             if initialization fails
	 */
	public EMFStoreVersionSubInterface(AbstractEmfstoreInterface parentInterface) throws FatalESException {
		super(parentInterface);
	}

	/**
	 * Returns the EMFStore version.
	 *
	 * @return the EMFStore version
	 */
	@ESMethod(MethodId.GETVERSION)
	public String getVersion() {
		final Bundle emfStoreBundle = Platform.getBundle("org.eclipse.emf.emfstore.server"); //$NON-NLS-1$
		final String versionId = emfStoreBundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
		return versionId;
	}
}
