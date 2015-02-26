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
package org.eclipse.emf.emfstore.server;

/**
 * Represents an {@link Iterable} that must be closed after consumption.
 * Note that a {@link ESCloseableIterable} is not an {@link Iterable} itself.
 *
 * @author emueller
 * @since 1.5
 *
 * @param <T> the type to iterate over
 */
public interface ESCloseableIterable<T> {

	/**
	 * Closes this {@link Iterable}.
	 */
	void close();

	/**
	 * Returns an {@link Iterable} that can be iterated over.
	 *
	 * @return an {@link Iterable} to iterate over
	 */
	Iterable<T> iterable();
}
