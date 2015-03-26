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
 * Tracks any reservation based an a opposite model element.
 *
 * @author mkoegel
 * @author emueller
 */
public class OppositeReservationMap extends LinkedHashMapWithConflictBucketCandidate<ConflictBucketCandidate> {

	private static final long serialVersionUID = 2688817784490600252L;
	private final boolean hasOpposites;

	/**
	 * Constructor.
	 *
	 * @param hasOpposites
	 *            whether this reservation map has opposites at all
	 */
	public OppositeReservationMap(boolean hasOpposites) {
		this.hasOpposites = hasOpposites;
	}

	@Override
	public Set<ConflictBucketCandidate> getAllConflictBucketCandidates() {
		final Set<ConflictBucketCandidate> candidates = new LinkedHashSet<ConflictBucketCandidate>();
		if (!hasOpposites) {
			if (getConflictBucketCandidate() != null) {
				candidates.add(getConflictBucketCandidate());
			}
			return candidates;
		}
		candidates.addAll(values());
		return candidates;
	}

	/**
	 * Whether this reservation map has opposites at all.
	 *
	 * @return <code>true</code> if this reservation map has opposites, <code>false</code> otherwise
	 */
	public boolean hasOpposites() {
		return hasOpposites;
	}

	/**
	 * Returns the {@link ConflictBucketCandidate}s for a given opposite.
	 *
	 * @param oppositeModelElement
	 *            the opposite model element
	 * @return a set of {@link ConflictBucketCandidate}s
	 */
	public Set<ConflictBucketCandidate> getConflictBucketCandidates(String oppositeModelElement) {
		final Set<ConflictBucketCandidate> candidates = new LinkedHashSet<ConflictBucketCandidate>();
		if (!hasOpposites) {
			candidates.add(getConflictBucketCandidate());
			return candidates;
		}

		final ConflictBucketCandidate matchingBucket = get(oppositeModelElement);
		candidates.add(matchingBucket);
		return candidates;
	}
}
