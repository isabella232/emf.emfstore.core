/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.ecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;

public class ECoreElementsResolver implements ESSingletonIdResolver {

	/**
	 * Constructor.
	 */
	public ECoreElementsResolver() {
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver#getSingleton(org.eclipse.emf.emfstore.common.model.ESModelElementId)
	 */
	public EObject getSingleton(ESModelElementId singletonId) {
		if (singletonId == null) {
			return null;
		}

		final String id = singletonId.getId();

		// TODO: build up cache
		final EList<EClassifier> eClassifiers = EcorePackage.eINSTANCE.getEClassifiers();
		for (final EClassifier eClassifier : eClassifiers) {
			if (eClassifier.getName().equals(id)) {
				return eClassifier;
			}
		}

		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver#getSingletonModelElementId(org.eclipse.emf.ecore.EObject)
	 */
	public ESModelElementId getSingletonModelElementId(EObject singleton) {
		if (singleton == null || !isSingleton(singleton) || !EClassifier.class.isInstance(singleton)) {
			return null;
		}

		final EClassifier eClassifier = EClassifier.class.cast(singleton);
		final ModelElementId id = ModelFactory.eINSTANCE.createModelElementId();
		id.setId(eClassifier.getName());

		return id.toAPI();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver#isSingleton(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isSingleton(EObject eDataType) {
		return eDataType.eContainer() == EcorePackage.eINSTANCE;
	}

}
