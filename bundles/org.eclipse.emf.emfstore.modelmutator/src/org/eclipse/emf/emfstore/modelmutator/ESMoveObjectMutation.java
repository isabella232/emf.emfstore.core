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
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A mutation, which moves an object from one container into another.
 * 
 * @author Philip Langer
 * @author
 * @since 2.0
 * 
 * @noextend This interface is not intended to be extended by clients
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESMoveObjectMutation extends ESContainmentChangeMutation<ESMoveObjectMutation> {

	/**
	 * Sets the {@link EStructuralFeature} of a source object from which this mutation will move or moved an object.
	 * 
	 * @param sourceFeature The feature of the source object through which the moved or to-be-moved object was or is
	 *            contained.
	 * 
	 * @return this mutation
	 */
	ESMoveObjectMutation setSourceFeature(EStructuralFeature sourceFeature);

	/**
	 * Returns the selected or set source object from which this mutation will move or moved an object.
	 * 
	 * @return the source object of the moved or to-be-moved object
	 */
	EObject getSourceObject();

	/**
	 * Returns the {@link EObject} that will be or has been moved.
	 * 
	 * @return the moved or to-be-moved object
	 */
	EObject getEObjectToMove();

	/**
	 * Sets the {@link EObject} to be used as source object from which this mutation will move an object.
	 * 
	 * @param sourceObject
	 *            the source object to be moved from.
	 * 
	 * @return this mutation
	 */
	ESMoveObjectMutation setSourceObject(EObject sourceObject);

	/**
	 * Sets the {@link EObject} to be moved.
	 * 
	 * @param eObjectToMove
	 *            the object to be moved
	 * 
	 * @return this mutation
	 */
	ESMoveObjectMutation setEObjectToMove(EObject eObjectToMove);

}
