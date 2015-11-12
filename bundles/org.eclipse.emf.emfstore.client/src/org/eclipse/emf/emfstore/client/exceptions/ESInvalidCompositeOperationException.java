/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.exceptions;

/**
 * Thrown when an operation is executed on a {@link org.eclipse.emf.emfstore.client.ESCompositeOperationHandle
 * ESCompositeOperationHandle} after recording has ended or was aborted.
 *
 * @author Johannes Faltermeier
 * @since 1.7
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 *
 */
public class ESInvalidCompositeOperationException extends Exception {

	private static final long serialVersionUID = -6381478147210693404L;

	/**
	 * @param message the message describing the error
	 */
	public ESInvalidCompositeOperationException(String message) {
		super(message);
	}

}
