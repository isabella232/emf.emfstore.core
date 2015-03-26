/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Langer - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.mutation;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation;

/**
 * An abstract mutation for changing structural feature values.
 *
 * @author Philip Langer
 *
 * @param <M> the implementing API mutation type
 */
public abstract class StructuralFeatureMutation<M extends ESStructuralFeatureMutation<?>> extends Mutation
	implements ESStructuralFeatureMutation<M> {

	/** The selector for the target object and target feature. */
	private final MutationTargetSelector targetContainerSelector;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public StructuralFeatureMutation(ESModelMutatorUtil util) {
		super(util);
		targetContainerSelector = new MutationTargetSelector(util);
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected StructuralFeatureMutation(ESModelMutatorUtil util, MutationTargetSelector selector) {
		super(util);
		targetContainerSelector = new MutationTargetSelector(util, selector);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#getExcludedTargetEClasses()
	 */
	public Collection<EClass> getExcludedTargetEClasses() {
		return targetContainerSelector.getExcludedEClasses();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#getExcludedTargetFeatures()
	 */
	public Collection<EStructuralFeature> getExcludedTargetFeatures() {
		return targetContainerSelector.getExcludedFeatures();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#getExcludedTargetObjects()
	 */
	public Collection<EObject> getExcludedTargetObjects() {
		return targetContainerSelector.getExcludedObjects();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#setTargetObject(org.eclipse.emf.ecore.EObject)
	 */
	@SuppressWarnings("unchecked")
	public M setTargetObject(EObject targetObject) {
		targetContainerSelector.setTargetObject(targetObject);
		return (M) this;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#getTargetObject()
	 */
	public EObject getTargetObject() {
		return targetContainerSelector.getTargetObject();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#setTargetFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	@SuppressWarnings("unchecked")
	public M setTargetFeature(EStructuralFeature targetFeature) {
		targetContainerSelector.setTargetFeature(targetFeature);
		return (M) this;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESStructuralFeatureMutation#getTargetFeature()
	 */
	public EStructuralFeature getTargetFeature() {
		return targetContainerSelector.getTargetFeature();
	}

	/**
	 * @return the targetContainerSelector
	 */
	protected MutationTargetSelector getTargetContainerSelector() {
		return targetContainerSelector;
	}

}