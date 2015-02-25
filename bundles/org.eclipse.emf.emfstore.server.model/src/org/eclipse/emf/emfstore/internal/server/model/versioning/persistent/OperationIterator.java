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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.persistent.FileBasedOperationIterable.Direction;

/**
 * @author Edgar
 * @since 1.4
 *
 */
public class OperationIterator implements Iterator<AbstractOperation> {

	private AbstractOperation operation;
	private OperationEmitter operationEmitter;
	private ReadLineCapable reader = null;
	private boolean isInitialized;
	private final String operationsFilePath;
	private final Direction direction;

	public OperationIterator(String operationsFilePath, Direction direction) {
		this.operationsFilePath = operationsFilePath;
		this.direction = direction;
		init();
	}

	/**
	 * @param operationsFilePath
	 * @param isForwardDirection2
	 */
	private void init() {
		operationEmitter = new OperationEmitter(direction);
		try {
			if (direction == Direction.Forward) {
				reader = ReadLineCapable.INSTANCE.create(
					new BufferedReader(new FileReader(new File(operationsFilePath))));
			} else {
				reader = ReadLineCapable.INSTANCE.create(
					new ReversedLinesFileReader(new File(operationsFilePath)));
			}
		} catch (final IOException ex1) {
			ex1.printStackTrace();
		}
		isInitialized = true;
	}

	public boolean hasNext() {
		if (!isInitialized) {
			init();
		}
		try {
			operation = operationEmitter.tryEmit(reader);
			final boolean hasNext = operation != null;
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

	public AbstractOperation next() {
		return operation;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void close() {
		try {
			reader.close();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}
}
