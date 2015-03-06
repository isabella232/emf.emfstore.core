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
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;

/**
 * Interfaces for types that have an instance of an {@link AbstractChangePackage}.
 *
 * author emueller
 *
 */
public interface HasChangePackage {

	/**
	 * Set the given change package.
	 *
	 * @param changePackage
	 *            the new change package to be set
	 */
	void setChangePackage(AbstractChangePackage changePackage);

	/**
	 * Returns the normalized {@link URI} of the change package.
	 *
	 * @return the URI of the change package
	 */
	URI getChangePackageUri();

}
