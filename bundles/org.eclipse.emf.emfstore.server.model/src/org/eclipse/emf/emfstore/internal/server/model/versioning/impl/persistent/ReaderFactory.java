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
import java.io.IOException;

import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 * Factory for creating types that are {@link ReadLineCapable}.
 *
 * @author emueller
 *
 */
public class ReaderFactory {

	/**
	 * Creates a {@link ReadLineCapable} type that is backed by a {@link BufferedReader}.
	 *
	 * @param reader
	 *            the {@link BufferedReader} to be wrapped
	 * @return a {@link ReadLineCapable} type
	 */
	public ReadLineCapable create(final BufferedReader reader) {
		return new ReadLineCapable() {
			public String readLine() throws IOException {
				return reader.readLine();
			}

			public void close() throws IOException {
				reader.close();
			}
		};
	}

	/**
	 * Creates a {@link ReadLineCapable} type that is backed by a {@link ReversedLinesFileReader}.
	 *
	 * @param reversedReader
	 *            the {@link ReversedLinesFileReader} to be wrapped
	 * @return a {@link ReadLineCapable} type
	 */
	public ReadLineCapable create(final ReversedLinesFileReader reversedReader) {
		return new ReadLineCapable() {
			public String readLine() throws IOException {
				return reversedReader.readLine();
			}

			public void close() throws IOException {
				reversedReader.close();
			}
		};
	}
}
