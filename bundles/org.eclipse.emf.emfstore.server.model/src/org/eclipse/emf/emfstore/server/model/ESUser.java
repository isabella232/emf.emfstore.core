/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.model;

/**
 * A single EMFStore user.
 *
 * @author emueller
 * @since 1.5
 *
 */
public interface ESUser extends ESOrgUnit {

	/**
	 * Returns the name of the user.
	 *
	 * @return the name of the user
	 */
	String getName();

	/**
	 * Returns the hashed password of the user.
	 *
	 * @return the hashed password of the user.
	 */
	String getPassword();

}
