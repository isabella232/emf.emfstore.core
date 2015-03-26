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

/**
 * Stores an {@link ConflictBucketCandidate}.
 *
 * @author mkoegel
 */
public interface ConflictBucketCandidateStore {

	/**
	 * Returns the contained {@link ConflictBucketCandidate}.
	 *
	 * @return the {@link ConflictBucketCandidate}.
	 */
	ConflictBucketCandidate getConflictBucketCandidate();

	/**
	 * Sets the contained {@link ConflictBucketCandidate}.
	 *
	 * @param conflictBucketCandidate
	 *            the {@link ConflictBucketCandidate} to be set
	 */
	void setConflictBucketCandidate(ConflictBucketCandidate conflictBucketCandidate);
}
