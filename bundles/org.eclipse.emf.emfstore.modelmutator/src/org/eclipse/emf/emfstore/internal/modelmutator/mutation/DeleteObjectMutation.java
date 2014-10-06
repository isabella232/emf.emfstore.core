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

import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.containsEObjectWithMaxNumberOfContainments;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.hasMaxNumberOfContainments;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isNonEmptyEObjectValueOrList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;

import com.google.common.base.Predicate;

/**
 * A mutation, which deletes a new object from the model.
 *
 * @author Philip Langer
 *
 */
public class DeleteObjectMutation extends ContainmentChangeMutation {

	private int maxNumberOfContainments;
	private EObject eObjectToDelete;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public DeleteObjectMutation(ModelMutatorUtil util) {
		super(util);
		addHasObjectToDeletePredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	public DeleteObjectMutation(ModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addHasObjectToDeletePredicate();
	}

	private void addHasObjectToDeletePredicate() {
		targetContainerSelector.getOriginalFeatureValuePredicates().add(isNonEmptyEObjectValueOrList);
	}

	/**
	 * Sets the maximum number of containments that the object selected for deletion may contain.
	 *
	 * @param maxNumberOfContainments The maximum number of containments of the object to be deleted.
	 */
	public void setMaxNumberOfContainments(int maxNumberOfContainments) {
		this.maxNumberOfContainments = maxNumberOfContainments;
	}

	/**
	 * Returns the maximum number of containments that the object selected for deletion may contain.
	 *
	 * @return The maximum number of containments of the object to be deleted.
	 */
	public int getMaxNumberOfContainments() {
		return maxNumberOfContainments;
	}

	/**
	 * Sets the object to be deleted by this mutation.
	 *
	 * @param eObjectToDelete The object to be deleted.
	 */
	public void setEObjectToDelete(EObject eObjectToDelete) {
		this.eObjectToDelete = eObjectToDelete;
	}

	/**
	 * Returns the object deleted or to be deleted by this mutation.
	 *
	 * @return The deleted or to-be-deleted object.
	 */
	public EObject getEObjectToDelete() {
		return eObjectToDelete;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation#clone()
	 */
	@Override
	public Mutation clone() {
		final DeleteObjectMutation mutation = new DeleteObjectMutation(getUtil(), targetContainerSelector);
		mutation.setMaxNumberOfContainments(maxNumberOfContainments);
		mutation.setEObjectToDelete(getEObjectToDelete());
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

		final EObject eObjectToDelete = getOrSelectEObjectToDelete();
		final int deleteMode = getUtil().getRandomDeleteMode();
		// TODO we should use the removeFullPerCommand but it does not work in the tests
		EcoreUtil.delete(eObjectToDelete);
		// getUtil().removeFullPerCommand(eObjectToDelete, deleteMode);
		getUtil().deletedEObject(eObjectToDelete);

		return true;
	}

	private void doSelection() throws MutationException {
		if (getEObjectToDelete() == null) {
			targetContainerSelector.getOriginalFeatureValuePredicates().add(
				containsEObjectWithMaxNumberOfContainments(maxNumberOfContainments));
			targetContainerSelector.doSelection();
		}
	}

	private EObject getOrSelectEObjectToDelete() throws MutationException {
		if (getEObjectToDelete() == null) {
			final Predicate<? super Object> predicate = getMaxNumberOfContainmentsPredicate();
			final Object objectToDelete = targetContainerSelector.selectRandomContainedValue(predicate);
			if (objectToDelete != null && objectToDelete instanceof EObject) {
				setEObjectToDelete((EObject) objectToDelete);
			} else {
				throw new MutationException("Cannot find object to delete.");
			}
		}
		return getEObjectToDelete();
	}

	@SuppressWarnings("unchecked")
	private Predicate<Object> getMaxNumberOfContainmentsPredicate() {
		return (Predicate<? super Object>) hasMaxNumberOfContainments(maxNumberOfContainments);
	}
}
