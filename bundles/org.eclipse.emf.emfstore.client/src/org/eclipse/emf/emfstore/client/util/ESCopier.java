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
package org.eclipse.emf.emfstore.client.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * An interface that enables to specify a different copy behavior
 * than standard {@link EcoreUtil#copy(EObject)}.
 * The copy of an {@link EObject} should be self-contained, if possible, i.e.
 * there should be no references pointing outside the copied containment tree.
 *
 * @since 1.8
 *
 */
public interface ESCopier {

	/**
	 * Whether this copier wants to copy the given {@link EObject}.
	 * 
	 * @param eObject the {@link EObject} to be copied
	 * @return an integer that specifies how critical it is that the copier handles the given
	 *         object. The copier that specifies the highest priority will be used to copy the object.
	 */
	int shouldHandle(EObject eObject);

	/**
	 * Copy the given {@link EObject}.
	 *
	 * @param eObject the {@link EObject} to be copied
	 * @return the copied {@link EObject}
	 */
	EObject copy(EObject eObject);

}
