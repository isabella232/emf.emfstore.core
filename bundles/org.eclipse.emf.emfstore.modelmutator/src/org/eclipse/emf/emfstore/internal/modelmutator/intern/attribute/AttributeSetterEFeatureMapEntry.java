/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.eclipse.emf.ecore.change.ChangeFactory;
import org.eclipse.emf.ecore.change.FeatureMapEntry;

/**
 * @author emueller
 * 
 */
// FIXME: currently only creates an uninitialized feature map entry
public class AttributeSetterEFeatureMapEntry extends AttributeSetter<FeatureMapEntry> {

	/**
	 * Creates a new AttributeSetter for a FeatureMapEntry attribute.
	 * 
	 * @param random
	 *            Random object used to create attribute values
	 */
	public AttributeSetterEFeatureMapEntry(Random random) {
		super(random);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetter#createNewAttribute()
	 */
	@Override
	public FeatureMapEntry createNewAttribute() {
		final FeatureMapEntry entry = ChangeFactory.eINSTANCE.createFeatureMapEntry();
		return entry;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.modelmutator.intern.attribute.AttributeSetter#createNewAttributes(int)
	 */
	@Override
	public Collection<FeatureMapEntry> createNewAttributes(int maxAmount) {
		final List<FeatureMapEntry> entries = new ArrayList<FeatureMapEntry>();
		for (int i = 0; i < maxAmount; i++) {
			entries.add(createNewAttribute());
		}
		return entries;
	}

}
