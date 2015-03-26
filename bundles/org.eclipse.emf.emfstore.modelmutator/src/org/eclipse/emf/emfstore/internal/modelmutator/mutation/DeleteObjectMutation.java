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

import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_NON_EMPTY_EOBJECT_OR_LIST;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.containsEObjectWithMaxNumberOfContainments;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.hasMaxNumberOfContainments;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.modelmutator.ESDeleteObjectMutation;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;

import com.google.common.base.Predicate;

/**
 * A mutation, which deletes a new object from the model.
 *
 * @author Philip Langer
 *
 */
public class DeleteObjectMutation extends ContainmentChangeMutation<ESDeleteObjectMutation> implements
	ESDeleteObjectMutation {

	private int maxNumberOfContainments;
	private EObject eObjectToDelete;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public DeleteObjectMutation(ESModelMutatorUtil util) {
		super(util);
		addHasObjectToDeletePredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	public DeleteObjectMutation(ESModelMutatorUtil util, MutationTargetSelector selector) {
		super(util, selector);
		addHasObjectToDeletePredicate();
	}

	private void addHasObjectToDeletePredicate() {
		getTargetContainerSelector().getOriginalFeatureValuePredicates().add(IS_NON_EMPTY_EOBJECT_OR_LIST);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.modelmutator.ESDeleteObjectMutation#setMaxNumberOfContainments(int)
	 */
	public DeleteObjectMutation setMaxNumberOfContainments(int maxNumberOfContainments) {
		this.maxNumberOfContainments = maxNumberOfContainments;
		return this;
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
		final DeleteObjectMutation mutation = new DeleteObjectMutation(getUtil(), getTargetContainerSelector());
		mutation.setMaxNumberOfContainments(maxNumberOfContainments);
		mutation.setEObjectToDelete(getEObjectToDelete());
		return mutation;
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

		final EObject eObjectToDelete = getOrSelectEObjectToDelete();
		// TODO: unused local variable deleteMode
		// final int deleteMode = getUtil().getRandomDeleteMode();
		// TODO we should use the removeFullPerCommand but it does not work in the tests
		EcoreUtil.delete(eObjectToDelete);
		// getUtil().removeFullPerCommand(eObjectToDelete, deleteMode);
		getUtil().deletedEObject(eObjectToDelete);
	}

	private void doSelection() throws ESMutationException {
		if (getEObjectToDelete() == null) {
			getTargetContainerSelector().getOriginalFeatureValuePredicates().add(
				containsEObjectWithMaxNumberOfContainments(maxNumberOfContainments));
			getTargetContainerSelector().doSelection();
		}
	}

	private EObject getOrSelectEObjectToDelete() throws ESMutationException {
		if (getEObjectToDelete() == null) {
			final Predicate<? super Object> predicate = getMaxNumberOfContainmentsPredicate();
			final Object objectToDelete = getTargetContainerSelector().selectRandomContainedValue(predicate);
			if (objectToDelete != null && objectToDelete instanceof EObject) {
				setEObjectToDelete((EObject) objectToDelete);
			} else {
				throw new ESMutationException(Messages.getString("DeleteObjectMutation.NoObjectForDeleteFound")); //$NON-NLS-1$
			}
		}
		return getEObjectToDelete();
	}

	@SuppressWarnings("unchecked")
	private Predicate<Object> getMaxNumberOfContainmentsPredicate() {
		return (Predicate<Object>) hasMaxNumberOfContainments(maxNumberOfContainments);
	}
}
