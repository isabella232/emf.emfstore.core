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
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isContainmentOrOppositeOfContainmentReference;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isMultiValued;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isNonEmptyEObjectList;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;

import com.google.common.base.Predicate;

/**
 * A mutation, which changes reference values.
 *
 * @author Philip Langer
 *
 */
public class ReferenceChangeMutation extends StructuralFeatureMutation {

	/**
	 * The modes in which a reference can be changed.
	 *
	 * @author Philip Langer
	 *
	 */
	protected enum ReferenceChangeMode {
		/** Adding or setting a new reference value. */
		ADD,
		/** Deleting or unsetting an existing reference value. */
		DELETE,
		/** Reordering existing values of a multi-valued reference. */
		REORDER
	}

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public ReferenceChangeMutation(ModelMutatorUtil util) {
		super(util);
		addTargetFeatureReferencePredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected ReferenceChangeMutation(ModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addTargetFeatureReferencePredicate();
	}

	private void addTargetFeatureReferencePredicate() {
		targetContainerSelector.getTargetFeaturePredicates().add(
			MutationPredicates.isMutatableReference);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		return new ReferenceChangeMutation(getUtil(), targetContainerSelector);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#doApply()
	 */
	@Override
	protected boolean doApply() throws MutationException {
		switch (getRandomChangeMode()) {
		case ADD:
			return doAddReferenceValue();
		case DELETE:
			return doDeleteReferenceValue();
		case REORDER:
			return doReorderReferenceValue();
		default:
			return false;
		}
	}

	private boolean doAddReferenceValue() throws MutationException {
		final boolean success;
		makeSureChangingTargetDoesNotAffectContainmentReference();
		makeSureValueForSelectedFeatureToAddExists();
		targetContainerSelector.doSelection();

		final EObject eObject = targetContainerSelector.getTargetObject();
		final EReference eReference = (EReference) targetContainerSelector.getTargetFeature();
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
				final int insertionIndex = targetContainerSelector.
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
	 * @throws MutationException Thrown if no valid value could be found.
	 */
	protected EObject selectNewReferenceValue() throws MutationException {
		final EObject newReferenceValue;
		final EReference eReference = (EReference) targetContainerSelector.getTargetFeature();

		final Iterable<EObject> suitableEObjects = getUtil().
			getSuitableEObjectsForAvailableFeature(eReference);
		final int numberOfAvailableEObjects = size(suitableEObjects);

		if (numberOfAvailableEObjects < 1) {
			throw new MutationException("No objects available as feature values to add"); //$NON-NLS-1$
		}

		final int randomIndex = getRandom().nextInt(numberOfAvailableEObjects);
		newReferenceValue = get(suitableEObjects, randomIndex);

		return newReferenceValue;
	}

	private boolean doDeleteReferenceValue() throws MutationException {
		makeSureChangingTargetDoesNotAffectContainmentReference();
		makeSureWeHaveValuesInSelectedObjectAtSelectedFeature();
		targetContainerSelector.doSelection();
		final EObject eObject = targetContainerSelector.getTargetObject();
		final EReference eReference = (EReference) targetContainerSelector.getTargetFeature();

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
		targetContainerSelector.getTargetFeaturePredicates().add(
			not(isContainmentOrOppositeOfContainmentReference));
	}

	private void makeSureValueForSelectedFeatureToAddExists() {
		targetContainerSelector.getTargetFeaturePredicates().add(new Predicate<EStructuralFeature>() {
			public boolean apply(EStructuralFeature input) {
				return input != null
					&& !isEmpty(getUtil().getSuitableEObjectsForAvailableFeature(input));
			}
		});
	}

	private boolean doReorderReferenceValue() throws MutationException {
		final boolean success;
		makeSureSelectedFeatureIsMultiValued();
		makeSureWeHaveValuesInSelectedObjectAtSelectedFeature();
		targetContainerSelector.doSelection();
		final EObject eObject = targetContainerSelector.getTargetObject();
		final EReference eReference = (EReference) targetContainerSelector.getTargetFeature();

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
		targetContainerSelector.getTargetFeaturePredicates().add(isMultiValued);
	}

	private void makeSureWeHaveValuesInSelectedObjectAtSelectedFeature() {
		targetContainerSelector.getOriginalFeatureValuePredicates().add(isNonEmptyEObjectList);
	}

	/**
	 * Returns a random change mode.
	 *
	 * @return A random change mode.
	 */
	protected ReferenceChangeMode getRandomChangeMode() {
		final ReferenceChangeMode[] values = ReferenceChangeMode.values();
		final int nextInt = getRandom().nextInt(values.length);
		return values[nextInt];
	}

}