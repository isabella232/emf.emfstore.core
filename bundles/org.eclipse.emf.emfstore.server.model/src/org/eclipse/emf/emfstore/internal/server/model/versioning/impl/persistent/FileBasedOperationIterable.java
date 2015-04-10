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
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

import java.util.Iterator;

import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * Enables to iterate through all operations that are saved within an underlying
 * operations file.<br>
 * <strong>NOTE</strong>: Callers must call {@code close} when they are finished.
 *
 * @author emueller
 * @since 1.5
 *
 */
public class FileBasedOperationIterable implements ESCloseableIterable<AbstractOperation> {

	private OperationIterator operationIterator;
	private final String operationsFilePath;
	private final Direction direction;

	/**
	 * Constructor.
	 *
	 * @param operationsFilePath
	 *            the absolute path to the underlying operations file
	 * @param direction
	 *            the read {@link Direction}
	 */
	public FileBasedOperationIterable(String operationsFilePath, Direction direction) {
		this.operationsFilePath = operationsFilePath;
		this.direction = direction;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterable<AbstractOperation> iterable() {
		operationIterator = new OperationIterator(operationsFilePath, direction);
		return new Iterable<AbstractOperation>() {
			@Override
			public Iterator<AbstractOperation> iterator() {
				return operationIterator;
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
		if (operationIterator != null) {
			operationIterator.close();
		}
	}

}
