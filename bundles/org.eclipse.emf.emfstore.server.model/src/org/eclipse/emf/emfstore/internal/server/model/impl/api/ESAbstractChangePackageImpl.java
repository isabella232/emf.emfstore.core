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
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;

public abstract class ESAbstractChangePackageImpl<CP extends AbstractChangePackage & APIDelegate<ESChangePackage>>
	extends AbstractAPIImpl<ESChangePackage, CP> {

	private final CP changePackage;

	/**
	 * @param changePackage
	 */
	protected ESAbstractChangePackageImpl(CP changePackage) {
		super(changePackage);
		this.changePackage = changePackage;
	}

	@Override
	public abstract CP toInternalAPI();

	/**
	 * @return the changePackage
	 */
	protected CP getChangePackage() {
		return changePackage;
	}

}
