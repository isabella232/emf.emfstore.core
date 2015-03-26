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
package org.eclipse.emf.emfstore.common.model;

import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdImpl;

/**
 * Static factory for creating {@link ESModelElementId}s.
 *
 * @author emueller
 * @since 1.4
 *
 */
public final class ESModelElementIdFactory {

	/**
	 * Private constructor.
	 */
	private ESModelElementIdFactory() {

	}

	/**
	 * Creates a new {@link ESModelElementId}.
	 *
	 * @return a new {@link ESModelElementId}
	 */
	public static ESModelElementId create() {
		final ModelElementId modelElementId = ModelFactory.eINSTANCE.createModelElementId();
		return new ESModelElementIdImpl(modelElementId);
	}

	/**
	 * Creates a new {@link ESModelElementId} from a given ID.
	 *
	 * @param id
	 *            the ID the {@link ESModelElementId} is based on
	 *
	 * @return a new {@link ESModelElementId}
	 */
	public static ESModelElementId fromString(String id) {
		final ESModelElementId modelElementId = create();
		ESModelElementIdImpl.class.cast(modelElementId).setId(id);
		return modelElementId;
	}
}
