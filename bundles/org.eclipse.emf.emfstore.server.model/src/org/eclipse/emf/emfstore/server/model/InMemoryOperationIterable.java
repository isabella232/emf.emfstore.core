/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.model;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * @author Edgar
 * @since 1.4
 *
 */
public class InMemoryOperationIterable implements CloseableIterable<AbstractOperation> {

	private final List<AbstractOperation> operations;

	public InMemoryOperationIterable(List<AbstractOperation> operations) {
		this.operations = operations;
	}

	public Iterable<AbstractOperation> iterable() {
		return new Iterable<AbstractOperation>() {
			public Iterator<AbstractOperation> iterator() {
				return operations.iterator();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.CloseableIterable#close()
	 */
	public void close() {
		// nothing to do
	}

}
