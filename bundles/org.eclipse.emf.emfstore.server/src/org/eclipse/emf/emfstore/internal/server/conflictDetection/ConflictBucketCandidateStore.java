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

public interface ConflictBucketCandidateStore {

	ConflictBucketCandidate getConflictBucketCandidate();

	void setConflictBucketCandidate(ConflictBucketCandidate conflictBucketCandidate);
}
