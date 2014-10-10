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
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Class for creating random Date values.
 * 
 * @author Eugen Neufeld
 * @author Stephan Koehler
 * @author Philip Achenbach
 * 
 * @see AttributeSetter
 */
public class AttributeSetterEDate extends AttributeSetter<Date> {

	private static final long HUNDRED_YEARS_MILLIS = 3155670000000L;

	/**
	 * Creates a new AttributeSetter for Date attributes.
	 * 
	 * @param random
	 *            Random object used to create attribute values
	 */
	public AttributeSetterEDate(Random random) {
		super(random);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date createNewAttribute() {
		final long value = (long) (getRandom().nextDouble() * HUNDRED_YEARS_MILLIS);
		return new Date(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Date> createNewAttributes(int maxAmount) {
		final List<Date> result = new ArrayList<Date>(maxAmount);
		for (int i = 0; i < maxAmount; i++) {
			result.add(createNewAttribute());
		}
		return result;
	}

}
