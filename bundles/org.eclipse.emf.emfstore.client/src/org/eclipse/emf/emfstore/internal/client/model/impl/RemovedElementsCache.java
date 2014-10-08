/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.impl.IdEObjectCollectionImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.SettingWithReferencedElement;

/**
 * Caches removed elements.
 * 
 * @author emueller
 */
public class RemovedElementsCache {

	private final IdEObjectCollectionImpl collection;

	private final List<EObject> removedRootElements;
	private final Map<EObject, ModelElementId> removedElementsIds;
	private final Map<EObject, List<SettingWithReferencedElement>> removedElementsToReferenceSettings;

	/**
	 * Constructor.
	 * 
	 * @param collection an underlying {@link org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection}
	 */
	public RemovedElementsCache(IdEObjectCollectionImpl collection) {
		this.collection = collection;
		removedRootElements = new ArrayList<EObject>();
		removedElementsIds = new HashMap<EObject, ModelElementId>();
		removedElementsToReferenceSettings = new LinkedHashMap<EObject, List<SettingWithReferencedElement>>();
	}

	/**
	 * Adds a new deleted element to the cache.
	 * 
	 * @param rootElement
	 *            the deleted element
	 * @param allModelElements
	 *            the deleted element and its contained elements
	 * @param crossReferences
	 *            in- and outgoing references of all model elements
	 */
	public void addRemovedElement(EObject rootElement, Set<EObject> allModelElements,
		List<SettingWithReferencedElement> crossReferences) {
		removedRootElements.add(rootElement);
		removedElementsIds.put(rootElement, collection.getDeletedModelElementId(rootElement));

		for (final EObject eObject : allModelElements) {
			removedElementsIds.put(eObject, collection.getDeletedModelElementId(eObject));
		}

		if (crossReferences.size() != 0) {
			for (final EObject eObject : allModelElements) {
				removedElementsToReferenceSettings.put(eObject, crossReferences);
			}
		}
	}

	/**
	 * Returns the removed elements.
	 * 
	 * @return list of all elements
	 */
	public List<EObject> getRemovedRootElements() {
		return removedRootElements;
	}

	/**
	 * Returns the id of the specified element id.
	 * 
	 * @param modelElement
	 *            The element whose id is requested
	 * @return the model element id
	 */
	public ModelElementId getRemovedElementId(EObject modelElement) {
		return removedElementsIds.get(modelElement);
	}

	/**
	 * Returns the saved settings of the specified model element.
	 * 
	 * @param modelElement
	 *            The model element whose settings are requested
	 * @return the settings
	 */
	public List<SettingWithReferencedElement> getRemovedRootElementToReferenceSetting(EObject modelElement) {
		return removedElementsToReferenceSettings.get(modelElement);
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		removedRootElements.clear();
		removedElementsIds.clear();
		removedElementsToReferenceSettings.clear();
	}

}
