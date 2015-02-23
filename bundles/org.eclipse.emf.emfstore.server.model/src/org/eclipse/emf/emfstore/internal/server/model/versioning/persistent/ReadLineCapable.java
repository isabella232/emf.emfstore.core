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
import java.io.IOException;

import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 * @author Edgar
 * 
 */
public interface ReadLineCapable {

	String readLine() throws IOException;

	void close() throws IOException;

	ReadLineFactory INSTANCE = new ReadLineFactory();
}

class ReadLineFactory {

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
