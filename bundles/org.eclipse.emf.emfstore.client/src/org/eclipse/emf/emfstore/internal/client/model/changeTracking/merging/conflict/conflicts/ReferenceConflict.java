/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk - initial API and implementation
 * Maximilian Koegel, Edgar Mueller - bugfix 421361
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts;

import java.util.List;

import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictContext;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictDescription;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;

/**
 * Container for {@link MultiReferenceConflict} and {@link SingleReferenceConflict}.
 * 
 * @author wesendon
 */
public class ReferenceConflict extends VisualConflict {

	private final VisualConflict conflict;

	/*
	 * Applies to --
	 * left: CS, right: CS
	 * left: CS, right: S
	 * left: S, right: CS
	 */
	/**
	 * Constructor for a reference conflict where two {@link SingleReferenceOperation} conflict with each other. This
	 * may be the case, if:
	 * <ul>
	 * <li>the left and rights operations are main operations of conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s</li>
	 * <li>the left operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the right operation is a stand-alone {@link SingleReferenceOperation}</li>
	 * <li>the right operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the left operation is a stand-alone {@link SingleReferenceOperation}</li>
	 * </ul>
	 * 
	 * @param leftSingleRef
	 *            a {@link SingleReferenceOperation}
	 * @param rightSingleRef
	 *            the {@link SingleReferenceOperation} conflicting with <code>leftSingleRef</code>
	 * @param conflictBucket
	 *            the conflict bucket
	 * @param decisionManager
	 *            the decision manager
	 */
	public ReferenceConflict(SingleReferenceOperation leftSingleRef,
		SingleReferenceOperation rightSingleRef, ConflictBucket conflictBucket,
		DecisionManager decisionManager) {
		super(conflictBucket, leftSingleRef, rightSingleRef, decisionManager, true, false);
		conflict = new SingleReferenceConflict(conflictBucket, leftSingleRef,
			rightSingleRef, decisionManager);
		init();
	}

	// left: CM, right: CM
	// left: CM, right: M
	// left: M, right: CM
	/**
	 * Constructor for a reference conflict where two {@link MultiReferenceOperation} conflict with each other. This
	 * may be the case, if:
	 * <ul>
	 * <li>the left and rights operations are main operations of conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s</li>
	 * <li>the left operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the right operation is a stand-alone {@link MultiReferenceOperation}</li>
	 * <li>the right operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the left operation is a stand-alone {@link MultiReferenceOperation}</li>
	 * </ul>
	 * 
	 * @param leftMultiRef
	 *            a {@link MultiReferenceOperation}
	 * @param rightMultiRef
	 *            the {@link MultiReferenceOperation} conflicting with <code>leftMultiRef</code>
	 * @param conflictBucket
	 *            the conflict bucket
	 * @param decisionManager
	 *            the decision manager
	 */
	public ReferenceConflict(MultiReferenceOperation leftMultiRef,
		MultiReferenceOperation rightMultiRef, ConflictBucket conflictBucket,
		DecisionManager decisionManager) {
		super(conflictBucket, leftMultiRef, rightMultiRef, decisionManager, true, false);
		conflict = new MultiReferenceConflict(conflictBucket, leftMultiRef, rightMultiRef, decisionManager, true);
		init();
	}

	// left: CM, right: CS
	// left: CM, right: S
	// left: M, right: CS
	/**
	 * Constructor for a reference conflict where a {@link MultiReferenceOperation} conflicts with a
	 * {@link SingleReferenceOperation}. This may be the case, if:
	 * <ul>
	 * <li>the left and rights operations are main operations of conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s</li>
	 * <li>the left operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the right operation is a stand-alone {@link SingleReferenceOperation}</li>
	 * <li>the right operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the left operation is a stand-alone {@link MultiReferenceOperation}</li>
	 * </ul>
	 * 
	 * @param leftMultiRef
	 *            a {@link SingleReferenceOperation}
	 * @param rightSingleRef
	 *            the {@link SingleReferenceOperation} conflicting with <code>leftSingleRef</code>
	 * @param conflictBucket
	 *            the conflict bucket
	 * @param decisionManager
	 *            the decision manager
	 */
	public ReferenceConflict(MultiReferenceOperation leftMultiRef,
		SingleReferenceOperation rightSingleRef, ConflictBucket conflictBucket,
		DecisionManager decisionManager) {
		super(conflictBucket, leftMultiRef, rightSingleRef, decisionManager, true, false);
		conflict = new MultiReferenceSingleConflict(leftMultiRef,
			rightSingleRef, conflictBucket, decisionManager);
		init();
	}

	// left: CS, right: CM
	// left: S, right: CM
	// left: CS, right: M
	/**
	 * Constructor for a reference conflict where a {@link SingleReferenceOperation} conflicts with a
	 * {@link MultiReferenceOperation}. This may be the case, if:
	 * <ul>
	 * <li>the left and rights operations are main operations of conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s</li>
	 * <li>the right operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the left operation is a stand-alone {@link SingleReferenceOperation}</li>
	 * <li>the left operation is the main operation of a conflicting
	 * {@link org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation
	 * CompositeOperation}s while the right operation is a stand-alone {@link MultiReferenceOperation}</li>
	 * </ul>
	 * 
	 * @param leftSingleRef
	 *            a {@link SingleReferenceOperation}
	 * @param rightMultiRef
	 *            the {@link SingleReferenceOperation} conflicting with <code>leftSingleRef</code>
	 * @param conflictBucket
	 *            the conflict bucket
	 * @param decisionManager
	 *            the decision manager
	 */
	public ReferenceConflict(SingleReferenceOperation leftSingleRef,
		MultiReferenceOperation rightMultiRef, ConflictBucket conflictBucket,
		DecisionManager decisionManager) {
		super(conflictBucket, leftSingleRef, rightMultiRef, decisionManager, true, false);
		conflict = new MultiReferenceSingleConflict(rightMultiRef,
			leftSingleRef, conflictBucket, decisionManager);
		init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ConflictContext initConflictContext() {
		return conflict.getConflictContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ConflictDescription initConflictDescription(ConflictDescription desc) {
		return conflict.getConflictDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initConflictOptions(List<ConflictOption> options) {
		for (final ConflictOption option : conflict.getOptions()) {
			if (option.getType() == OptionType.MyOperation) {
				option.getOperations().clear();
				option.addOperations(getConflictBucket().getMyOperations());
			} else if (option.getType() == OptionType.TheirOperation) {
				option.getOperations().clear();
				option.addOperations(getConflictBucket().getTheirOperations());
			}
			options.add(option);
		}
	}

}
