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

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_NON_EMPTY_EOBJECT_OR_LIST;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_NULL_OR_LIST;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.hasCompatibleType;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isAncestor;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isChild;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isCompatibleWithAnyFeatureOfEClass;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isContainedByEObject;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isContainedByFeature;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isNotTheSame;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.mayBeContainedByAnyOfTheseReferences;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.mayBeContainedByFeature;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.mayTakeEObjectAsValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;

import com.google.common.base.Predicate;

/**
 * A mutation, which moves an object from one container into another.
 *
 * @author Philip Langer
 *
 */
public class MoveObjectMutation extends ContainmentChangeMutation<ESMoveObjectMutation>
	implements ESMoveObjectMutation {

	private final MutationTargetSelector sourceContainerSelector;
	private EObject eObjectToMove;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public MoveObjectMutation(ESModelMutatorUtil util) {
		super(util);
		sourceContainerSelector = new MutationTargetSelector(util);
		addSourceContainmentFeaturePredicate();
		addSourceOriginalFeatureValueNotEmptyPredicate();
		addTargetValueIsEmptySingleValuedReferenceOrMultivalueReferencePredicate();

	}

	/**
	 * Creates a new mutation with the specified {@code util} and a selector for the source and target container,
	 * {@code sourceContainerSelector} and {@code targetContainerSelector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param sourceContainerSelector The source selector for selecting the source container and feature.
	 * @param targetContainerSelector The target selector for selecting the target container and feature.
	 */
	protected MoveObjectMutation(ESModelMutatorUtil util, MutationTargetSelector sourceContainerSelector,
		MutationTargetSelector targetContainerSelector) {
		super(util, targetContainerSelector);
		this.sourceContainerSelector = sourceContainerSelector;
		addSourceContainmentFeaturePredicate();
		addSourceOriginalFeatureValueNotEmptyPredicate();
		addTargetValueIsEmptySingleValuedReferenceOrMultivalueReferencePredicate();
	}

	private void addSourceContainmentFeaturePredicate() {
		sourceContainerSelector.getTargetFeaturePredicates().add(
			MutationPredicates.IS_MUTABLE_CONTAINMENT_REFERENCE);
	}

	private void addSourceOriginalFeatureValueNotEmptyPredicate() {
		sourceContainerSelector.getOriginalFeatureValuePredicates().add(
			IS_NON_EMPTY_EOBJECT_OR_LIST);
	}

	private void addTargetValueIsEmptySingleValuedReferenceOrMultivalueReferencePredicate() {
		getTargetContainerSelector().getOriginalFeatureValuePredicates().add(IS_NULL_OR_LIST);
	}

	/**
	 * Returns the collection of {@link EClass EClasses} to be excluded when selecting the source object from which
	 * this mutation will move an object.
	 * <p>
	 * That is, EObjects are excluded from being selected as source object if they are an instance of an EClass
	 * contained in this collection. The returned collection is changeable. Add items using
	 * {@code getExcludedSourceEClasses().add}.
	 * </p>
	 *
	 * @return The collection of excluded EClasses.
	 */
	public Collection<EClass> getExcludedSourceEClasses() {
		return sourceContainerSelector.getExcludedEClasses();
	}

	/**
	 * Returns the collection of {@link EStructuralFeature features} to be excluded from being selected as the source
	 * object's containment feature from which this mutation will move an object.
	 * <p>
	 * The returned collection is changeable. Add items using {@code getExcludedSourceFeatures().add}.
	 * </p>
	 *
	 * @return The collection of excluded features.
	 */
	public Collection<EStructuralFeature> getExcludedSourceFeatures() {
		return sourceContainerSelector.getExcludedFeatures();
	}

	/**
	 * Returns the collection of {@link EObject EObjects} to be excluded from being selected as the source object
	 * from which this mutation will move an object.
	 * <p>
	 * The returned collection is changeable. Add items using {@code getExcludedSourceObjects().add}.
	 * </p>
	 *
	 * @return The collection of EObjects.
	 */
	public Collection<EObject> getExcludedSourceObjects() {
		return sourceContainerSelector.getExcludedObjects();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation#setSourceObject(org.eclipse.emf.ecore.EObject)
	 */
	public ESMoveObjectMutation setSourceObject(EObject sourceObject) {
		sourceContainerSelector.setTargetObject(sourceObject);
		return this;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation#getSourceObject()
	 */
	public EObject getSourceObject() {
		return sourceContainerSelector.getTargetObject();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation#setSourceFeature(org.eclipse.emf.ecore.EStructuralFeature)
	 */
	public ESMoveObjectMutation setSourceFeature(EStructuralFeature sourceFeature) {
		sourceContainerSelector.setTargetFeature(sourceFeature);
		return this;
	}

	/**
	 * Returns the {@link EStructuralFeature} of a source object from which this mutation will move or moved an
	 * object.
	 *
	 * @return The feature of the source object through which the moved or to-be-moved object was or is contained.
	 */
	public EStructuralFeature getSourceFeature() {
		return sourceContainerSelector.getTargetFeature();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation#setEObjectToMove(org.eclipse.emf.ecore.EObject)
	 */
	public ESMoveObjectMutation setEObjectToMove(EObject eObjectToMove) {
		this.eObjectToMove = eObjectToMove;
		return this;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation#getEObjectToMove()
	 */
	public EObject getEObjectToMove() {
		return eObjectToMove;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		final MoveObjectMutation clone = new MoveObjectMutation(getUtil(), sourceContainerSelector,
			getTargetContainerSelector());
		clone.setEObjectToMove(eObjectToMove);
		return clone;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#apply()
	 */
	@Override
	public void apply() throws ESMutationException {
		doSelection();

		final EObject targetObject = getTargetContainerSelector().getTargetObject();
		final EReference targetReference = (EReference) getTargetContainerSelector().getTargetFeature();
		final Random random = getRandom();

		if (targetReference.isMany()) {
			final Integer insertionIndex = random.nextBoolean() ? 0 : null;
			getUtil().addPerCommand(targetObject, targetReference, getEObjectToMove(), insertionIndex);
		} else {
			getUtil().setPerCommand(targetObject, targetReference, getEObjectToMove());
		}
	}

	private void doSelection() throws ESMutationException {
		if (getEObjectToMove() != null) {
			makeSureTargetFitsSelectedEObjectToMove();
			makeSureTargetContainerIsNotChildOfEObjectToMove();
			getTargetContainerSelector().doSelection();
		} else if (haveTargetContainer() && haveTargetFeature()) {
			makeSureSourceContainerIsNotTheSameAsTargetContainer();
			makeSureSourceFeatureIsCompatibleWithTargetFeature();
			sourceContainerSelector.doSelection();
			selectEObjectToMove();
		} else if (haveTargetContainer()) {
			makeSureSourceContainerIsNotTheSameAsTargetContainer();
			makeSureSourceFeatureIsCompatibleWithAnyFeatureOfTargetContainer();
			makeSureSourceContainerIsNotAncesterOfTargetContainer();
			selectSourceAndTarget();
		} else if (haveTargetFeature()) {
			makeSureSourceFeatureIsCompatibleWithTargetFeature();
			selectSourceAndTarget();
		} else {
			selectSourceAndTarget();
		}
	}

	private void selectSourceAndTarget() throws ESMutationException {
		sourceContainerSelector.doSelection();
		selectEObjectToMove();
		makeSureTargetFitsSelectedEObjectToMove();
		makeSureTargetContainerIsNotTheSameAsSourceContainer();
		makeSureTargetContainerIsNotChildOfEObjectToMove();
		getTargetContainerSelector().doSelection();
	}

	private boolean haveTargetFeature() {
		return getTargetContainerSelector().getTargetFeature() != null;
	}

	private boolean haveTargetContainer() {
		return getTargetContainerSelector().getTargetObject() != null;
	}

	private void makeSureTargetFitsSelectedEObjectToMove() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(
			mayTakeEObjectAsValue(getEObjectToMove()));
		getTargetContainerSelector().getTargetObjectPredicates().add(
			isNotTheSame(getEObjectToMove().eContainer()));
	}

	private void makeSureSourceContainerIsNotTheSameAsTargetContainer() {
		sourceContainerSelector.getTargetObjectPredicates().add(
			isNotTheSame(getTargetContainerSelector().getTargetObject()));
	}

	private void makeSureSourceFeatureIsCompatibleWithAnyFeatureOfTargetContainer() {
		sourceContainerSelector.getTargetFeaturePredicates().add(
			isCompatibleWithAnyFeatureOfEClass(getTargetContainerSelector().getTargetObject().eClass()));
	}

	private void makeSureSourceFeatureIsCompatibleWithTargetFeature() {
		sourceContainerSelector.getTargetFeaturePredicates().add(
			hasCompatibleType(getTargetContainerSelector().getTargetFeature()));
	}

	private void makeSureTargetContainerIsNotTheSameAsSourceContainer() {
		getTargetContainerSelector().getTargetObjectPredicates().add(
			isNotTheSame(sourceContainerSelector.getTargetObject()));
	}

	private void selectEObjectToMove() throws ESMutationException {
		// we assume that source selector has already selected everything now
		final Collection<Predicate<? super Object>> predicates = new HashSet<Predicate<? super Object>>();
		predicates.addAll(predicatesOnEObjectToMoveFromSourceSelector());
		predicates.addAll(predicatesOnEObjectToMoveFromTargetSelector());

		final Object objectToMove = sourceContainerSelector.selectRandomContainedValue(and(predicates));

		if (objectToMove != null && objectToMove instanceof EObject) {
			setEObjectToMove((EObject) objectToMove);
		} else {
			throw new ESMutationException("Cannot find object to move."); //$NON-NLS-1$
		}
	}

	private Collection<Predicate<? super Object>> predicatesOnEObjectToMoveFromSourceSelector() {
		final Collection<Predicate<? super Object>> predicates = new HashSet<Predicate<? super Object>>();
		if (sourceContainerSelector.getTargetFeature() != null) {
			predicates.add(getIsContainedByFeaturePredicate());
		}
		if (sourceContainerSelector.getTargetObject() != null) {
			predicates.add(getIsContainedByEObjectPredicate());
		}
		return predicates;
	}

	private Collection<Predicate<? super Object>> predicatesOnEObjectToMoveFromTargetSelector() {
		final Collection<Predicate<? super Object>> predicates = new HashSet<Predicate<? super Object>>();
		if (haveTargetFeature()) {
			predicates.add(getMayBeContainedByFeaturePredicate());
		} else if (haveTargetContainer()) {
			final EClass targetEClass = getTargetContainerSelector().getTargetObject().eClass();
			predicates.add(getMayBeContainedByAnyOfTheseReferencesPredicate(targetEClass));
		}
		return predicates;
	}

	@SuppressWarnings("unchecked")
	private Predicate<? super Object> getIsContainedByEObjectPredicate() {
		return (Predicate<? super Object>) isContainedByEObject(sourceContainerSelector.getTargetObject());
	}

	@SuppressWarnings("unchecked")
	private Predicate<? super Object> getIsContainedByFeaturePredicate() {
		return (Predicate<? super Object>) isContainedByFeature(sourceContainerSelector.getTargetFeature());
	}

	@SuppressWarnings("unchecked")
	private Predicate<? super Object> getMayBeContainedByAnyOfTheseReferencesPredicate(final EClass targetEClass) {
		return (Predicate<? super Object>) mayBeContainedByAnyOfTheseReferences(targetEClass.getEAllContainments());
	}

	@SuppressWarnings("unchecked")
	private Predicate<? super Object> getMayBeContainedByFeaturePredicate() {
		return (Predicate<? super Object>) mayBeContainedByFeature(getTargetContainerSelector().getTargetFeature());
	}

	private void makeSureTargetContainerIsNotChildOfEObjectToMove() {
		getTargetContainerSelector().getTargetObjectPredicates().add(
			not(isChild(getEObjectToMove())));
	}

	private void makeSureSourceContainerIsNotAncesterOfTargetContainer() {
		sourceContainerSelector.getTargetObjectPredicates().add(
			not(isAncestor(getTargetContainerSelector().getTargetObject())));
	}

}
