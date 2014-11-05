/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
	private final Set<EObject> allRemovedElements;
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
		allRemovedElements = new LinkedHashSet<EObject>();
		removedElementsIds = new LinkedHashMap<EObject, ModelElementId>();
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
			allRemovedElements.add(eObject);
		}

		if (crossReferences.size() != 0) {
			for (final EObject eObject : allModelElements) {
				removedElementsToReferenceSettings.put(eObject, crossReferences);
			}
		}
	}

	/**
	 * Returns a list of all removed root elements. Children of
	 * any root element are not included in the returned list.
	 * 
	 * @return list of all root elements
	 */
	public List<EObject> getRemovedRootElements() {
		return removedRootElements;
	}

	/**
	 * Checks whether the given model element has been previously removed.
	 * 
	 * @param modelElement
	 *            the model element that should be checked whether it has been removed
	 * @return {@code true}, if the model element has been removed, {@code false} otherwise
	 */
	public boolean contains(EObject modelElement) {
		return allRemovedElements.contains(modelElement);
	}

	/**
	 * Returns the id of the specified removed element.
	 * 
	 * @param removedElement
	 *            The removed element whose id is requested
	 * @return the model element id of the removed element
	 */
	public ModelElementId getRemovedElementId(EObject removedElement) {
		return removedElementsIds.get(removedElement);
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
		allRemovedElements.clear();
		removedRootElements.clear();
		removedElementsIds.clear();
		removedElementsToReferenceSettings.clear();
	}

}
