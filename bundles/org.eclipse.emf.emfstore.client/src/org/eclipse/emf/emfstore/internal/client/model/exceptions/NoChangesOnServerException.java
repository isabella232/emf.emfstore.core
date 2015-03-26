/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.exceptions;

/**
 * Indicates that there are no changes on the server.
 *
 * @author koegel
 */
@SuppressWarnings("serial")
public class NoChangesOnServerException extends WorkspaceException {

	private static final String MESSAGE = Messages.NoChangesOnServerException_NoChangesOnServer;

	/**
	 * Constructor.
	 */
	public NoChangesOnServerException() {
		super(MESSAGE);
	}

}
