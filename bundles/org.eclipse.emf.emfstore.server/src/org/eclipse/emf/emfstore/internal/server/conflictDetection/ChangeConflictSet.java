/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 * Maximilian Koegel - Conflict Detection refactorings
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.api.APIDelegate;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.server.impl.api.ESConflictSetImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESConflictSet;

/**
 * The actual implementation of an {@link ESConflictSetImpl} containing
 * the changes that caused the conflict.
 * 
 * @author wesendon
 * @author emueller
 * @author mkoegel
 */
public class ChangeConflictSet implements APIDelegate<ESConflictSet> {

	private final ModelElementIdToEObjectMapping idToEObjectMapping;
	private ESConflictSetImpl apiImpl;
	private final Set<ConflictBucket> conflictBuckets;
	private final Set<AbstractOperation> notInvolvedInConflict;
	private final List<ChangePackage> leftChanges;
	private final List<ChangePackage> rightChanges;

	/**
	 * Constructor.
	 * 
	 * @param conflictBuckets
	 *            a set of conflict candidates
	 * @param notInvolvedInConflict
	 *            a set of operations not involved in any conflict
	 * @param idToEObjectMapping
	 *            a mapping from IDs to EObjects and vice versa.<br/>
	 *            Contains all IDs of model elements involved in the {@link ChangePackage}s
	 *            as well as those contained by the project in the ProjectSpace
	 * @param leftChanges
	 *            a list of {@link ChangePackage}s representing one side of the conflict
	 * @param rightChanges
	 *            a list of {@link ChangePackage}s representing the other side of the conflict
	 */
	public ChangeConflictSet(Set<ConflictBucket> conflictBuckets, Set<AbstractOperation> notInvolvedInConflict,
		ModelElementIdToEObjectMapping idToEObjectMapping, List<ChangePackage> leftChanges,
		List<ChangePackage> rightChanges) {

		this.conflictBuckets = conflictBuckets;
		this.notInvolvedInConflict = notInvolvedInConflict;
		this.idToEObjectMapping = idToEObjectMapping;
		this.leftChanges = leftChanges;
		this.rightChanges = rightChanges;
	}

	/**
	 * Returns the mapping from IDs to EObjects and vice versa.<br/>
	 * The mapping contains all IDs of model elements involved in the {@link ChangePackage}s
	 * as well as those contained by the project in the ProjectSpace
	 * 
	 * @return the mapping from IDs to EObjects and vice versa
	 */
	public ModelElementIdToEObjectMapping getIdToEObjectMapping() {
		return idToEObjectMapping;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.common.api.APIDelegate#toAPI()
	 */
	public ESConflictSet toAPI() {
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
	public ESConflictSetImpl createAPI() {
		return new ESConflictSetImpl(this);
	}

	/**
	 * Returns a set of operations not involved in any conflict.
	 * 
	 * @return a set of operations not involved in any conflict
	 */
	public Set<AbstractOperation> getNotInvolvedInConflict() {
		return notInvolvedInConflict;
	}

	/**
	 * Returns a set of conflict candidates.
	 * 
	 * @return a set of conflict candidates
	 */
	public Set<ConflictBucket> getConflictBuckets() {
		return conflictBuckets;
	}

	/**
	 * Returns a list of {@link ChangePackage}s representing one side of the conflict.
	 * 
	 * @return a list of {@link ChangePackage}s representing one side of the conflict
	 */
	public List<ChangePackage> getLeftChanges() {
		return leftChanges;
	}

	/**
	 * Returns a list of {@link ChangePackage}s representing the other side of the conflict.
	 * 
	 * @return a list of {@link ChangePackage}s representing the other side of the conflict
	 */
	public List<ChangePackage> getRightChanges() {
		return rightChanges;
	}
}