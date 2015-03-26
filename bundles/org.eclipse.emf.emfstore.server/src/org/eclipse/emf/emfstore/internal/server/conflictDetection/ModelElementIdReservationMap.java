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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Tracks any reservation based on {@link org.eclipse.emf.emfstore.internal.common.model.ModelElementId ModelElementId}
 * s.
 *
 * @author mkoegel
 * @author emueller
 */
public class ModelElementIdReservationMap extends LinkedHashMap<String, FeatureNameReservationMap> {

	private static final long serialVersionUID = -7135830923364954134L;

	/**
	 * Returns a set of {@link ConflictBucketCandidate}s for a given ModelElementId.
	 *
	 * @param modelElementId
	 *            the ModelElementId for which to retrieve the {@link ConflictBucketCandidate}s
	 * @return a set of {@link ConflictBucketCandidate}s for the given ModelElementId
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String modelElementId) {
		final FeatureNameReservationMap featureNameReservationMap = get(modelElementId);
		if (featureNameReservationMap == null) {
			return new LinkedHashSet<ConflictBucketCandidate>();
		}
		return featureNameReservationMap.getAllConflictBucketCandidates();
	}

	/**
	 * Returns a set of {@link ConflictBucketCandidate}s for the combination
	 * of ModelElementId and a feature name.
	 *
	 * @param modelElementId
	 *            the ModelElementId for which to retrieve the {@link ConflictBucketCandidate}s
	 * @param featureName
	 *            a feature name
	 * @return a set of {@link ConflictBucketCandidate}s for the combination
	 *         of ModelElementId and a feature name.
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String modelElementId, String featureName) {
		final FeatureNameReservationMap featureNameReservationMap = get(modelElementId);

		final LinkedHashSet<ConflictBucketCandidate> result = new LinkedHashSet<ConflictBucketCandidate>();
		if (featureNameReservationMap == null) {
			return result;
		}

		return featureNameReservationMap.getConflictBucketCandidates(featureName);
	}

	/**
	 * Returns a set of {@link ConflictBucketCandidate}s for the combination
	 * of ModelElementId, a feature name and an opposite model element.
	 *
	 * @param modelElementId
	 *            the ModelElementId for which to retrieve the {@link ConflictBucketCandidate}s
	 * @param featureName
	 *            a feature name
	 * @param oppositeModelElement
	 *            an opposite model element
	 * @return a set of {@link ConflictBucketCandidate}s for the combination
	 *         of ModelElementId, a feature name and an opposite model element
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String modelElementId, String featureName,
		String oppositeModelElement) {
		final FeatureNameReservationMap featureNameReservationMap = get(modelElementId);

		final LinkedHashSet<ConflictBucketCandidate> result = new LinkedHashSet<ConflictBucketCandidate>();
		if (featureNameReservationMap == null) {
			return result;
		}

		return featureNameReservationMap.getConflictBucketCandidates(featureName, oppositeModelElement);
	}
}
