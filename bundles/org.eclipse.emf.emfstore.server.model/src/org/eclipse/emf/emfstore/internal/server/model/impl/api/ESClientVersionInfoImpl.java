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
import org.eclipse.emf.emfstore.internal.server.model.ClientVersionInfo;
import org.eclipse.emf.emfstore.server.model.ESClientVersionInfo;

/**
 * @author emueller
 *
 */
public class ESClientVersionInfoImpl extends AbstractAPIImpl<ESClientVersionInfo, ClientVersionInfo>
	implements ESClientVersionInfo {

	/**
	 * Constructor.
	 *
	 * @param clientVersionInfo
	 *            the internal representation of the client version
	 */
	public ESClientVersionInfoImpl(ClientVersionInfo clientVersionInfo) {
		super(clientVersionInfo);
	}

}
