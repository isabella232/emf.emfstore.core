/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk, Max Hohenegger - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.exceptions;

import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Is thrown if an operation is cancelled.
 * 
 * @author wesendon
 */
@SuppressWarnings("serial")
public class CancelOperationException extends ESException {

	/**
	 * Constructor.
	 * 
	 * @param message reason why this exception will be thrown
	 */
	public CancelOperationException(String message) {
		super(message);
	}

}