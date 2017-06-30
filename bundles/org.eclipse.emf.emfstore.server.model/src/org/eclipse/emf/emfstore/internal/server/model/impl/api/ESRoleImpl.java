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

import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.server.model.ESRole;

/**
 * The API implementation class for an {@link ESRole}.
 *
 * @author emueller
 *
 */
public class ESRoleImpl extends AbstractAPIImpl<ESRole, Role> implements ESRole {

	/**
	 * Constructor.
	 *
	 * @param role
	 *            the internal representation of a role
	 */
	public ESRoleImpl(Role role) {
		super(role);
	}

}
