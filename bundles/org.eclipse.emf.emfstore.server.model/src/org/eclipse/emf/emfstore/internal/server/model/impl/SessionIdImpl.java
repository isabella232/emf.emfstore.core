/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.internal.common.model.impl.UniqueIdentifierImpl;
import org.eclipse.emf.emfstore.internal.server.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.server.model.SessionId;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.ESSessionIdImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Session Id</b></em>'.
 *
 * @implements SessionId
 *             <!-- end-user-doc -->
 *             <p>
 *             </p>
 *
 * @generated
 */
public class SessionIdImpl extends UniqueIdentifierImpl implements SessionId {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected SessionIdImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ModelPackage.Literals.SESSION_ID;
	}

	/**
	 * @generated NOT
	 */
	private ESSessionIdImpl apiImpl;

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 *
	 * @generated NOT
	 */
	public ESSessionIdImpl toAPI() {
		if (apiImpl == null) {
			apiImpl = createAPI();
		}
		return apiImpl;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 *
	 * @generated NOT
	 */
	public ESSessionIdImpl createAPI() {
		return new ESSessionIdImpl(this);
	}

} // SessionIdImpl