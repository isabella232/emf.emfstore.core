/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Langer - initial API and implementation
 * Edgar Mueller - API layer
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import org.eclipse.emf.ecore.EObject;

/**
 * A mutation, which changes reference values.
 *
 * @author Philip Langer
 * @author emueller
 * @since 2.0
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESReferenceChangeMutation extends ESStructuralFeatureMutation<ESReferenceChangeMutation> {

	/**
	 * Sets the change mode for this attribute change.
	 *
	 * @param mode
	 *            the random change mode to be set
	 * @return this mutation
	 */
	ESReferenceChangeMutation setRandomChangeMode(ESRandomChangeMode mode);

	/**
	 * Sets the value that should be used when changing the reference.
	 *
	 * @param newValue
	 *            the value of the reference
	 * @return this mutation
	 */
	ESReferenceChangeMutation setNewReferenceValue(EObject newValue);
}
