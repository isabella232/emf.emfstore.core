/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl.api;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.emfstore.client.util.ESCopier;

/**
 * The default {@link ESCopier} that does not copy by using
 * original references.
 *
 */
public class DefaultCopier implements ESCopier {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.util.ESCopier#shouldHandle(org.eclipse.emf.ecore.EObject)
	 */
	public int shouldHandle(EObject eObject) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.util.ESCopier#copy(org.eclipse.emf.ecore.EObject)
	 */
	public EObject copy(EObject eObject) {
		final Copier copier = new Copier(true, false);
		final EObject copiedElement = copier.copy(eObject);
		copier.copyReferences();
		return copiedElement;
	}

}
