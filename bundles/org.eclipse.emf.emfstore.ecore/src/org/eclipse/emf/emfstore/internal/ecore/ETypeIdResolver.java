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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;

/**
 *
 *
 */
public class ETypeIdResolver implements ESSingletonIdResolver {

	private final Map<String, EClass> datatypes = new LinkedHashMap<String, EClass>();

	/**
	 * Constructor.
	 */
	public ETypeIdResolver() {
		// eclass stuff
		datatypes.put("EClass", EcorePackage.eINSTANCE.getEClass()); //$NON-NLS-1$
		datatypes.put("EStructuralFeature", EcorePackage.eINSTANCE.getEStructuralFeature()); //$NON-NLS-1$
		datatypes.put("EGenericType", EcorePackage.eINSTANCE.getEGenericType()); //$NON-NLS-1$
		datatypes.put("EPackage", EcorePackage.eINSTANCE.getEPackage()); //$NON-NLS-1$
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

		return datatypes.get(singletonId.getId());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver#getSingletonModelElementId(org.eclipse.emf.ecore.EObject)
	 */
	public ESModelElementId getSingletonModelElementId(EObject singleton) {
		if (!isSingleton(singleton) || singleton == null) {
			return null;
		}

		// TODO: EM, provide 2nd map for performance reasons
		for (final Map.Entry<String, EClass> entry : datatypes.entrySet()) {
			if (!entry.getValue().isInstance(singleton)) {
				continue;
			}

			// TODO: don't create IDs on the fly rather put them directly into the map
			final ModelElementId id = ModelFactory.eINSTANCE.createModelElementId();
			id.setId(entry.getKey());
			return id.toAPI();
		}

		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESSingletonIdResolver#isSingleton(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isSingleton(EObject eDataType) {
		for (final EClass eClass : datatypes.values()) {
			if (eClass.isInstance(eDataType)) {
				return true;
			}
		}

		return false;
	}

}
