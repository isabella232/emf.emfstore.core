/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Stephan Koehler, Eugen Neufeld, Philip Achenbach - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Class for creating random Double values.
 *
 * @author Eugen Neufeld
 * @author Stephan Koehler
 * @author Philip Achenbach
 *
 * @see AttributeSetter
 */
public class AttributeSetterEDouble extends AttributeSetter<Double> {

	/**
	 * Creates a new AttributeSetter for Double attributes.
	 *
	 * @param random
	 *            Random object used to create attribute values
	 */
	public AttributeSetterEDouble(Random random) {
		super(random);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double createNewAttribute() {
		return getRandom().nextDouble() * getRandom().nextInt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Double> createNewAttributes(int maxAmount) {
		final List<Double> result = new ArrayList<Double>(maxAmount);
		for (int i = 0; i < maxAmount; i++) {
			result.add(createNewAttribute());
		}
		return result;
	}

}
