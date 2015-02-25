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
package org.eclipse.emf.emfstore.internal.server.model.versioning.persistent;

import java.util.Iterator;

import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * @author Edgar
 * @since 1.4
 *
 */
public class FileBasedOperationIterable implements CloseableIterable<AbstractOperation> {

	enum Direction {
		Forward,
		Backward
	}

	private OperationIterator operationIterator;
	private final String operationsFilePath;
	private final Direction direction;

	public FileBasedOperationIterable(String operationsFilePath, Direction direction) {
		this.operationsFilePath = operationsFilePath;
		this.direction = direction;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterable<AbstractOperation> iterable() {
		operationIterator = new OperationIterator(operationsFilePath, direction);
		return new Iterable<AbstractOperation>() {
			public Iterator<AbstractOperation> iterator() {
				return operationIterator;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.CloseableIterable#close()
	 */
	public void close() {
		if (operationIterator != null) {
			operationIterator.close();
		}
	}

}
