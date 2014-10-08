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
import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;

/**
 * An abstract mutation for changing structural feature values.
 *
 * @author Philip Langer
 *
 */
public abstract class StructuralFeatureMutation extends Mutation {

	/** The selector for the target object and target feature. */
	protected final MutationTargetSelector targetContainerSelector;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public StructuralFeatureMutation(ModelMutatorUtil util) {
		super(util);
		targetContainerSelector = new MutationTargetSelector(util);
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected StructuralFeatureMutation(ModelMutatorUtil util, MutationTargetSelector selector) {
		super(util);
		targetContainerSelector = new MutationTargetSelector(util, selector);
	}

	/**
	 * Returns the collection of {@link EClass EClasses} to be excluded when selecting the target object.
	 * <p>
	 * That is, EObjects are excluded from being selected as target object if they are an instance of an EClass
	 * contained in this collection. The returned collection is changeable. Add items using
	 * {@code getExcludedTargetEClasses().add}.
	 * </p>
	 *
	 * @return The collection of excluded EClasses.
	 */
	public Collection<EClass> getExcludedTargetEClasses() {
		return targetContainerSelector.getExcludedEClasses();
	}

	/**
	 * Returns the collection of {@link EStructuralFeature features} to be excluded from being selected as the target
	 * feature.
	 * <p>
	 * The returned collection is changeable. Add items using {@code getExcludedTargetFeatures().add}.
	 * </p>
	 *
	 * @return The collection of excluded features.
	 */
	public Collection<EStructuralFeature> getExcludedTargetFeatures() {
		return targetContainerSelector.getExcludedFeatures();
	}

	/**
	 * Returns the collection of {@link EObject EObjects} to be excluded from being selected as the target object.
	 * <p>
	 * The returned collection is changeable. Add items using {@code getExcludedTargetObjects().add}.
	 * </p>
	 *
	 * @return The collection of EObjects.
	 */
	public Collection<EObject> getExcludedTargetObjects() {
		return targetContainerSelector.getExcludedObjects();
	}

	/**
	 * Sets the {@link EObject} to be used as target object.
	 *
	 * @param targetContainer The target object to be mutated.
	 */
	public void setTargetObject(EObject targetObject) {
		targetContainerSelector.setTargetObject(targetObject);
	}

	/**
	 * Returns the selected or set target object that will or has been mutated.
	 *
	 * @return The target object.
	 */
	public EObject getTargetObject() {
		return targetContainerSelector.getTargetObject();
	}

	/**
	 * Sets the {@link EStructuralFeature} of a target object that will be mutated.
	 *
	 * @param targetFeature The feature of the target object to be mutated.
	 */
	public void setTargetFeature(EStructuralFeature targetFeature) {
		targetContainerSelector.setTargetFeature(targetFeature);
	}

	/**
	 * Returns the {@link EStructuralFeature} of a target object that will or has been mutated.
	 *
	 * @return The target feature.
	 */
	public EStructuralFeature getTargetFeature() {
		return targetContainerSelector.getTargetFeature();
	}

}