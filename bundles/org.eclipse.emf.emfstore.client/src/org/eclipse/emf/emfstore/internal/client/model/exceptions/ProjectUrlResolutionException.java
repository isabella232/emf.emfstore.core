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
 * Represents exception in project URL resolution. The project cannot be resolved in the current workspace content. The
 * project the URL refers to has not been checked out yet probably.
 * 
 * @author koegel
 */
@SuppressWarnings("serial")
public class ProjectUrlResolutionException extends Exception {

	private static final String EXCEPTION_MESSAGE = Messages.ProjectUrlResolutionException_ResolutionFailed;

	/**
	 * Constructor.
	 */
	public ProjectUrlResolutionException() {
		super(EXCEPTION_MESSAGE);
	}

}
