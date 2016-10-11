/*******************************************************************************
 * Copyright (c) 2016 EclipseSource Muenchen GmbH and others.
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

import org.eclipse.emf.ecore.EObject;

/**
 * Interface for generating IDs for model elements which are materialized
 * implicitly by EMF, e.g. EGenericTypes.
 * Such elements might be ignored during application of Create/Delete operations.
 *
 * @author emueller
 * @since 1.9
 *
 * @param <ID> the type of ID
 */
public interface ESMaterializedModelElementIdGenerator<ID> {

	/**
	 * Create an ID for an materialized model element.
	 *
	 * @param eObject the {@link EObject} for which to generate an ID
	 * @param container the current container of {@link EObject}s
	 * @return a generated String ID or null, if the given {@link EObject} is not materialized implicitly
	 */
	String generateModelElementId(EObject eObject, ESObjectContainer<ID> container);

	/**
	 * Whether to ignore the given ID during application of a Create/Delete operation.
	 *
	 * @param id the ID of an {@link EObject}
	 * @param container the current container of {@link EObject}s
	 * @return {@code true}, if the element with the given ID should be ignored, {@code false} otherwise
	 */
	boolean skip(String id, ESObjectContainer<ID> container);
}
