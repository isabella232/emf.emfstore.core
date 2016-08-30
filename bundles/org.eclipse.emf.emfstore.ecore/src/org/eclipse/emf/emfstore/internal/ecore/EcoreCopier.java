/*******************************************************************************
 * Copyright (c) 2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.ecore;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.client.util.ESCopier;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;

/**
 * Copier responsible for copying {@link EObject}s that are considered
 * 'Singletons', that is, {@link ETypedElement}, by EMFStore.
 *
 *
 */
public class EcoreCopier implements ESCopier {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.util.ESCopier#shouldHandle(org.eclipse.emf.ecore.EObject)
	 */
	public int shouldHandle(EObject eObject) {
		if (ModelUtil.isSingleton(eObject)) {
			return 1;
		}
		return -1;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.util.ESCopier#copy(org.eclipse.emf.ecore.EObject)
	 */
	public EObject copy(EObject eObject) {
		return EcoreUtil.copy(eObject);
	}

}
