/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * EMFStore Resource, inherits from XMIResource and sets intrinsic ID to EObjectMap optimization.
 *
 * @author emueller
 */
public class EMFStoreResource extends XMIResourceImpl {

	private final Set<Map<String, EObject>> additionalIdToEObjectMappings;
	private final Set<Map<EObject, String>> additionalEObjectToIdMappings;
	private boolean isMappingInitialized;

	/**
	 * Default constructor.
	 *
	 * @param uri
	 *            the URI of the resource
	 */
	public EMFStoreResource(final URI uri) {
		super(uri);
		idToEObjectMap = new LinkedHashMap<String, EObject>();
		eObjectToIDMap = new LinkedHashMap<EObject, String>();
		additionalEObjectToIdMappings = new LinkedHashSet<Map<EObject, String>>();
		additionalIdToEObjectMappings = new LinkedHashSet<Map<String, EObject>>();
	}

	/**
	 * Adds an additional ID/EObject mapping that may be used during {@link #getID(EObject)}.
	 *
	 * @param additionalIdToEObjectMap
	 *            a additional map from IDs to EObject that might be used during {@link #getID(EObject)}
	 */
	public void addIdToEObjectDelegateMapping(final Map<String, EObject> additionalIdToEObjectMap) {
		additionalIdToEObjectMappings.add(additionalIdToEObjectMap);
	}

	/**
	 * Adds an additional EObject/ID mapping that may be used during {@link #getEObjectByID(String)}.
	 *
	 * @param eObjectToIdMap
	 *            a additional map from EObject to IDs that might be used during {@link #getEObjectByID(String)}
	 */
	public void addEObjectToIdDelegateMapping(final Map<EObject, String> eObjectToIdMap) {
		if (additionalEObjectToIdMappings.contains(eObjectToIdMap)) {
			return;
		}
		additionalEObjectToIdMappings.add(eObjectToIdMap);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#getID(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getID(EObject eObject) {
		String id = eObjectToIDMap.get(eObject);
		if (id == null) {
			for (final Map<EObject, String> mapping : additionalEObjectToIdMappings) {
				id = mapping.get(eObject);
				if (id != null) {
					break;
				}
			}
		}
		return id;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#getEObjectByID(java.lang.String)
	 */
	@Override
	protected EObject getEObjectByID(String id) {
		EObject eObject = idToEObjectMap.get(id);
		if (eObject == null) {
			for (final Map<String, EObject> mapping : additionalIdToEObjectMappings) {
				eObject = mapping.get(id);
				if (eObject != null) {
					break;
				}
			}
		}
		return eObject;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
	 */
	@Override
	protected XMLHelper createXMLHelper() {
		return new EMFStoreResourceHelper(this);
	}

	/**
	 * Whether the EObject/ID mapping has been initialized.
	 *
	 * @return {@code true}, if the mapping has been initialized, {@code false} otherwise
	 */
	public boolean isMappingInitialized() {
		return isMappingInitialized;
	}

	/**
	 * Determines whether the EObject/ID mapping has been initialized.
	 *
	 * @param mappingInitialized whether the mapping has been initialized
	 */
	public void setMappingInitialized(boolean mappingInitialized) {
		isMappingInitialized = mappingInitialized;
	}
}