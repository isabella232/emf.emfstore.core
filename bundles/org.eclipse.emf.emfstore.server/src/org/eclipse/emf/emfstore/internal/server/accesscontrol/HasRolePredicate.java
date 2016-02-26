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
package org.eclipse.emf.emfstore.internal.server.accesscontrol;

import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;

import com.google.common.base.Predicate;

/**
 * Predicate that checks {@link Role} matches an expected one.
 *
 * @author emueller
 *
 */
public class HasRolePredicate implements Predicate<Role> {

	private final Class<? extends Role> expectedRole;

	/**
	 * Constructor.
	 *
	 * @param expectedRole the expected {@link Role} to be matched
	 */
	public HasRolePredicate(Class<? extends Role> expectedRole) {
		this.expectedRole = expectedRole;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see com.google.common.base.Predicate#apply(java.lang.Object)
	 */
	public boolean apply(final Role role) {
		return expectedRole.isInstance(role);
	}

}
