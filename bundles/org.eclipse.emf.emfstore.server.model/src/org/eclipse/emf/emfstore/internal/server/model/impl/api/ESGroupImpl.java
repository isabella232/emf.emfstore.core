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

import java.util.Collection;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.server.model.ESGroup;
import org.eclipse.emf.emfstore.server.model.ESRole;

/**
 * The API implementation class for an {@link ESGroup}.
 *
 * @author emueller
 *
 */
public class ESGroupImpl extends ESOrgUnitImpl<ESGroup>implements ESGroup {

	/**
	 * Constructor.
	 *
	 * @param acGroup
	 *            the internal representation of a group
	 */
	public ESGroupImpl(ACGroup acGroup) {
		super(acGroup);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESGroup#getRoles()
	 */
	public Collection<? extends ESRole> getRoles() {
		return APIUtil.toExternal(toInternalAPI().getRoles());
	}
}
