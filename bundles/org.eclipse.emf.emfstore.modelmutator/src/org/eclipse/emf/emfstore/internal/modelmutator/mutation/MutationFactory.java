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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil;

/**
 * A factory for creating {@link Mutation mutations}.
 *
 * @author Philip Langer
 */
public class MutationFactory implements Iterable<Mutation> {

	private final ModelMutatorUtil util;
	private final List<Mutation> mutationPrototypes;

	public MutationFactory(ModelMutatorUtil util, List<Mutation> mutationPrototypes) {
		this.util = util;
		this.mutationPrototypes = mutationPrototypes;
	}

	public MutationFactory(ModelMutatorUtil util) {
		this(util, mutationPrototypesFromConfig(util));
	}

	private static List<Mutation> mutationPrototypesFromConfig(ModelMutatorUtil util) {
		final List<Mutation> mutationPrototypes = new ArrayList<Mutation>();
		// TODO implement initialization from config
		mutationPrototypes.add(new AddObjectMutation(util));
		mutationPrototypes.add(new DeleteObjectMutation(util));

		return mutationPrototypes;
	}

	private Mutation getNextMutation() {
		final int randomMutationIndex = random().nextInt(mutationPrototypes.size());
		final Mutation randomMutationPrototype = mutationPrototypes.get(randomMutationIndex);
		return randomMutationPrototype.clone();
	}

	private Random random() {
		return util.getModelMutatorConfiguration().getRandom();
	}

	public Iterator<Mutation> iterator(final int number) {
		return new Iterator<Mutation>() {

			private int count = 0;

			public boolean hasNext() {
				return !mutationPrototypes.isEmpty()
					&& count < mutationPrototypes.size()
					&& count <= number;
			}

			public Mutation next() {
				count++;
				return getNextMutation();
			}

		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Mutation> iterator() {
		return new Iterator<Mutation>() {

			public boolean hasNext() {
				return !mutationPrototypes.isEmpty();
			}

			public Mutation next() {
				return getNextMutation();
			}

		};
	}

}
