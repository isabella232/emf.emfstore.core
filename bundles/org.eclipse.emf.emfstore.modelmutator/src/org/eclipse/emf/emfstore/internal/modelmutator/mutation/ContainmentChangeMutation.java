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

import org.eclipse.emf.emfstore.modelmutator.ESContainmentChangeMutation;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;

/**
 * Abstract mutation for mutating the containment tree of models.
 * 
 * @author Philip Langer
 * 
 * @param <M> the implementing API mutation type
 */
public abstract class ContainmentChangeMutation<M extends ESContainmentChangeMutation<?>> extends
	StructuralFeatureMutation<M> {

	/**
	 * Creates a new mutation with the specified {@code util} making sure that only containment references are selected
	 * as target feature.
	 * 
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	public ContainmentChangeMutation(ESModelMutatorUtil util) {
		super(util);
		addTargetFeatureContainmentPredicate();
	}

	/**
	 * Creates a new mutation with the specified {@code util} and the {@code selector} making sure that
	 * only containment references are selected as target feature.
	 * 
	 * @param util The model mutator util used for accessing the model to be mutated.
	 * @param selector The target selector for selecting the target container and feature.
	 */
	protected ContainmentChangeMutation(ESModelMutatorUtil util, MutationTargetSelector selector) {
		super(util);
		addTargetFeatureContainmentPredicate();
	}

	private void addTargetFeatureContainmentPredicate() {
		getTargetContainerSelector().getTargetFeaturePredicates().add(
			MutationPredicates.IS_MUTABLE_CONTAINMENT_REFERENCE);
	}

}