/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import org.eclipse.emf.emfstore.internal.modelmutator.mutation.AddObjectMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.AttributeChangeMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.DeleteObjectMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.FeatureMapKeyMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.MoveObjectMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.ReferenceChangeMutation;

/**
 * Factory for creating mutations.
 * 
 * @author emueller
 * @since 2.0
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class ESMutationFactory {

	/**
	 * Private constructor.
	 */
	private ESMutationFactory() {

	}

	/**
	 * Creates an {@link ESAddObjectMutation}.
	 * 
	 * @param util
	 *            the model mutator utility used for accessing the model to be mutated
	 * @return the created {@link ESAddObjectMutation}
	 */
	public static ESAddObjectMutation add(ESModelMutatorUtil util) {
		return new AddObjectMutation(util);
	}

	/**
	 * Creates an {@link ESAttributeChangeMutation}.
	 * 
	 * @param util
	 *            the model mutator utility used for accessing the model to be mutated
	 * @return the created {@link ESAttributeChangeMutation}
	 */
	public static ESAttributeChangeMutation attributeChange(ESModelMutatorUtil util) {
		return new AttributeChangeMutation(util);
	}

	/**
	 * Creates an {@link ESFeatureMapKeyMutation}.
	 * 
	 * @param util
	 *            the model mutator utility used for accessing the model to be mutated
	 * @return the created {@link ESFeatureMapKeyMutation}
	 */
	public static ESFeatureMapKeyMutation featureMap(ESModelMutatorUtil util) {
		return new FeatureMapKeyMutation(util);
	}

	/**
	 * Creates an {@link ESDeleteObjectMutation}.
	 * 
	 * @param util
	 *            the model mutator utility used for accessing the model to be mutated
	 * @return the created {@link ESFeatureMapKeyMutation}
	 */
	public static ESDeleteObjectMutation delete(ESModelMutatorUtil util) {
		return new DeleteObjectMutation(util);
	}

	/**
	 * Creates an {@link ESMoveObjectMutation}.
	 * 
	 * @param util
	 *            the model mutator utility used for accessing the model to be mutated
	 * @return the created {@link ESMoveObjectMutation}
	 */
	public static ESMoveObjectMutation move(ESModelMutatorUtil util) {
		return new MoveObjectMutation(util);
	}

	/**
	 * Creates an {@link ESReferenceChangeMutation}.
	 * 
	 * @param util
	 *            the model mutator utility used for accessing the model to be mutated
	 * @return the created {@link ESReferenceChangeMutation}
	 */
	public static ESReferenceChangeMutation referenceChange(ESModelMutatorUtil util) {
		return new ReferenceChangeMutation(util);
	}
}
