/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * planger - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.mutation;

/**
 * The class encapsulates mutation-specific exceptions if mutations cannot succeed.
 *
 * @author Philip Langer
 *
 */
public class MutationException extends Exception {

	private static final long serialVersionUID = 5880905487870618741L;

	/**
	 * Creates a new mutation exception.
	 */
	public MutationException() {
		super();
	}

	/**
	 * Creates a new mutation exception with the specified error {@code message}.
	 *
	 * @param message The error message to be set.
	 */
	public MutationException(String message) {
		super(message);
	}

}
