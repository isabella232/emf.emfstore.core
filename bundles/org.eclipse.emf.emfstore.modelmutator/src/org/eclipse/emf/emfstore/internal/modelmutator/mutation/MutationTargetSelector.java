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

import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * A selector of a mutation target.
 * <p>
 * A mutation target is an {@link EObject} and a {@link EStructuralFeature} to be mutated. This selector can be
 * configured with predicates that are respected during the selection and can also be partly pre-filled with, for
 * instance, an {@link EStructuralFeature}, and the selector will complete the selection based on the configuration or
 * pre-filled data.
 * </p>
 *
 * @author Philip Langer
 */
public class MutationTargetSelector {

	private final ESModelMutatorUtil util;
	private final Collection<EClass> excludedEClasses = new HashSet<EClass>();
	private final Collection<EStructuralFeature> excludedFeatures = new HashSet<EStructuralFeature>();
	private final Collection<EObject> excludedObjects = new HashSet<EObject>();
	private final Set<Predicate<? super EStructuralFeature>> targetFeaturePredicates = new HashSet<Predicate<? super EStructuralFeature>>();
	private final Set<Predicate<? super EObject>> targetObjectPredicates = new HashSet<Predicate<? super EObject>>();
	private final Set<Predicate<? super Object>> originalFeatureValuePredicates = new HashSet<Predicate<? super Object>>();

	private EObject targetObject;
	private EStructuralFeature targetFeature;

	/**
	 * Creates a new mutation target selector with the given {@code util}.
	 *
	 * @param util The model mutator util to be used.
	 */
	public MutationTargetSelector(ESModelMutatorUtil util) {
		this.util = util;
		addExcludedEStructuralFeaturesAndEClassesFromConfig();
	}

	private void addExcludedEStructuralFeaturesAndEClassesFromConfig() {
		excludedFeatures.addAll(util.getModelMutatorConfiguration().geteStructuralFeaturesToIgnore());
		excludedEClasses.addAll(util.getModelMutatorConfiguration().geteClassesToIgnore());
	}

	/**
	 * Creates a new mutation target selector with the same values as the given {@code selector} and the given
	 * {@code util}.
	 *
	 * @param util The model mutator util to be used.
	 * @param selector The selector to copy from.
	 */
	public MutationTargetSelector(ESModelMutatorUtil util, MutationTargetSelector selector) {
		this.util = util;
		setupFromOtherSelector(selector);
	}

	private void setupFromOtherSelector(MutationTargetSelector selector) {
		setupExcludedEClasses(selector);
		setupExcludedObjects(selector);
		setupExcludedFeatures(selector);
		setTargetObject(selector.getTargetObject());
		setTargetFeature(selector.getTargetFeature());
		targetFeaturePredicates.addAll(selector.getTargetFeaturePredicates());
		targetObjectPredicates.addAll(selector.getTargetObjectPredicates());
		originalFeatureValuePredicates.addAll(selector.getOriginalFeatureValuePredicates());
	}

	private void setupExcludedEClasses(MutationTargetSelector selector) {
		getExcludedEClasses().clear();
		getExcludedEClasses().addAll(selector.getExcludedEClasses());
	}

	private void setupExcludedObjects(MutationTargetSelector selector) {
		getExcludedObjects().clear();
		getExcludedObjects().addAll(selector.getExcludedObjects());
	}

	private void setupExcludedFeatures(MutationTargetSelector selector) {
		getExcludedFeatures().clear();
		getExcludedFeatures().addAll(selector.getExcludedFeatures());
	}

	/**
	 * Returns the list of EClasses to be excluded from the selection.
	 * <p>
	 * That is, that objects are excluded if they are instances of one of the excluded EClasses.
	 * </p>
	 *
	 * @return The list of EClasses to be excluded from the selection.
	 */
	protected Collection<EClass> getExcludedEClasses() {
		return excludedEClasses;
	}

	/**
	 * Returns the list of features to be excluded from the selection.
	 *
	 * @return The list of features to be excluded from the selection.
	 */
	protected Collection<EStructuralFeature> getExcludedFeatures() {
		return excludedFeatures;
	}

	/**
	 * Returns the list of EObjects to be excluded from the selection.
	 *
	 * @return The list of EObjects to be excluded from the selection.
	 */
	protected Collection<EObject> getExcludedObjects() {
		return excludedObjects;
	}

	/**
	 * Returns the set or selected target object.
	 *
	 * @return The set or selected target object.
	 */
	protected EObject getTargetObject() {
		return targetObject;
	}

	/**
	 * Sets the target object to be used.
	 *
	 * @param targetObject The target object to be used.
	 */
	protected void setTargetObject(EObject targetObject) {
		this.targetObject = targetObject;
	}

	/**
	 * Returns the set or selected target feature.
	 *
	 * @return The set or selected target feature.
	 */
	protected EStructuralFeature getTargetFeature() {
		return targetFeature;
	}

	/**
	 * Sets the target feature to be used.
	 *
	 * @param targetFeature The target feature to be used.
	 */
	protected void setTargetFeature(EStructuralFeature targetFeature) {
		this.targetFeature = targetFeature;
	}

	/**
	 * Returns the modifiable list of predicates constraining the target feature.
	 *
	 * @return The list of predicates constraining the target feature.
	 */
	protected Set<Predicate<? super EStructuralFeature>> getTargetFeaturePredicates() {
		return targetFeaturePredicates;
	}

	private Predicate<? super EStructuralFeature> getTargetFeaturePredicatesConjunction() {
		return and(getTargetFeaturePredicates());
	}

	/**
	 * Returns the modifiable list of predicates constraining the target object.
	 *
	 * @return The list of predicates constraining the target object.
	 */
	protected Set<Predicate<? super EObject>> getTargetObjectPredicates() {
		return targetObjectPredicates;
	}

	private Predicate<? super EObject> getTargetObjectPredicatesConjunction() {
		return and(getTargetObjectPredicates());
	}

	/**
	 * Returns the modifiable list of predicates constraining the original value of the target object at the target
	 * feature.
	 *
	 * @return The list of predicates constraining the original value of the target object at the target feature.
	 */
	protected Set<Predicate<? super Object>> getOriginalFeatureValuePredicates() {
		return originalFeatureValuePredicates;
	}

	private Predicate<? super Object> getOriginalFeatureValuePredicatesConjunction() {
		return and(getOriginalFeatureValuePredicates());
	}

	/**
	 * Performs the selection according to the configured predicates and optionally pre-filled data.
	 *
	 * @throws ESMutationException If no valid target object or feature could be found.
	 */
	protected void doSelection() throws ESMutationException {
		final List<EStructuralFeature> features = getShuffledFeaturesToSelect();
		for (final EStructuralFeature feature : features) {
			for (final EObject eObject : getShuffledTargetObjectsToSelect(feature)) {
				if (isValid(feature, eObject)) {
					setTargetFeature(feature);
					setTargetObject(eObject);
					return;
				}
			}
		}
		throw new ESMutationException("No valid target found."); //$NON-NLS-1$
	}

	private List<EStructuralFeature> getShuffledFeaturesToSelect() {
		if (hasTargetFeature()) {
			return Lists.newArrayList(getTargetFeature());
		} else if (hasTargetObject()) {
			return getShuffledAvailableFeaturesFromTargetObject();
		} else {
			return getShuffledAvailableFeatures();
		}
	}

	private boolean hasTargetFeature() {
		return targetFeature != null;
	}

	private boolean hasTargetObject() {
		return targetObject != null;
	}

	private List<EStructuralFeature> getShuffledAvailableFeaturesFromTargetObject() {
		final List<EStructuralFeature> availableFeatures = new ArrayList<EStructuralFeature>();
		final EClass eClassOfTargetObject = targetObject.eClass();
		availableFeatures.addAll(eClassOfTargetObject.getEAllStructuralFeatures());
		excludeAndShuffleTargetFeatures(availableFeatures);
		return availableFeatures;
	}

	private void excludeAndShuffleTargetFeatures(final List<EStructuralFeature> features) {
		features.removeAll(excludedFeatures);
		filterTargetFeaturePredicates(features);
		Collections.shuffle(features, getRandom());
	}

	private void filterTargetFeaturePredicates(final List<EStructuralFeature> features) {
		for (final EStructuralFeature feature : Lists.newArrayList(features)) {
			if (!getTargetFeaturePredicatesConjunction().apply(feature)) {
				features.remove(feature);
			}
		}
	}

	private List<EStructuralFeature> getShuffledAvailableFeatures() {
		final List<EStructuralFeature> features = getAvailableFeatures();
		excludeAndShuffleTargetFeatures(features);
		return features;
	}

	private List<EStructuralFeature> getAvailableFeatures() {
		return Lists.newArrayList(util
			.getAvailableFeatures(getTargetFeaturePredicatesConjunction()));
	}

	private List<EObject> getShuffledTargetObjectsToSelect(EStructuralFeature feature) {
		if (hasTargetObject()) {
			return Lists.newArrayList(targetObject);
		}
		return getShuffledEObjectsForAvailableFeature(feature);
	}

	private List<EObject> getShuffledEObjectsForAvailableFeature(EStructuralFeature feature) {
		final ArrayList<EObject> eObjects = getEObjectsForAvailableFeature(feature);
		excludeAndShuffleTargetObjects(eObjects);
		return eObjects;
	}

	private void excludeAndShuffleTargetObjects(final List<EObject> eObjects) {
		eObjects.removeAll(excludedObjects);
		filterTargetObjectPredicates(eObjects);
		Collections.shuffle(eObjects);
	}

	private void filterTargetObjectPredicates(final List<EObject> eObjects) {
		for (final EObject eObject : Lists.newArrayList(eObjects)) {
			if (!getTargetObjectPredicatesConjunction().apply(eObject)) {
				eObjects.remove(eObject);
			}
		}
	}

	private ArrayList<EObject> getEObjectsForAvailableFeature(EStructuralFeature feature) {
		final Predicate<? super EObject> targetObjectPredicate = getTargetObjectPredicatesConjunction();
		return Lists.newArrayList(util.getOfferingEObjectsForAvailableFeature(feature, targetObjectPredicate));
	}

	private Random getRandom() {
		return util.getModelMutatorConfiguration().getRandom();
	}

	/**
	 * Specifies whether the current selection is valid with respect to the configured predicates and excluded EClasses,
	 * EObjects, and features.
	 *
	 * @return <code>true</code> if the selection is valid, <code>false</code> otherwise.
	 */
	protected boolean isValid() {
		return isValid(targetFeature, targetObject);
	}

	private boolean isValid(final EStructuralFeature feature, final EObject eObject) {
		if (feature == null || eObject == null) {
			return false;
		}
		final EClass eClass = eObject.eClass();
		final EList<EStructuralFeature> featuresOfEClass = eClass.getEAllStructuralFeatures();
		return !isExcluded(feature, eObject)
			&& featuresOfEClass.contains(feature)
			&& fulfillsTargetFeaturePredicate(feature)
			&& fulfillsTargetObjectPredicate(eObject)
			&& fulfullsOriginalFeatureValuePredicate(feature, eObject);
	}

	private boolean isExcluded(EStructuralFeature feature, EObject eObject) {
		final EClass eClass = eObject.eClass();
		return excludedFeatures.contains(feature)
			|| excludedEClasses.contains(feature.getEType())
			|| excludedEClasses.contains(eClass)
			|| excludedObjects.contains(eObject);
	}

	private boolean fulfillsTargetFeaturePredicate(EStructuralFeature feature) {
		return getTargetFeaturePredicatesConjunction().apply(feature);
	}

	private boolean fulfillsTargetObjectPredicate(EObject eObject) {
		return getTargetObjectPredicatesConjunction().apply(eObject);
	}

	private boolean fulfullsOriginalFeatureValuePredicate(EStructuralFeature feature, EObject eObject) {
		final Object originalValue = eObject.eGet(feature);
		return getOriginalFeatureValuePredicatesConjunction().apply(originalValue);
	}

	/**
	 * Returns a random value from the currently selected target object at the selected target feature.
	 *
	 * @return A random value from the target object at the target feature.
	 */
	protected Object selectRandomValueFromTargetObject() {
		return selectRandomContainedValue(alwaysTrue());
	}

	/**
	 * Returns a random value that conforms to the given {@code predicates} from the currently selected target object at
	 * the selected target feature.
	 *
	 * @param predicate The predicate to be respected for selecting the random value.
	 * @return A random value respecting the {@code predicates} from the target object at the target feature.
	 */
	protected Object selectRandomContainedValue(Predicate<? super Object> predicate) {
		if (!isValid()) {
			throw new IllegalStateException("There is no valid selection to get value for."); //$NON-NLS-1$
		} else if (getTargetFeature().isMany()) {
			return selectRandomValueFromTargetObjectWithMultiValuedFeature(predicate);
		} else {
			return selectRandomValueFromTargetObjectWithSingleValuedFeature(predicate);
		}
	}

	private Object selectRandomValueFromTargetObjectWithMultiValuedFeature(Predicate<? super Object> predicate) {
		@SuppressWarnings("unchecked")
		final List<Object> values = (List<Object>) getTargetValue();
		final Iterable<Object> filteredValues = filter(values, predicate);
		final int randomIndex = getRandomIndexFromValueRange(filteredValues);
		final Object randomObject = get(filteredValues, randomIndex);

		return randomObject;
	}

	private Object getTargetValue() {
		return getTargetObject().eGet(getTargetFeature());
	}

	private Object selectRandomValueFromTargetObjectWithSingleValuedFeature(Predicate<? super Object> predicate) {
		final Object targetValue = getTargetValue();
		if (predicate.apply(targetValue)) {
			return targetValue;
		}
		return null;
	}

	/**
	 * Returns a random index within the range of currently contained values in the target object at the target feature.
	 *
	 * @return A random index within the range of the current value of the target object at the target feature.
	 */
	protected int getRandomIndexFromTargetObjectAndFeatureValueRange() {
		@SuppressWarnings("unchecked")
		final Collection<Object> values = (Collection<Object>) getTargetValue();
		return getRandomIndexFromValueRange(values);
	}

	private int getRandomIndexFromValueRange(Iterable<Object> values) {
		final int randomIndex;
		final int numberOfCurrentValues = size(values);
		if (numberOfCurrentValues > 0) {
			randomIndex = getRandom().nextInt(numberOfCurrentValues);
		} else {
			randomIndex = 0;
		}
		return randomIndex;
	}
}