/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

import com.google.common.base.Optional;

/**
 * Type for emitting {@link AbstractOperation}s in their serialized form when given a {@link ReadLineCapable} type.
 *
 * @author Johannes Faltermeier
 *
 */
public class SerializedOperationEmitter extends AbstractOperationEmitter {

	/**
	 * Constructor.
	 *
	 * @param direction
	 *            the {@link Direction} that is used for reading
	 * @param file
	 *            the operation file
	 */
	public SerializedOperationEmitter(Direction direction, File file) {
		super(direction, file);
	}

	/**
	 * Tries to parse an operation in the reading directions and emits it,
	 * if parsing has been successful. The operation is returned in its parsed string representation.
	 *
	 * @return the successfully parsed operation as a string representation
	 * @throws IOException
	 *             in case reading from the {@link ReadLineCapable} fails
	 */
	public Optional<String> tryEmit() throws IOException {
		final PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream(pos);

		new Thread(new Runnable() {
			public void run() {
				if (getDirection() == Direction.Forward) {
					readForward(pos);
				} else {
					readBackward(pos);
				}
			}
		}).start();

		try {
			final String streamToString = convertStreamToString(pis);
			if (XmlTags.XML_RESOURCE_WITH_EOBJECT.equals(streamToString)) {
				return Optional.absent();
			}
			return Optional.of(streamToString);
		} finally {
			pis.close();
		}
	}

	private static String convertStreamToString(InputStream inputStream) {
		final Scanner scanner = new Scanner(inputStream);
		scanner.useDelimiter("\\A"); //$NON-NLS-1$
		final String result = scanner.hasNext() ? scanner.next() : ""; //$NON-NLS-1$
		scanner.close();
		return result;
	}

}
