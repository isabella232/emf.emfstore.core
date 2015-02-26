/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.impl.api;

import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.model.ESOperation;

/**
 * Mapping between {@link ESOperation} and {@link AbstractOperation}.
 *
 */
public class ESOperationImpl extends AbstractAPIImpl<ESOperation, AbstractOperation> implements ESOperation {

	/**
	 * Constructs a new {@link ESOperation} by wrapping an internal operation type.
	 *
	 * @param operation the internal operation
	 */
	public ESOperationImpl(AbstractOperation operation) {
		super(operation);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOperation#getId()
	 */
	public String getId() {
		return toInternalAPI().getIdentifier();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.model.ESOperation#reverse()
	 */
	public ESOperation reverse() {
		return new ESOperationImpl(toInternalAPI().reverse());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.server.model.ESOperation#getModelElementId()
	 */
	public ESModelElementId getModelElementId() {
		return toInternalAPI().getModelElementId().toAPI();
	}
}
