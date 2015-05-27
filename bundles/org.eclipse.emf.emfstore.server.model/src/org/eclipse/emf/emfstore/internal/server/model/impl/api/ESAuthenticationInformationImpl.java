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
import org.eclipse.emf.emfstore.internal.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ESUser;

import com.google.common.base.Preconditions;

/**
 * Mapping between {@link ESAuthenticationInformation} and {@link AuthenticationInformation}.
 *
 * @author emueller
 */
public class ESAuthenticationInformationImpl
extends AbstractAPIImpl<ESAuthenticationInformation, AuthenticationInformation> implements
ESAuthenticationInformation {

	/**
	 * Constructor.
	 *
	 * @param authInfo
	 *            the internal representation of an authentication information
	 */
	public ESAuthenticationInformationImpl(AuthenticationInformation authInfo) {
		super(authInfo);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation#getUser()
	 */
	public ESUser getUser() {
		// TODO: double check
		return toInternalAPI().getResolvedACUser().toAPI();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESAuthenticationInformation#setUser(org.eclipse.emf.emfstore.server.model.ESUser)
	 */
	public void setUser(ESUser user) {
		Preconditions.checkNotNull(user);
		toInternalAPI().setResolvedACUser(
			(ACUser) ESUserImpl.class.cast(user).toInternalAPI());
	}
}
