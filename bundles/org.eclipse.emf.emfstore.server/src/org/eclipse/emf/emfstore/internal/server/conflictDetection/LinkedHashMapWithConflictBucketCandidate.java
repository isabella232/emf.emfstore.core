/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * {@link LinkedHashMap} with an additional {@link ConflictBucketCandidate} field. The Key is a String.
 * 
 * @param <V> the value type of the map
 */
public abstract class LinkedHashMapWithConflictBucketCandidate<V> extends LinkedHashMap<String, V> {

	private static final long serialVersionUID = -6885405442204079582L;
	private ConflictBucketCandidate conflictBucketCandidate;

	/**
	 * Returns the {@link ConflictBucketCandidate}.
	 * 
	 * @return the candidate
	 */
	public ConflictBucketCandidate getConflictBucketCandidate() {
		return this.conflictBucketCandidate;
	}

	/**
	 * Sets the {@link ConflictBucketCandidate} for this map.
	 * 
	 * @param conflictBucketCandidate the candidate to set
	 */
	public void setConflictBucketCandidate(ConflictBucketCandidate conflictBucketCandidate) {
		this.conflictBucketCandidate = conflictBucketCandidate;
	}

	/**
	 * Returns all {@link ConflictBucketCandidate}s.
	 * 
	 * @return a set of candidates.
	 */
	public abstract Set<ConflictBucketCandidate> getAllConflictBucketCandidates();
}
