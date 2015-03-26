/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier
 ******************************************************************************/
package org.eclipse.emf.emfstore.common;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Interface for resource set provider.
 *
 * @author jfaltermeier
 * @since 1.1
 *
 */
public interface ESResourceSetProvider {

	/**
	 * Returns ResourceSet with load and save options configured and including
	 * {@link org.eclipse.emf.emfstore.internal.common.ResourceFactoryRegistry ResourceFactoryRegistry} and
	 * {@link org.eclipse.emf.ecore.resource.URIConverter URIConverter}.
	 *
	 * @return the ResourceSet
	 */
	ResourceSet getResourceSet();
}
