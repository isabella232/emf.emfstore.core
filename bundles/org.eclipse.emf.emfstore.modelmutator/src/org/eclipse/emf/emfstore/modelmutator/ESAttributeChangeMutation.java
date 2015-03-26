/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Phliip Langer - initial API and implementation
 * Edgar Mueller - API layer
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

/**
 * A mutation, which adds, deletes, or reorders an attribute value of an object.
 *
 * @author Philip Langer
 * @author emueller
 * @since 2.0
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESAttributeChangeMutation extends ESStructuralFeatureMutation<ESAttributeChangeMutation> {

	/**
	 * Sets the value that should be used when changing the attribute.
	 *
	 * @param newValue
	 *            the value of the attribute
	 * @return this mutation
	 */
	ESAttributeChangeMutation setNewValue(Object newValue);

	/**
	 * Sets the change mode for this attribute change.
	 *
	 * @param mode
	 *            the random change mode to be set
	 * @return this mutation
	 */
	ESAttributeChangeMutation setRandomChangeMode(ESRandomChangeMode mode);
}
