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
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.hasFeatureMapEntryType;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isMultiValued;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isMutatableAttribute;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isNonEmptyValueOrList;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;
import org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetter;

import com.google.common.base.Predicate;

/**
 * A mutation, which adds, deletes, or reorders an attribute value of an object.
 *
 * @author Philip Langer
 *
 */
public class AttributeChangeMutation extends StructuralFeatureMutation {

	/**
	 * The modes in which an attribute can be changed.
	 *
	 * @author Philip Langer
	 *
	 */
	protected enum AttributeChangeMode {
		/** Adding or setting a new attribute value. */
		ADD,
		/** Deleting or unsetting an existing attribute value. */
		DELETE,
		/** Reordering existing values of a multi-valued attributes. */
		REORDER
	}

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public AttributeChangeMutation(ModelMutatorUtil util) {
		super(util);
		addGeneralAttributeChangeMutationPredicates();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected AttributeChangeMutation(ModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addGeneralAttributeChangeMutationPredicates();
	}

	private void addGeneralAttributeChangeMutationPredicates() {
		addTargetFeatureAttributePredicate();
		addAttributeTypeNotFeatureMapPredicate();
		addAttributeTypeNotEEnumeratorPredicate();
	}

	private void addTargetFeatureAttributePredicate() {
		targetContainerSelector.getTargetFeaturePredicates().add(isMutatableAttribute);
	}

	private void addAttributeTypeNotFeatureMapPredicate() {
		targetContainerSelector.getTargetFeaturePredicates().add(not(hasFeatureMapEntryType));
	}

	private void addAttributeTypeNotEEnumeratorPredicate() {
		targetContainerSelector.getTargetFeaturePredicates().add(
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
		return new AttributeChangeMutation(getUtil(), targetContainerSelector);
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
			return doAddAttributeValue();
		case DELETE:
			return doDeleteAttributeValue();
		case REORDER:
			return doReorderAttributeValue();
		default:
			return false;
		}
	}

	private boolean doAddAttributeValue() throws MutationException {
		targetContainerSelector.doSelection();
		final EObject eObject = targetContainerSelector.getTargetObject();
		final EAttribute eAttribute = (EAttribute) targetContainerSelector.getTargetFeature();

		Object newValue = createNewValue(eAttribute);
		if (newValue != null && eAttribute.isID() && !getUtil().getModelMutatorConfiguration().isAllowDuplicateIDs())
		{
			while (!getUtil().isUniqueID(newValue)) {
				newValue = createNewValue(eAttribute);
			}
			getUtil().registerID(newValue);
		}
		if (eAttribute.isMany()) {
			final int insertionIndex = targetContainerSelector.
				getRandomIndexFromTargetObjectAndFeatureValueRange();
			getUtil().setPerCommand(eObject, eAttribute, newValue, insertionIndex);
		} else {
			getUtil().setPerCommand(eObject, eAttribute, newValue);
		}
		return true;
	}

	private boolean doDeleteAttributeValue() throws MutationException {
		makeSureWeHaveAValueInSelectedObjectAtSelectedFeature();
		targetContainerSelector.doSelection();
		final EObject eObject = targetContainerSelector.getTargetObject();
		final EAttribute eAttribute = (EAttribute) targetContainerSelector.getTargetFeature();

		if (eAttribute.isMany()) {
			final List<?> currentValues = (List<?>) eObject.eGet(eAttribute);
			final int deletionIndex = targetContainerSelector.
				getRandomIndexFromTargetObjectAndFeatureValueRange();
			currentValues.remove(deletionIndex);
			getUtil().setPerCommand(eObject, eAttribute, currentValues);
		} else {
			getUtil().setPerCommand(eObject, eAttribute, SetCommand.UNSET_VALUE);
		}
		return true;
	}

	private void makeSureWeHaveAValueInSelectedObjectAtSelectedFeature() {
		targetContainerSelector.getOriginalFeatureValuePredicates().add(isNonEmptyValueOrList);
	}

	private boolean doReorderAttributeValue() throws MutationException {
		final boolean success;
		makeSureAttributeIsMutliValued();
		makeSureWeHaveAValueInSelectedObjectAtSelectedFeature();
		targetContainerSelector.doSelection();
		final EObject eObject = targetContainerSelector.getTargetObject();
		final EAttribute eAttribute = (EAttribute) targetContainerSelector.getTargetFeature();

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
		targetContainerSelector.getTargetFeaturePredicates().add(isMultiValued);
	}

	/**
	 * Creates a new value suitable to be set for the specified {@code eAttribute}.
	 *
	 * @param eAttribute The attribute to create a value for.
	 * @return The created new value suitable for {@code eAttribute}.
	 */
	protected Object createNewValue(EAttribute eAttribute) {
		final AttributeSetter<?> setter = getUtil().getAttributeSetter(eAttribute.getEType());
		return setter.createNewAttribute();
	}

	/**
	 * Returns a random change mode.
	 *
	 * @return A random change mode.
	 */
	protected AttributeChangeMode getRandomChangeMode() {
		final AttributeChangeMode[] values = AttributeChangeMode.values();
		final int nextInt = getRandom().nextInt(values.length);
		return values[nextInt];
	}

}