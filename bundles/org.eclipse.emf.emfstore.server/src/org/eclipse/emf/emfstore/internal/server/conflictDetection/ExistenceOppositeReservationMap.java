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
public class ExistenceOppositeReservationMap extends OppositeReservationMap {

	private static final long serialVersionUID = 6226587689609391089L;

	private final Set<ConflictBucketCandidate> candidates;

	/**
	 * Default constructor that assumes that there are no opposites.
	 */
	public ExistenceOppositeReservationMap() {
		super(false);
		candidates = new LinkedHashSet<ConflictBucketCandidate>();
	}

	@Override
	public Set<ConflictBucketCandidate> getAllConflictBucketCandidates() {
		return candidates;
	}

	/**
	 * Adds a {@link ConflictBucketCandidate}.
	 * 
	 * @param conflictBucketCandidate
	 *            the {@link ConflictBucketCandidate} to be added
	 */
	public void addConflictBucketCandidate(ConflictBucketCandidate conflictBucketCandidate) {
		if (conflictBucketCandidate == null) {
			return;
		}
		candidates.add(conflictBucketCandidate);
	}

	@Override
	public void setConflictBucketCandidate(ConflictBucketCandidate conflictBucketCandidate) {
		super.setConflictBucketCandidate(conflictBucketCandidate);
		addConflictBucketCandidate(conflictBucketCandidate);
	}

}
