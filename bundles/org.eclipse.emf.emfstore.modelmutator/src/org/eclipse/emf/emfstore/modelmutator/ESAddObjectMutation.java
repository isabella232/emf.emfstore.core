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
 * A mutation, which adds a new object into the model.
 * 
 * @author Philip Langer
 * @author emueller
 * @since 2.0
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESAddObjectMutation extends ESContainmentChangeMutation<ESAddObjectMutation> {

	/**
	 * Sets the object to be added by this mutation.
	 * 
	 * @param eObject
	 *            the object to be added
	 * @return this mutation
	 */
	ESAddObjectMutation setEObjectToAdd(EObject eObject);

	/**
	 * Returns the object added or to be added by this mutation.
	 * 
	 * @return the added or to-be-added object
	 */
	EObject getEObjectToAdd();

}
