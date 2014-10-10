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

import java.util.Random;

import org.eclipse.emf.emfstore.modelmutator.ESMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;

/**
 * Abstract mutation acting as a common super class of specific implementations of mutations.
 * 
 * @author Philip Langer
 * 
 */
public abstract class Mutation implements Cloneable, ESMutation {

	private final ESModelMutatorUtil util;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 * 
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	protected Mutation(ESModelMutatorUtil util) {
		this.util = util;
	}

	/**
	 * Returns the {@link ESModelMutatorUtil model mutator utility} that is used by this mutation.
	 * 
	 * @return The used {@link ESModelMutatorUtil model mutator utility}.
	 */
	protected final ESModelMutatorUtil getUtil() {
		return util;
	}

	/**
	 * Returns the {@link Random random instance} to be used for generating pseudorandom stream of values. This instance
	 * must be shared and used across all mutations to make sure the pseudorandom values all are based on the same
	 * random seed.
	 * 
	 * @return The random instance to be used.
	 */
	protected Random getRandom() {
		return util.getModelMutatorConfiguration().getRandom();
	}

	/**
	 * Mutations must follow the prototype pattern as org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutator
	 * will clone pre-configured mutations before
	 * they will be completed and applied. This allows clients to provide a specifically configured set mutations and
	 * start the mutation only from cloning and applying the set of pre-configured mutations.
	 * 
	 * @return A copy of this mutation with the same configuration.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract Mutation clone();

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.modelmutator.ESMutation#apply()
	 */
	public abstract void apply() throws ESMutationException;

}
