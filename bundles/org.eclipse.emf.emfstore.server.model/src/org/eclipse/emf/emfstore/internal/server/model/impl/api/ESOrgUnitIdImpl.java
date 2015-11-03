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
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.server.model.ESOrgUnitId;

/**
 * The API implementation class for an {@link ESOrgUnitId}.
 *
 * @author emueller
 *
 */
public class ESOrgUnitIdImpl extends AbstractAPIImpl<ESOrgUnitIdImpl, ACOrgUnitId>implements ESOrgUnitId {

	/**
	 * Constructor.
	 *
	 * @param orgUnitId
	 *            the internal representation of an ID of an organizational unit
	 */
	public ESOrgUnitIdImpl(ACOrgUnitId orgUnitId) {
		super(orgUnitId);
	}

}
