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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Predicate;

/**
 * A predicate that checks whether a given project has an external reference, i.e.
 * a cross-resource reference.
 *
 * @author emueller
 * @since 2.0
 */
public class ESEObjectHasExternalReference implements Predicate<EObject> {

	/**
	 * {@inheritDoc}
	 *
	 * @see com.google.common.base.Predicate#apply(java.lang.Object)
	 */
	public boolean apply(EObject root) {
		final Map<EObject, Collection<Setting>> externalCrossReferences =
			EcoreUtil.ExternalCrossReferencer.find(Collections.singleton(root));

		final Resource rootResource = root.eResource();
		final Set<EObject> externalEObjects = externalCrossReferences.keySet();

		for (final EObject externalEObject : externalEObjects) {
			if (externalEObject.eResource() != null && externalEObject.eResource() != rootResource) {
				return true;
			}
		}

		return false;
	}

}