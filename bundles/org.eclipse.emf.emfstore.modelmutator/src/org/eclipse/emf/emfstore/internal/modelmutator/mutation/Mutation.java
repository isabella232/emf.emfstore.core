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

import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;

/**
 * Abstract mutation acting as a common super class of specific implementations of mutations.
 *
 * @author Philip Langer
 *
 */
public abstract class Mutation implements Cloneable {

	private final ModelMutatorUtil util;

	/**
	 * Creates a new mutation with the specified {@code util}.
	 *
	 * @param util The model mutator util used for accessing the model to be mutated.
	 */
	protected Mutation(ModelMutatorUtil util) {
		this.util = util;
	}

	/**
	 * Returns the {@link ModelMutatorUtil model mutator utility} that is used by this mutation.
	 *
	 * @return The used {@link ModelMutatorUtil model mutator utility}.
	 */
	protected final ModelMutatorUtil getUtil() {
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
	 * Applies this mutation and returns whether it succeeded in being applied.
	 *
	 * @return <code>true</code> if this mutation succeeded in being applied successfully, <code>false</code> otherwise.
	 */
	public final boolean apply() {
		try {
			return doApply();
		} catch (final MutationException e) {
			return false;
		}
	}

	/**
	 * Sub-classes have to override this method to actually perform the specific mutation.
	 *
	 * @return <code>true</code> if the mutation has been applied successfully, <code>false</code> otherwise.
	 * @throws MutationException Thrown if the mutation failed.
	 */
	protected abstract boolean doApply() throws MutationException;

}
