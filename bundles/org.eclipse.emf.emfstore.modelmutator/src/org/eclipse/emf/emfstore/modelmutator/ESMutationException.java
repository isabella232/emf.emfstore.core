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
 * The class encapsulates mutation-specific exceptions if mutations cannot succeed.
 * 
 * @author Philip Langer
 * @since 2.0
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ESMutationException extends Exception {

	private static final long serialVersionUID = 5880905487870618741L;

	/**
	 * Creates a new mutation exception.
	 */
	public ESMutationException() {
		super();
	}

	/**
	 * Creates a new mutation exception with the specified error {@code message}.
	 * 
	 * @param message The error message to be set.
	 */
	public ESMutationException(String message) {
		super(message);
	}

}
