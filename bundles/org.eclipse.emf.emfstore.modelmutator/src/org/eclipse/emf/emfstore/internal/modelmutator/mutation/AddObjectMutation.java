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

import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isNullValueOrList;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.mayTakeEObjectAsValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;

/**
 * A mutation, which adds a new object into the model.
 *
 * @author Philip Langer
 *
 */
public class AddObjectMutation extends ContainmentChangeMutation {

	private EObject eObjectToAdd;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public AddObjectMutation(ModelMutatorUtil util) {
		super(util);
		addIsEmptySingleValuedReferenceOrMultivalueReferencePredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected AddObjectMutation(ModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addIsEmptySingleValuedReferenceOrMultivalueReferencePredicate();
	}

	private void addIsEmptySingleValuedReferenceOrMultivalueReferencePredicate() {
		targetContainerSelector.getOriginalFeatureValuePredicates().add(isNullValueOrList);
	}

	/**
	 * Sets the object to be added by this mutation.
	 *
	 * @param eObjectToAdd The object to be added.
	 */
	public void setEObjectToAdd(EObject eObjectToAdd) {
		this.eObjectToAdd = eObjectToAdd;
	}

	/**
	 * Returns the object added or to be added by this mutation.
	 *
	 * @return The added or to-be-added object.
	 */
	public EObject getEObjectToAdd() {
		return eObjectToAdd;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		final AddObjectMutation mutation = new AddObjectMutation(getUtil(), targetContainerSelector);
		mutation.setEObjectToAdd(getEObjectToAdd());
		return mutation;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#doApply()
	 */
	@Override
	protected boolean doApply() throws MutationException {
		doSelection();

		final EObject eObjectToAdd = getOrCreateEObjectToAdd();
		addEObjectToTargetContainer(eObjectToAdd);
		getUtil().addedEObject(eObjectToAdd);

		return true;
	}

	private void doSelection() throws MutationException {
		if (getEObjectToAdd() != null) {
			targetContainerSelector.getTargetFeaturePredicates().add(
				mayTakeEObjectAsValue(getEObjectToAdd()));
		}
		targetContainerSelector.doSelection();
	}

	private EObject getOrCreateEObjectToAdd() {
		if (getEObjectToAdd() == null) {
			setEObjectToAdd(createEObjectToAdd());
		}
		return getEObjectToAdd();
	}

	private EClass selectEClassToInstantiate() {
		final List<EClass> eClasses = getAllInstatiableEClassesCompatibleWithSelectedFeature();
		final int randomIndex = getRandom().nextInt(eClasses.size());
		return eClasses.get(randomIndex);
	}

	private List<EClass> getAllInstatiableEClassesCompatibleWithSelectedFeature() {
		final EReference reference = (EReference) targetContainerSelector.getTargetFeature();
		final List<EClass> eClasses = getUtil().getAllEContainments(reference);
		eClasses.removeAll(getUtil().getModelMutatorConfiguration().geteClassesToIgnore());
		for (final EClass eClass : new ArrayList<EClass>(eClasses)) {
			if (!ModelMutatorUtil.canHaveInstance(eClass)) {
				eClasses.remove(eClass);
			}
		}
		return eClasses;
	}

	private EObject createEObjectToAdd() {
		final EClass eClassToInstantiate = selectEClassToInstantiate();
		final EObject eObjectToAdd = EcoreUtil.create(eClassToInstantiate);
		getUtil().setEObjectAttributes(eObjectToAdd);
		return eObjectToAdd;
	}

	private void addEObjectToTargetContainer(final EObject eObjectToAdd) {
		final EObject targetObject = targetContainerSelector.getTargetObject();
		final EReference targetReference = (EReference) targetContainerSelector.getTargetFeature();

		final Random random = getRandom();
		if (targetReference.isMany()) {
			final Integer insertionIndex = random.nextBoolean() ? 0 : null;
			getUtil().addPerCommand(targetObject, targetReference, eObjectToAdd, insertionIndex);
		} else {
			getUtil().setPerCommand(targetObject, targetReference, eObjectToAdd);
		}
	}
}
