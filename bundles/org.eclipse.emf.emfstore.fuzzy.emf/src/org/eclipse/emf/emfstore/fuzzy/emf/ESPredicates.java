/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Predicate;

/**
 * A collection of {@link com.google.common.base.Predicate Predicate}s
 * to be used in conjunction with the
 * {@link org.eclipse.emf.emfstore.modelmutator.ESCrossResourceReferencesModelMutator
 * ESCrossResourceReferencesModelMutator}.
 *
 * @author emueller
 * @since 2.0
 */
public final class ESPredicates {

	/**
	 * Private constructor.
	 */
	private ESPredicates() {

	}

	/**
	 * Returns a predicate that checks whether an given {@link EObject} has an external reference.
	 *
	 * @return an {@link ESEObjectHasExternalReference} predicate
	 */
	public static Predicate<EObject> hasExternalReference() {
		return new ESEObjectHasExternalReference();
	}

}