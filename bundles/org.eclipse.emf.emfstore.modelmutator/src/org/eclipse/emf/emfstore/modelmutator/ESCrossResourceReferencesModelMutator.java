/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.google.common.collect.Sets;

/**
 * Model mutator that creates a {@link ResourceSet}, places the root object into the first
 * resource and then distributes all children across the different resources.
 *
 * @author emueller
 * @since 2.0
 *
 */
public class ESCrossResourceReferencesModelMutator extends ESAbstractModelMutator {

	private static final String VIRTUAL_URI_BASE = "virtualUri"; //$NON-NLS-1$
	private int noOfResources = 2;
	private ResourceSet resourceSet;
	private final Set<Resource> resources = Sets.newLinkedHashSet();
	private ESResourceSelectionStrategy resourceSelectionStrategy = new ESRoundRobinResourceSelectionStrategy();

	private static URI createVirtualUri(String id) {
		return URI.createURI(VIRTUAL_URI_BASE + id);
	}

	/**
	 * Default constructor.
	 */
	public ESCrossResourceReferencesModelMutator() {

	}

	/**
	 * Constructor.
	 * <p>
	 * Uses the {@link ESRoundRobinResourceSelectionStrategy} to select resources.
	 * </p>
	 *
	 * @param config
	 *            The {@link ESModelMutatorConfiguration} used during mutation
	 */
	public ESCrossResourceReferencesModelMutator(ESModelMutatorConfiguration config) {
		super(config);
	}

	/**
	 * Constructor.
	 * <p>
	 * Uses the {@link ESRoundRobinResourceSelectionStrategy} to select resources.
	 * </p>
	 *
	 * @param config
	 *            The {@link ESModelMutatorConfiguration} used during mutation
	 * @param howManyResources
	 *            specifies how many resources should be created
	 */
	public ESCrossResourceReferencesModelMutator(ESModelMutatorConfiguration config, int howManyResources) {
		super(config);
		noOfResources = howManyResources;
	}

	/**
	 * Constructor.
	 *
	 * @param config
	 *            The {@link ESModelMutatorConfiguration} used during mutation
	 * @param howManyResources
	 *            specifies how many resources should be created
	 * @param resourceSelectionStrategy
	 *            specifies how to select the resource in order
	 */
	public ESCrossResourceReferencesModelMutator(ESModelMutatorConfiguration config,
		int howManyResources, ESResourceSelectionStrategy resourceSelectionStrategy) {
		super(config);
		noOfResources = howManyResources;
		this.resourceSelectionStrategy = resourceSelectionStrategy;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator#preMutate()
	 */
	@Override
	public void preMutate() {
		resourceSet = createResourceSet(noOfResources);
		resourceSet.getResources().get(0).getContents().add(getRootEObject());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESAbstractModelMutator#addToParent(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EReference)
	 */
	@Override
	protected void addToParent(EObject parent, EObject newObject, EReference reference) {
		super.addToParent(parent, newObject, reference);
		final Resource selectedResource = selectResource(newArrayList(resources));
		if (!isMainResource(selectedResource)) {
			selectedResource.getContents().add(newObject);
		}
	}

	private boolean isMainResource(Resource resource) {
		return resource == resourceSet.getResources().get(0);
	}

	private Resource selectResource(List<Resource> resources) {
		return resourceSelectionStrategy.selectResource(resources);
	}

	private ResourceSet createResourceSet(int howManyResources) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		for (int i = 0; i < howManyResources; i++) {
			final Resource resource = resourceSet.createResource(
				createVirtualUri(Integer.toString(i)));
			resources.add(resource);
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

	}

	/**
	 * Returns the {@link ResourceSet} the mutator is acting upon.
	 *
	 * @return the {@link ResourceSet} of this mutator
	 */
	public ResourceSet getResourceSet() {
		return resourceSet;
	}
}
