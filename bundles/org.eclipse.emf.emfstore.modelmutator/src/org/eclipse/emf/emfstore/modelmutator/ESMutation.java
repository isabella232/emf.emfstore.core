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
 * Abstract mutation acting as a common super class of specific implementations of mutations.
 * 
 * @author Philip Langer
 * @author emueller
 * @since 2.0
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESMutation {

	/**
	 * Applies this mutation and returns whether it succeeded in being applied.
	 * 
	 * @throws ESMutationException thrown if the mutation failed.
	 */
	void apply() throws ESMutationException;

}
