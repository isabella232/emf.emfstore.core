/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.collect.Maps;

/**
 * @author Edgar
 * @since 2.0
 * 
 */
public class ESSplittedResourcesModelMutator extends ESAbstractModelMutator {

	public static final String VIRTUAL_URI_BASE = "virtualUri"; //$NON-NLS-1$
	private final int nrOfResources = 2;
	private ResourceSet resourceSet;
	private final Map<Resource, EObject> rootEObjectsByResource = Maps.newLinkedHashMap();

	private static URI createVirtualUri(String id) {
		return URI.createURI(VIRTUAL_URI_BASE + id);
	}

	/**
	 * @param config
	 */
	public ESSplittedResourcesModelMutator(ESModelMutatorConfiguration config) {
		super(config);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator#preMutate()
	 */
	@Override
	public void preMutate() {
		final EClass rootEClass = getConfig().getRootEObject().eClass();
		resourceSet = createResourceSet(nrOfResources, rootEClass);
	}

	@Override
	public EObject getRootEObject() {

		final Resource resource =
			selectRandomResource(newArrayList(rootEObjectsByResource.keySet()));
		// TODO: assumes exactly one eobject at root level
		final EObject rootEObject = resource.getContents().get(0);

		return rootEObject;
	}

	private Resource selectRandomResource(List<Resource> resources) {
		return resources.get(
			getConfig().getRandom().nextInt(resources.size()));
	}

	private ResourceSet createResourceSet(int howManyResources, EClass rootEClass) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		for (int i = 0; i < howManyResources; i++) {
			final Resource resource =
				resourceSet.createResource(
					createVirtualUri(Integer.toString(i)));
			final EObject rootEObject = EcoreUtil.create(rootEClass);
			rootEObjectsByResource.put(resource, rootEObject);
			resource.getContents().add(rootEObject);
		}
		return resourceSet;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator#postMutate()
	 */
	@Override
	public void postMutate() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the resourceSet
	 */
	public ResourceSet getResourceSet() {
		return resourceSet;
	}

	/**
	 * @param resourceSet the resourceSet to set
	 */
	public void setResourceSet(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

}
