/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common.api;

import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * An internal {@link org.eclipse.emf.ecore.EObject EObject} that has an API delegate.
 *
 * @author emueller
 *
 * @param <A> the internal API implementation wrapper class, naming convention goes by {@code ES_XXX_Impl} class
 * @param <I> the internal type
 *
 */
public abstract class AbstractAPIDeletegateImpl<A extends AbstractAPIImpl<A, I>, I extends APIDelegate<A>> extends
	EObjectImpl implements APIDelegate<A> {

	private A apiImpl;

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public A toAPI() {
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
	 */
	public abstract A createAPI();

}
