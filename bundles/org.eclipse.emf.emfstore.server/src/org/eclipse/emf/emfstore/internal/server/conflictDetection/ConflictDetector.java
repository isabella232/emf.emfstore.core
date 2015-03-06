/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * Detects conflicts between operation.
 *
 * @author koegel
 */
public class ConflictDetector {

	/**
	 * Constructor.
	 */
	public ConflictDetector() {
	}

	/**
	 * Calculates a {@link ChangeConflictSet} based on opposing {@link AbstractChangePackage}s.
	 *
	 * @param myChangePackages
	 *            a list of {@link AbstractChangePackage}s
	 * @param theirChangePackages
	 *            a other list of {@link AbstractChangePackage}s
	 * @param project
	 *            the project for which calculate conflicts
	 * @return a {@link ChangeConflictSet}
	 */
	public ChangeConflictSet calculateConflicts(List<AbstractChangePackage> myChangePackages,
		List<AbstractChangePackage> theirChangePackages, Project project) {

		final ModelElementIdToEObjectMappingImpl idToEObjectMappingImpl = new ModelElementIdToEObjectMappingImpl(
			project,
			myChangePackages);
		idToEObjectMappingImpl.put(theirChangePackages);
		return calculateConflicts(myChangePackages, theirChangePackages, idToEObjectMappingImpl);
	}

	/**
	 * Calculates a {@link ChangeConflictSet} based on opposing {@link AbstractChangePackage}s.
	 *
	 * @param myChangePackages
	 *            a list of {@link AbstractChangePackage}s
	 * @param theirChangePackages
	 *            a other list of {@link AbstractChangePackage}s
	 * @param idToEObjectMapping
	 *            a mapping that is used to resolve model elements while calculating conflicts
	 * @return a {@link ChangeConflictSet}
	 */
	public ChangeConflictSet calculateConflicts(List<AbstractChangePackage> myChangePackages,
		List<AbstractChangePackage> theirChangePackages, ModelElementIdToEObjectMapping idToEObjectMapping) {

		final Set<ConflictBucketCandidate> conflictCandidateBuckets = calculateConflictCandidateBuckets(
			myChangePackages,
			theirChangePackages, idToEObjectMapping);
		final Set<AbstractOperation> notInvolvedInConflict = new LinkedHashSet<AbstractOperation>();
		final Set<ConflictBucket> conflictBuckets = calculateConflictBucketsFromConflictCandidateBuckets(
			conflictCandidateBuckets, notInvolvedInConflict);
		return new ChangeConflictSet(conflictBuckets, notInvolvedInConflict, idToEObjectMapping, myChangePackages,
			theirChangePackages);
	}

	/**
	 * Calculate a set of conflict candidate buckets from a list of my and their change packages.
	 *
	 * @param myChangePackages their operations in a list of change packages
	 * @param theirChangePackages their operations in a list of change packages
	 * @return a set of buckets with potentially conflicting operations
	 */
	private Set<ConflictBucketCandidate> calculateConflictCandidateBuckets(
		List<AbstractChangePackage> myChangePackages,
		List<AbstractChangePackage> theirChangePackages, ModelElementIdToEObjectMapping idToEObjectMapping) {

		final Iterable<AbstractOperation> myOperations = flattenChangepackages(myChangePackages);
		final Iterable<AbstractOperation> theirOperations = flattenChangepackages(theirChangePackages);

		return calculateConflictCandidateBuckets(idToEObjectMapping, myOperations, theirOperations);
	}

	private Set<ConflictBucketCandidate> calculateConflictCandidateBuckets(
		ModelElementIdToEObjectMapping idToEObjectMapping,
		Iterable<AbstractOperation> myOperations,
		Iterable<AbstractOperation> theirOperations) {

		final ReservationToConflictBucketCandidateMap conflictMap = new ReservationToConflictBucketCandidateMap();

		int counter = 0;
		for (final AbstractOperation myOperation : myOperations) {
			conflictMap.scanOperationReservations(myOperation, counter, idToEObjectMapping, true);
			counter++;
		}

		for (final AbstractOperation theirOperation : theirOperations) {
			conflictMap.scanOperationReservations(theirOperation, counter, idToEObjectMapping, false);
			counter++;
		}
		return conflictMap.getConflictBucketCandidates();
	}

	// TODO: LCP, List return type
	private List<AbstractOperation> flattenChangepackages(List<AbstractChangePackage> changePackages) {
		final List<AbstractOperation> operations = new ArrayList<AbstractOperation>();
		for (final AbstractChangePackage changePackage : changePackages) {
			final ESCloseableIterable<AbstractOperation> changePackageOperations = changePackage.operations();
			try {
				for (final AbstractOperation operation : changePackageOperations.iterable()) {
					operations.add(operation);
				}
			} finally {
				changePackageOperations.close();
			}
		}
		return operations;
	}

	/**
	 * Calculate a set of conflict buckets from an existing set of conflict candidate buckets. the resulting set may be
	 * empty.
	 *
	 * @param conflictBucketsCandidateSet a set of conflict candidate buckets
	 * @param notInvolvedInConflict all my operations that are not involved in a conflict are collected in this
	 *            transient parameter set
	 * @return a set of conflict buckets
	 */
	private Set<ConflictBucket> calculateConflictBucketsFromConflictCandidateBuckets(
		Set<ConflictBucketCandidate> conflictBucketsCandidateSet, Set<AbstractOperation> notInvolvedInConflict) {

		final Set<ConflictBucket> conflictBucketsSet = new LinkedHashSet<ConflictBucket>();
		for (final ConflictBucketCandidate conflictBucketCandidate : conflictBucketsCandidateSet) {
			final Set<ConflictBucket> buckets = conflictBucketCandidate.calculateConflictBuckets(this,
				notInvolvedInConflict);
			for (final ConflictBucket bucket : buckets) {
				conflictBucketsSet.add(bucket);
			}
		}
		return conflictBucketsSet;
	}
}