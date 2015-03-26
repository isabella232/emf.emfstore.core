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
 * Edgar Mueller - API layer
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * An abstract mutation for changing structural feature values.
 *
 * @author Philip Langer
 * @author emueller
 * @since 2.0
 *
 * @param <T> a subtype of {@link ESStructuralFeatureMutation}
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESStructuralFeatureMutation<T extends ESStructuralFeatureMutation<?>> extends ESMutation {

	/**
	 * Returns the selected or set target object that will or has been mutated.
	 *
	 * @return the target object
	 */
	EObject getTargetObject();

	/**
	 * Returns the {@link EStructuralFeature} of a target object that will or has been mutated.
	 *
	 * @return the target feature
	 */
	EStructuralFeature getTargetFeature();

	/**
	 * Returns the collection of {@link EClass EClasses} to be excluded when selecting the target object.
	 * <p>
	 * That is, EObjects are excluded from being selected as target object if they are an instance of an EClass
	 * contained in this collection. The returned collection is changeable. Add items using
	 * {@code getExcludedTargetEClasses().add}.
	 * </p>
	 *
	 * @return the collection of excluded EClasses
	 */
	Collection<EClass> getExcludedTargetEClasses();

	/**
	 * Returns the collection of {@link EStructuralFeature features} to be excluded from being selected as the target
	 * feature.
	 * <p>
	 * The returned collection is changeable. Add items using {@code getExcludedTargetFeatures().add}.
	 * </p>
	 *
	 * @return the collection of excluded features
	 */
	Collection<EStructuralFeature> getExcludedTargetFeatures();

	/**
	 * Returns the collection of {@link EObject EObjects} to be excluded from being selected as the target object.
	 * <p>
	 * The returned collection is changeable. Add items using {@code getExcludedTargetObjects().add}.
	 * </p>
	 *
	 * @return the collection of EObjects
	 */
	Collection<EObject> getExcludedTargetObjects();

	/**
	 * Sets the {@link EObject} to be used as target object.
	 *
	 * @param targetObject
	 *            the target object to be mutated
	 *
	 * @return this mutation
	 */
	T setTargetObject(EObject targetObject);

	/**
	 * Sets the {@link EStructuralFeature} of a target object that will be mutated.
	 *
	 * @param targetFeature
	 *            the feature of the target object to be mutated
	 *
	 * @return this mutation
	 */
	T setTargetFeature(EStructuralFeature targetFeature);

}
