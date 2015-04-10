/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang.NotImplementedException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

import com.google.common.base.Optional;

/**
 * A file-based iterator for {@link AbstractOperation}s.
 *
 * @author emueller
 * @since 1.5
 *
 */
public class OperationIterator implements Iterator<AbstractOperation> {

	private Optional<AbstractOperation> operation;
	private OperationEmitter operationEmitter;
	private ReadLineCapable reader;
	private boolean isInitialized;
	private final String operationsFilePath;
	private final Direction direction;

	/**
	 * Constructor.
	 *
	 * @param operationsFilePath
	 *            the absolute path to the operations file
	 * @param direction
	 *            the reading {@link Direction}
	 */
	public OperationIterator(String operationsFilePath, Direction direction) {
		this.operationsFilePath = operationsFilePath;
		this.direction = direction;
		init();
	}

	private void init() {
		operationEmitter = new OperationEmitter(direction);
		try {
			if (direction == Direction.Forward) {
				reader = ReadLineCapable.INSTANCE.create(new BufferedReader(
					new FileReader(new File(operationsFilePath))));
			} else {
				reader = ReadLineCapable.INSTANCE.create(new ReversedLinesFileReader(new File(operationsFilePath)));
			}
		} catch (final IOException ex1) {
			ex1.printStackTrace();
		}
		isInitialized = true;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (!isInitialized) {
			init();
		}
		try {
			operation = operationEmitter.tryEmit(reader);
			final boolean hasNext = operation.isPresent();
			if (!hasNext) {
				close();
			}
			return hasNext;
		} catch (final IOException ex) {
			// replace operations file
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#next()
	 */
	@Override
	public AbstractOperation next() {
		if (operation == null) {
			hasNext();
		}
		return operation.get();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new NotImplementedException();
	}

	/**
	 * Closes the underlying operations file.
	 */
	public void close() {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (final IOException ex) {
			// TODO
			ex.printStackTrace();
		}
	}
}
