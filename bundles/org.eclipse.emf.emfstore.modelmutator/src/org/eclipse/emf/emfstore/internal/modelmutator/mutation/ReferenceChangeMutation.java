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

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_CONTAINMENT_OR_OPPOSITE_OF_CONTAINMENT_REFERENCE;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_MULTI_VALUED;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_NON_EMPTY_EOBJECT_LIST;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESRandomChangeMode;
import org.eclipse.emf.emfstore.modelmutator.ESReferenceChangeMutation;

import com.google.common.base.Predicate;

/**
 * A mutation, which changes reference values.
 *
 * @author Philip Langer
 *
 */
public class ReferenceChangeMutation extends StructuralFeatureMutation<ESReferenceChangeMutation> implements
	ESReferenceChangeMutation {

	private EObject newReferenceValue;
	private ESRandomChangeMode randomChangeMode;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public ReferenceChangeMutation(ESModelMutatorUtil util) {
		super(util);
		addTargetFeatureReferencePredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected ReferenceChangeMutation(ESModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addTargetFeatureReferencePredicate();
	}

	private void addTargetFeatureReferencePredicate() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(
			MutationPredicates.IS_MUTABLE_REFERENCE);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		return new ReferenceChangeMutation(getUtil(), getTargetContainerSelector());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#apply()
	 */
	@Override
	public void apply() throws ESMutationException {
		switch (getRandomChangeMode()) {
		case ADD:
			doAddReferenceValue();
			break;
		case DELETE:
			doDeleteReferenceValue();
			break;
		case REORDER:
			doReorderReferenceValue();
			break;
		default:
			break;
		}
	}

	private boolean doAddReferenceValue() throws ESMutationException {
		final boolean success;
		makeSureChangingTargetDoesNotAffectContainmentReference();
		makeSureValueForSelectedFeatureToAddExists();
		getTargetContainerSelector().doSelection();

		final EObject eObject = getTargetContainerSelector().getTargetObject();
		final EReference eReference = (EReference) getTargetContainerSelector().getTargetFeature();
		final EObject newValue = selectNewReferenceValue();

		if (newValue != null) {
			success = addOrSetReferenceValue(eObject, eReference, newValue);
		} else {
			success = false;
		}

		return success;
	}

	private boolean addOrSetReferenceValue(final EObject eObject, final EReference eReference,
		final EObject newValue) {
		final boolean success;
		if (newValue != null) {
			if (eReference.isMany()) {
				final int insertionIndex = getTargetContainerSelector().
					getRandomIndexFromTargetObjectAndFeatureValueRange();
				getUtil().addPerCommand(eObject, eReference, newValue, insertionIndex);
			} else {
				getUtil().setPerCommand(eObject, eReference, newValue);
			}
			success = true;
		} else {
			success = false;
		}
		return success;
	}

	/**
	 * Selects and returns a suitable value for the currently selected reference.
	 *
	 * @return The selected reference value.
	 * @throws ESMutationException Thrown if no valid value could be found.
	 */
	protected EObject selectNewReferenceValue() throws ESMutationException {

		if (newReferenceValue != null) {
			return newReferenceValue;
		}

		final EObject newReferenceValue;
		final EReference eReference = (EReference) getTargetContainerSelector().getTargetFeature();

		final Iterable<EObject> suitableEObjects = getUtil().
			getSuitableEObjectsForAvailableFeature(eReference);
		final int numberOfAvailableEObjects = size(suitableEObjects);

		if (numberOfAvailableEObjects < 1) {
			throw new ESMutationException("No objects available as feature values to add"); //$NON-NLS-1$
		}

		final int randomIndex = getRandom().nextInt(numberOfAvailableEObjects);
		newReferenceValue = get(suitableEObjects, randomIndex);

		return newReferenceValue;
	}

	private boolean doDeleteReferenceValue() throws ESMutationException {
		makeSureChangingTargetDoesNotAffectContainmentReference();
		makeSureWeHaveValuesInSelectedObjectAtSelectedFeature();
		getTargetContainerSelector().doSelection();
		final EObject eObject = getTargetContainerSelector().getTargetObject();
		final EReference eReference = (EReference) getTargetContainerSelector().getTargetFeature();

		if (eReference.isMany()) {
			final List<?> currentValues = (List<?>) eObject.eGet(eReference);
			final int numberOfCurrentValues = currentValues.size();
			final int deletionIndex = getRandom().nextInt(numberOfCurrentValues);
			currentValues.remove(deletionIndex);
			getUtil().setPerCommand(eObject, eReference, currentValues);
		} else {
			getUtil().setPerCommand(eObject, eReference, SetCommand.UNSET_VALUE);
		}

		return true;
	}

	private void makeSureChangingTargetDoesNotAffectContainmentReference() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(
			not(IS_CONTAINMENT_OR_OPPOSITE_OF_CONTAINMENT_REFERENCE));
	}

	private void makeSureValueForSelectedFeatureToAddExists() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(new Predicate<EStructuralFeature>() {
			public boolean apply(EStructuralFeature input) {
				return input != null
					&& !isEmpty(getUtil().getSuitableEObjectsForAvailableFeature(input));
			}
		});
	}

	private boolean doReorderReferenceValue() throws ESMutationException {
		final boolean success;
		makeSureSelectedFeatureIsMultiValued();
		makeSureWeHaveValuesInSelectedObjectAtSelectedFeature();
		getTargetContainerSelector().doSelection();
		final EObject eObject = getTargetContainerSelector().getTargetObject();
		final EReference eReference = (EReference) getTargetContainerSelector().getTargetFeature();

		if (eReference.isMany()) {
			@SuppressWarnings("unchecked")
			final List<Object> currentValues = (List<Object>) eObject.eGet(eReference);
			final int numberOfCurrentValues = currentValues.size();
			final int pickIndex = getRandom().nextInt(numberOfCurrentValues);
			final int putIndex = getRandom().nextInt(numberOfCurrentValues);
			getUtil().movePerCommand(eObject, eReference, currentValues.get(pickIndex), putIndex);
			success = true;
		} else {
			success = false;
		}

		return success;
	}

	private void makeSureSelectedFeatureIsMultiValued() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(IS_MULTI_VALUED);
	}

	private void makeSureWeHaveValuesInSelectedObjectAtSelectedFeature() {
		getTargetContainerSelector().getOriginalFeatureValuePredicates().add(IS_NON_EMPTY_EOBJECT_LIST);
	}

	/**
	 * Returns a random change mode.
	 *
	 * @return A random change mode.
	 */
	protected ESRandomChangeMode getRandomChangeMode() {

		if (randomChangeMode != null) {
			return randomChangeMode;
		}

		final ESRandomChangeMode[] values = ESRandomChangeMode.values();
		final int nextInt = getRandom().nextInt(values.length);
		return values[nextInt];
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESReferenceChangeMutation#setRandomChangeMode(org.eclipse.emf.emfstore.modelmutator.ESRandomChangeMode)
	 */
	public ESReferenceChangeMutation setRandomChangeMode(ESRandomChangeMode randomChangeMode) {
		this.randomChangeMode = randomChangeMode;
		return this;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESReferenceChangeMutation#setNewReferenceValue(org.eclipse.emf.ecore.EObject)
	 */
	public ESReferenceChangeMutation setNewReferenceValue(EObject newValue) {
		if (newValue != null) {
			newReferenceValue = newValue;
		}

		return this;
	}
}