/*******************************************************************************
 * Copyright (c) 2012-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 * Edgar Mueller - API annotations
 * Edgar Mueller - Bug 460275 - Support lazy loading of local change package
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.model;

import java.util.List;

import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * Represents a change package.
 *
 * @author emueller
 * @author wesendon
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESChangePackage {

	/**
	 * Returns the {@link ESLogMessage} that was entered by the
	 * user and is supposed to describe the changes within
	 * the change package.
	 *
	 * @return the log message as entered by the user
	 */
	ESLogMessage getLogMessage();

	/**
	 * Sets the log message of this change package.
	 *
	 * @param logMessage
	 *            the log message to be set
	 */
	void setLogMessage(ESLogMessage logMessage);

	/**
	 * Adds a list of operations to this change package.
	 *
	 * @param ops
	 *            the operations to be added
	 *
	 * @since 1.5
	 */
	void addAll(List<ESOperation> ops);

	/**
	 * Add a single a operation to this change package.
	 *
	 * @param operation
	 *            the operation to be added
	 *
	 * @since 1.5
	 */
	void add(ESOperation operation);

	/**
	 * Clears all operations from this change package.
	 *
	 * @since 1.5
	 */
	void clear();

	/**
	 * Whether this change package contains any operations.
	 *
	 * @return {@code true}, if this change package contains no operations, {@code false} otherwise
	 *
	 * @since 1.5
	 */
	boolean isEmpty();

	/**
	 * Removes the given number of operations from this change package beginning at the
	 * end.
	 *
	 * @param howMany
	 *            the number of operations to be removed
	 * @return the list of removed operations
	 *
	 * @since 1.5
	 */
	List<ESOperation> removeFromEnd(int howMany);

	/**
	 * Returns an {@link ESCloseableIterable} that iterates over all operations
	 * of this change package. <br>
	 * You <strong>MUST</strong> call {@code close} on the returned {@link ESCloseableIterable}.
	 *
	 * @return an {@link ESCloseableIterable} that enables iterating over all operations.
	 *
	 * @since 1.5
	 */
	ESCloseableIterable<ESOperation> operations();

	/**
	 * Returns the operations of this change package in the reversed order.
	 * Note that you must call {@code close()} on the returned {@link ESCloseableIterable}.
	 *
	 * @return the operations in reversed order
	 * @since 1.5
	 */
	ESCloseableIterable<ESOperation> reversedOperations();

	/**
	 * Returns the number of operations this change package contains.
	 *
	 * @return the number of operations within this change package
	 *
	 * @since 1.5
	 *
	 */
	int size();
}
