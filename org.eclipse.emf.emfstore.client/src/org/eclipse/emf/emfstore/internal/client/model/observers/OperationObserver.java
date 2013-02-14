/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.observers;

import org.eclipse.emf.emfstore.common.IObserver;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

/**
 * Operation observers are added to a project space and informed whenever an operation is executed or undone.
 * 
 * @author hodaie
 */
public interface OperationObserver extends IObserver {

	/**
	 * Called when an {@link AbstractOperation} has been executed.
	 * 
	 * @param operation
	 *            the executed operation
	 */
	void operationExecuted(AbstractOperation operation);

	/**
	 * Called when an {@link AbstractOperation} has been reversed.
	 * 
	 * @param reversedOperation
	 *            the reversed operation that has been executed
	 */
	void operationUnDone(AbstractOperation reversedOperation);
}