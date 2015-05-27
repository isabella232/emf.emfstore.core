/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;

import com.google.common.base.Predicate;

/**
 * @author Edgar
 *
 */
public class HasRolePredicate implements Predicate<Role> {

	private final Class<? extends Role> expectedRole;

	public HasRolePredicate(Class<? extends Role> expectedRole) {
		this.expectedRole = expectedRole;
	}

	public boolean apply(final Role role) {
		return expectedRole.isInstance(role);
	}

}
