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
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.internal.server.impl.api.ESConflictImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESConflict;

/**
 * Represents a bucket of conflicting operations sets. In this context my operations are operations authored/owned by
 * the current user while their operation are incoming operations from another user.
 *
 * @author koegel
 */
public class ConflictBucket implements APIDelegate<ESConflict> {

	private final Set<AbstractOperation> myOperations;
	private final Set<AbstractOperation> theirOperations;
	private AbstractOperation myOperation;
	private AbstractOperation theirOperation;
	private ESConflict apiImpl;
	private Set<AbstractOperation> acceptedLocalOperations;
	private boolean isResolved;
	private Set<AbstractOperation> rejectedRemoteOperations;

	/**
	 * Constructor.
	 *
	 * @param myOperation initial my operation
	 * @param theirOperation initial their operation
	 */
	public ConflictBucket(AbstractOperation myOperation, AbstractOperation theirOperation) {
		myOperations = new LinkedHashSet<AbstractOperation>();
		myOperations.add(myOperation);
		theirOperations = new LinkedHashSet<AbstractOperation>();
		theirOperations.add(theirOperation);
	}

	/**
	 * Constructor.
	 *
	 * @param myOperations initial set of my operations
	 * @param theirOperations initial set of their operations
	 */
	public ConflictBucket(Set<AbstractOperation> myOperations, Set<AbstractOperation> theirOperations) {
		this.myOperations = myOperations;
		this.theirOperations = theirOperations;
	}

	/**
	 * @return the set of my operations
	 */
	public Set<AbstractOperation> getMyOperations() {
		return myOperations;
	}

	/**
	 * @return the set of their operations
	 */
	public Set<AbstractOperation> getTheirOperations() {
		return theirOperations;
	}

	/**
	 * @return one of my operations representing all my operations
	 */
	public AbstractOperation getMyOperation() {
		return myOperation;
	}

	/**
	 * Set one of my operations representing all my operations.
	 *
	 * @param myOperation the operation
	 */
	public void setMyOperation(AbstractOperation myOperation) {
		this.myOperation = myOperation;
	}

	/**
	 * @return one of their operations representing all their operations
	 */
	public AbstractOperation getTheirOperation() {
		return theirOperation;
	}

	/**
	 * Set one of their operations representing all their operations.
	 * * @param theirOperation the operation
	 */
	public void setTheirOperation(AbstractOperation theirOperation) {
		this.theirOperation = theirOperation;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public ESConflict toAPI() {
		if (apiImpl == null) {
			apiImpl = createAPI();
		}
		return apiImpl;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#createAPI()
	 */
	public ESConflict createAPI() {
		return new ESConflictImpl(this);
	}

	/**
	 * Resolve the conflict by specifying the accepted local operations and the rejected
	 * remote operations.
	 *
	 * @param acceptedLocalOperations
	 *            a set of local operations that have been accepted
	 * @param rejectedRemoteOperations
	 *            a set of remote operations that have been rejected
	 */
	public void resolveConflict(Set<AbstractOperation> acceptedLocalOperations,
		Set<AbstractOperation> rejectedRemoteOperations) {
		this.acceptedLocalOperations = acceptedLocalOperations;
		this.rejectedRemoteOperations = rejectedRemoteOperations;
		isResolved = true;
	}

	/**
	 * Returns the set of local operations that have been accepted.
	 *
	 * @return a set of accepted local operations
	 */
	public Set<AbstractOperation> getAcceptedLocalOperations() {
		return acceptedLocalOperations;
	}

	/**
	 * Returns the set of remote operations that have been rejected.
	 *
	 * @return the set of rejected remote operations
	 */
	public Set<AbstractOperation> getRejectedRemoteOperations() {
		return rejectedRemoteOperations;
	}

	/**
	 * Whether this conflict bucket is resolved.
	 *
	 * @return <code>true</code> if this bucket is resolved, <code>false</code> otherwise
	 */
	public boolean isResolved() {
		return isResolved;
	}
}