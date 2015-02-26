/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 * Edgar Mueller - API annotations
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.model;

import java.util.List;

import org.eclipse.emf.emfstore.internal.server.model.impl.api.CloseableIterable;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

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
	ESLogMessage getCommitMessage();

	/**
	 * Sets the log message of this change package.
	 *
	 * @param logMessage
	 *            the log message to be set
	 */
	void setCommitMessage(ESLogMessage logMessage);

	void addAll(List<? extends AbstractOperation> ops);

	void add(AbstractOperation op);

	void clear();

	boolean isEmpty();

	List<AbstractOperation> removeFromEnd(int n);

	/**
	 * @return
	 */
	CloseableIterable<AbstractOperation> operations();

	CloseableIterable<AbstractOperation> reversedOperations();

	/**
	 *
	 */
	// TODO: check whether we need this method
	// void save();

	/**
	 *
	 */
	int size();
}
