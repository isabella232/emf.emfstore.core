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

/**
 * A mutation, which deletes a new object from the model.
 * 
 * @author Philip Langer
 * @author emueller
 * 
 * @since 2.0
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESDeleteObjectMutation extends ESContainmentChangeMutation<ESDeleteObjectMutation> {

	/**
	 * Sets the maximum number of containments that the object selected for deletion may contain.
	 * 
	 * @param maxNumberOfContainments
	 *            the maximum number of containments of the object to be deleted.
	 * 
	 * @return this mutation
	 */
	ESDeleteObjectMutation setMaxNumberOfContainments(int maxNumberOfContainments);

}
