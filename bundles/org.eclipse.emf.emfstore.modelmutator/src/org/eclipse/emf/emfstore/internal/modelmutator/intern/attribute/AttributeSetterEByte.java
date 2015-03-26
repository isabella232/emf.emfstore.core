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
 * Class for creating random Byte values.
 *
 * @author Eugen Neufeld
 * @author Stephan Koehler
 * @author Philip Achenbach
 *
 * @see AttributeSetter
 */
public class AttributeSetterEByte extends AttributeSetter<Byte> {

	/**
	 * Creates a new AttributeSetter for Byte attributes.
	 *
	 * @param random
	 *            Random object used to create attribute values
	 */
	public AttributeSetterEByte(Random random) {
		super(random);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Byte createNewAttribute() {
		final byte[] singlebyte = new byte[1];
		getRandom().nextBytes(singlebyte);
		return singlebyte[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Byte> createNewAttributes(int maxAmount) {
		final List<Byte> result = new ArrayList<Byte>(maxAmount);
		for (int i = 0; i < maxAmount; i++) {
			result.add(createNewAttribute());
		}
		return result;
	}

}
