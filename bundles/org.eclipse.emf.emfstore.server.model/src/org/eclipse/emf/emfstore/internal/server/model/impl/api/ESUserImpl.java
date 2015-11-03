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
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.model.ESUser;

/**
 * The API implementation class for an {@link ESUser}.
 *
 * @author emueller
 *
 */
public class ESUserImpl extends ESOrgUnitImpl<ESUser>implements ESUser {

	/**
	 * Constructor.
	 *
	 * @param acUser
	 *            the internal representation of an user
	 */
	public ESUserImpl(ACUser acUser) {
		super(acUser);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESUser#getName()
	 */
	public String getName() {
		return toInternalAPI().getName();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESUser#getPassword()
	 */
	public String getPassword() {
		final ACUser acUser = (ACUser) toInternalAPI();
		return acUser.getPassword();
	}

}
