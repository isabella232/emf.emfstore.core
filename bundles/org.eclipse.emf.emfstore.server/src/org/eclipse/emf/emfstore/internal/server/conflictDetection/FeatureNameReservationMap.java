/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tracks any reservation based an a feature name.
 *
 * @author mkoegel
 * @author emueller
 */
public class FeatureNameReservationMap extends LinkedHashMapWithConflictBucketCandidate<OppositeReservationMap> {

	private static final long serialVersionUID = -908068067539831820L;

	/**
	 * The name of the existence feature.
	 */
	public static final String EXISTENCE_FEATURE = "+existence"; //$NON-NLS-1$

	/**
	 * The name of the container feature.
	 */
	public static final String CONTAINER_FEATURE = "+container"; //$NON-NLS-1$

	private final boolean isAllFeatures;

	/**
	 * Constructor.
	 *
	 * @param isAllFeatures
	 *            whether to make a reservation for all feature
	 */
	public FeatureNameReservationMap(boolean isAllFeatures) {
		this.isAllFeatures = isAllFeatures;
	}

	/**
	 * Default constructor that does not make a reservation for all feature.
	 *
	 */
	public FeatureNameReservationMap() {
		this(false);
	}

	@Override
	public Set<ConflictBucketCandidate> getAllConflictBucketCandidates() {
		final Set<ConflictBucketCandidate> result = new LinkedHashSet<ConflictBucketCandidate>();
		if (isAllFeatures) {
			if (getConflictBucketCandidate() != null) {
				result.add(getConflictBucketCandidate());
			}
			return result;
		}

		for (final String featureName : keySet()) {
			result.addAll(get(featureName).getAllConflictBucketCandidates());
		}
		return result;
	}

	/**
	 * Whether this reservation map holds a reservation for an existence feature.
	 *
	 * @return <code>true</code>, if this reservation holds a reservation for an existence feature, <code>false</code>
	 *         otherwise
	 */
	public boolean hasExistenceFeature() {
		return containsKey(EXISTENCE_FEATURE);
	}

	/**
	 * Whether this reservation map has a reservation for all features.
	 *
	 * @return <code>true</code>, if this reservation map has a reservation for all features <code>false</code>
	 *         otherwise
	 */
	public boolean isAllFeatures() {
		return isAllFeatures;
	}

	/**
	 * Returns the {@link ConflictBucketCandidate}s for a given feature name.
	 *
	 * @param featureName
	 *            a feature name
	 * @return a set of {@link ConflictBucketCandidate}s for the given feature name
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String featureName) {
		final Set<ConflictBucketCandidate> result = new LinkedHashSet<ConflictBucketCandidate>();
		if (isAllFeatures()) {
			result.add(getConflictBucketCandidate());
			return result;
		}
		final OppositeReservationMap oppositeReservationMap = get(featureName);
		if (oppositeReservationMap == null) {
			return result;
		}
		return oppositeReservationMap.getAllConflictBucketCandidates();
	}

	/**
	 * Returns the {@link ConflictBucketCandidate}s for the combination of a feature name and an opposite.
	 *
	 * @param featureName
	 *            a feature name
	 * @param oppositeModelElement
	 *            an additional opposite model element
	 * @return a set of {@link ConflictBucketCandidate}s
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String featureName, String oppositeModelElement) {
		final Set<ConflictBucketCandidate> result = new LinkedHashSet<ConflictBucketCandidate>();
		if (isAllFeatures()) {
			result.add(getConflictBucketCandidate());
			return result;
		}
		final OppositeReservationMap oppositeReservationMap = get(featureName);
		if (oppositeReservationMap == null) {
			return result;
		}
		return oppositeReservationMap.getConflictBucketCandidates(oppositeModelElement);
	}
}
