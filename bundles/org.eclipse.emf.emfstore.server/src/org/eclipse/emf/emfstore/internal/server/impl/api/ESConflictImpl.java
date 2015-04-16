/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.impl.api;

import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.emf.emfstore.internal.common.api.AbstractAPIImpl;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESConflict;
import org.eclipse.emf.emfstore.server.model.ESOperation;

/**
 * Mapping between {@link ESConflict} and {@link ConflictBucket}.
 */
public class ESConflictImpl extends AbstractAPIImpl<ESConflict, ConflictBucket> implements ESConflict {

	/**
	 * Constructs a new instance by wrapping a {@link ConflictBucket}.
	 *
	 * @param internalType the internal object
	 */
	public ESConflictImpl(ConflictBucket internalType) {
		super(internalType);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESConflict#getLocalOperations()
	 */
	public Set<ESOperation> getLocalOperations() {
		return APIUtil.toExternal(toInternalAPI().getMyOperations());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESConflict#getRemoteOperations()
	 */
	public Set<ESOperation> getRemoteOperations() {
		return APIUtil.toExternal(toInternalAPI().getTheirOperations());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.server.ESConflict#resolveConflict(java.util.Set, java.util.Set)
	 */
	public void resolveConflict(Set<ESOperation> acceptedLocalOperations, Set<ESOperation> rejectedRemoteOperations) {
		final Set<AbstractOperation> internalAcceptedLocalOperations = APIUtil.toInternal(acceptedLocalOperations);
		final Set<AbstractOperation> internalRejectedRemoteOperations = APIUtil.toInternal(rejectedRemoteOperations);
		toInternalAPI().resolveConflict(internalAcceptedLocalOperations,
			internalRejectedRemoteOperations);
	}

}
