/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.exceptions;

import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * Exception for indicating that branch related information is missing.
 *
 * @author emueller
 *
 */
@SuppressWarnings("serial")
public class BranchInfoMissingException extends ESException {

	/**
	 * Constructor.
	 *
	 * @param message
	 *            the exception message
	 */
	public BranchInfoMissingException(String message) {
		super(message);
	}

}
