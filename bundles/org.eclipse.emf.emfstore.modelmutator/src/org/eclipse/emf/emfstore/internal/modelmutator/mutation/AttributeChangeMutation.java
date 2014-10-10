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
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.HAS_FEATURE_MAP_ENTRY_TYPE;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_MULTI_VALUED;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_MUTABLE_ATTRIBUTE;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_NON_EMPTY_VALUE_OR_LIST;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.emfstore.modelmutator.ESAttributeChangeMutation;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESRandomChangeMode;

import com.google.common.base.Predicate;

/**
 * A mutation, which adds, deletes, or reorders an attribute value of an object.
 * 
 * @author Philip Langer
 * 
 */
public class AttributeChangeMutation extends StructuralFeatureMutation<ESAttributeChangeMutation> implements
	ESAttributeChangeMutation {

	private Object newValue;
	private ESRandomChangeMode randomChangeMode;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 * 
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public AttributeChangeMutation(ESModelMutatorUtil util) {
		super(util);
		addGeneralAttributeChangeMutationPredicates();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 * 
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected AttributeChangeMutation(ESModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addGeneralAttributeChangeMutationPredicates();
	}

	private void addGeneralAttributeChangeMutationPredicates() {
		addTargetFeatureAttributePredicate();
		addAttributeTypeNotFeatureMapPredicate();
		addAttributeTypeNotEEnumeratorPredicate();
	}

	private void addTargetFeatureAttributePredicate() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(IS_MUTABLE_ATTRIBUTE);
	}

	private void addAttributeTypeNotFeatureMapPredicate() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(not(HAS_FEATURE_MAP_ENTRY_TYPE));
	}

	private void addAttributeTypeNotEEnumeratorPredicate() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(
			not(new Predicate<EStructuralFeature>() {
				public boolean apply(EStructuralFeature input) {
					return input != null && EcorePackage.eINSTANCE.getEEnumerator().equals(input.getEType());
				}
			}));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		return new AttributeChangeMutation(getUtil(), getTargetContainerSelector());
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
			doAddAttributeValue();
			break;
		case DELETE:
			doDeleteAttributeValue();
			break;
		case REORDER:
			doReorderAttributeValue();
			break;
		default:
			break;
		}
	}

	private boolean doAddAttributeValue() throws ESMutationException {
		getTargetContainerSelector().doSelection();
		final EObject eObject = getTargetContainerSelector().getTargetObject();
		final EAttribute eAttribute = (EAttribute) getTargetContainerSelector().getTargetFeature();

		Object newValue = createNewValue(eAttribute);
		if (newValue != null && eAttribute.isID() && !getUtil().getModelMutatorConfiguration().isAllowDuplicateIDs())
		{
			while (!getUtil().isUniqueID(newValue)) {
				newValue = createNewValue(eAttribute);
			}
			getUtil().registerID(newValue);
		}
		if (eAttribute.isMany()) {
			final int insertionIndex = getTargetContainerSelector().
				getRandomIndexFromTargetObjectAndFeatureValueRange();
			getUtil().setPerCommand(eObject, eAttribute, newValue, insertionIndex);
		} else {
			getUtil().setPerCommand(eObject, eAttribute, newValue);
		}
		return true;
	}

	private boolean doDeleteAttributeValue() throws ESMutationException {
		makeSureWeHaveAValueInSelectedObjectAtSelectedFeature();
		getTargetContainerSelector().doSelection();
		final EObject eObject = getTargetContainerSelector().getTargetObject();
		final EAttribute eAttribute = (EAttribute) getTargetContainerSelector().getTargetFeature();

		if (eAttribute.isMany()) {
			final List<?> currentValues = (List<?>) eObject.eGet(eAttribute);
			final int deletionIndex = getTargetContainerSelector().
				getRandomIndexFromTargetObjectAndFeatureValueRange();
			currentValues.remove(deletionIndex);
			getUtil().setPerCommand(eObject, eAttribute, currentValues);
		} else {
			getUtil().setPerCommand(eObject, eAttribute, SetCommand.UNSET_VALUE);
		}
		return true;
	}

	private void makeSureWeHaveAValueInSelectedObjectAtSelectedFeature() {
		getTargetContainerSelector().getOriginalFeatureValuePredicates().add(IS_NON_EMPTY_VALUE_OR_LIST);
	}

	private boolean doReorderAttributeValue() throws ESMutationException {
		final boolean success;
		makeSureAttributeIsMutliValued();
		makeSureWeHaveAValueInSelectedObjectAtSelectedFeature();
		getTargetContainerSelector().doSelection();
		final EObject eObject = getTargetContainerSelector().getTargetObject();
		final EAttribute eAttribute = (EAttribute) getTargetContainerSelector().getTargetFeature();

		if (eAttribute.isMany()) {
			final List<?> currentValues = (List<?>) eObject.eGet(eAttribute);
			final int numberOfCurrentValues = currentValues.size();
			final int pickIndex = getRandom().nextInt(numberOfCurrentValues);
			final int putIndex = getRandom().nextInt(numberOfCurrentValues);
			getUtil().movePerCommand(eObject, eAttribute, currentValues.get(pickIndex), putIndex);
			success = true;
		} else {
			success = false;
		}
		return success;
	}

	private void makeSureAttributeIsMutliValued() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(IS_MULTI_VALUED);
	}

	/**
	 * Creates a new value suitable to be set for the specified {@code eAttribute}.
	 * 
	 * @param eAttribute The attribute to create a value for.
	 * @return The created new value suitable for {@code eAttribute}.
	 */
	protected Object createNewValue(EAttribute eAttribute) {

		if (newValue != null) {
			return newValue;
		}

		return getUtil().createNewAttribute(eAttribute);
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
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.modelmutator.ESAttributeChangeMutation#setNewValue(java.lang.Object)
	 */
	public ESAttributeChangeMutation setNewValue(Object newValue) {
		this.newValue = newValue;
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.modelmutator.ESAttributeChangeMutation#setRandomChangeMode(org.eclipse.emf.emfstore.modelmutator.ESRandomChangeMode)
	 */
	public ESAttributeChangeMutation setRandomChangeMode(ESRandomChangeMode mode) {
		randomChangeMode = mode;
		return this;
	}

}