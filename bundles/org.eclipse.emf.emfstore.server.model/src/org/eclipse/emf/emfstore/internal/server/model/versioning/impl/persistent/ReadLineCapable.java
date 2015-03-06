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

import java.io.IOException;

/**
 * Common interface for types that are able to read from a source line-by-line.
 *
 * @author emueller
 * @since 1.5
 *
 */
public interface ReadLineCapable {

	/**
	 * Returns a line read.
	 *
	 * @return the read line
	 *
	 * @throws IOException in case reading fails
	 */
	String readLine() throws IOException;

	/**
	 * Closes the source from which lines have been read.
	 *
	 * @throws IOException in case closing the source fails
	 */
	void close() throws IOException;

	/**
	 * Factory for creating types that are an instances of {@link ReadLineCapable}.
	 */
	ReaderFactory INSTANCE = new ReaderFactory();
}