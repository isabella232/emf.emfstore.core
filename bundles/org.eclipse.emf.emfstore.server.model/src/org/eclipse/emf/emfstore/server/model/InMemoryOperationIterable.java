/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.model;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * In-memory representation of an {@link ESCloseableIterable} for operations.
 * Calling {@code close} has no effect.
 *
 * @author emueller
 * @since 1.5
 *
 */
public class InMemoryOperationIterable implements ESCloseableIterable<ESOperation> {

	private final List<ESOperation> operations;

	/**
	 * Defaults constructor.
	 *
	 * @param operations
	 *            the in-memory representation of the operations
	 */
	public InMemoryOperationIterable(List<ESOperation> operations) {
		this.operations = operations;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESCloseableIterable#iterable()
	 */
	@Override
	public Iterable<ESOperation> iterable() {
		return new Iterable<ESOperation>() {
			@Override
			public Iterator<ESOperation> iterator() {
				return operations.iterator();
			}
		};
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESCloseableIterable#close()
	 */
	@Override
	public void close() {
		// nothing to do
	}

}
