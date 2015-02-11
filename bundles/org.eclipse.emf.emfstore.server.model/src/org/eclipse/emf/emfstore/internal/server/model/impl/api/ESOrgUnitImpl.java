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
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.server.model.ESOrgUnit;

/**
 * The API implementation class for an {@link ESOrgUnit}.
 *
 * @param <E> the concrete type of the organizational unit
 *
 * @author emueller
 *
 */
public class ESOrgUnitImpl<E extends ESOrgUnit> extends AbstractAPIImpl<E, ACOrgUnit<E>> implements ESOrgUnit {

	/**
	 * Constructor.
	 *
	 * @param orgUnit
	 *            the internal representation of an organizational unit
	 */
	public ESOrgUnitImpl(ACOrgUnit<E> orgUnit) {
		super(orgUnit);
	}

}
