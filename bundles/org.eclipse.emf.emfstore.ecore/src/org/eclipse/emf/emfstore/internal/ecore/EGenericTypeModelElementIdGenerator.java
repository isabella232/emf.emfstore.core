/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.ecore;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.common.model.ESMaterializedModelElementIdGenerator;
import org.eclipse.emf.emfstore.common.model.ESObjectContainer;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;

/**
 * An {@link ESMaterializedModelElementIdGenerator} for {@link EGenericType}s.
 * EGenericTypes are special in that sense that they are create implicitly
 * by EMF once the type of an {@link EAttribute} or an {@link EReference} has been
 * set. EMFStore will track those created {@link EGenericType}s, but this will
 * lead to a problem when applying the respective Create/Delete operation, since
 * the {@link EGenericType}s already has been created when the parent of the generic,
 * that is an {@link EReference} or an {@link EStructuralFeature}, has been added
 * to project. This would therefore lead to duplicates.
 * In order to avoid this issue, we need skip those operations, which will be taken care of
 * with this class.
 *
 *
 * @author emueller
 */
public class EGenericTypeModelElementIdGenerator implements ESMaterializedModelElementIdGenerator<ModelElementId> {

	private static final String SEPARATOR = "__"; //$NON-NLS-1$
	private static final String ID_SUFFIX = SEPARATOR + "EGenericType"; //$NON-NLS-1$

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESMaterializedModelElementIdGenerator#generateModelElementId(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.emfstore.common.model.ESObjectContainer)
	 */
	public String generateModelElementId(EObject eObject, ESObjectContainer<ModelElementId> collection) {

		if (EGenericType.class.isInstance(eObject)) {
			final EGenericType eGeneric = EGenericType.class.cast(eObject);

			if (eGeneric.eContainer() != null
				&& eGeneric.eContainer() != collection
				&& collection.contains(eGeneric.eContainer())) {
				// generate artificial ID based on the container of the EGenericType
				final ModelElementId containerId = collection.getModelElementId(eGeneric.eContainer());
				return containerId.getId() + ID_SUFFIX;
			}
		}

		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESMaterializedModelElementIdGenerator#skip(java.lang.String,
	 *      org.eclipse.emf.emfstore.common.model.ESObjectContainer)
	 */
	public boolean skip(String id, ESObjectContainer<ModelElementId> collection) {
		if (id.endsWith(ID_SUFFIX)) {
			// if id is a previously generated ID of an EGenericType
			// we may skip in case the container is already part of the collection
			final String containerId = id.substring(0, id.lastIndexOf(SEPARATOR));
			final ModelElementId containerModelElementId = ModelFactory.eINSTANCE.createModelElementId();
			containerModelElementId.setId(containerId);
			return collection.contains(containerModelElementId);
		}
		return false;
	}

}
