/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts;

// BEGIN COMPLEX CODE
//
// WORK IN PROGRESS !
//

import java.util.List;

import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictDescription;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;

public class MultiAttributeConflict extends VisualConflict {

	public MultiAttributeConflict(ConflictBucket conflictBucket, DecisionManager decisionManager,
		boolean myAdd) {
		super(conflictBucket, decisionManager, myAdd, true);
	}

	/**
	 * LEFT: ADDING RIGHT: REMOVING
	 */

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.VisualConflict.dialogs.merge.conflict.Conflict#initConflictDescription()
	 */
	@Override
	protected ConflictDescription initConflictDescription(ConflictDescription description) {
		if (isLeftMy()) {
			description.setDescription(DecisionUtil.getDescription("multiattributeconflict.my", getDecisionManager() //$NON-NLS-1$
				.isBranchMerge()));
		} else {
			description.setDescription(DecisionUtil.getDescription("multiattributeconflict.their", getDecisionManager() //$NON-NLS-1$
				.isBranchMerge()));
		}
		description.setImage("attribute.gif"); //$NON-NLS-1$
		return description;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.VisualConflict.dialogs.merge.conflict.Conflict#initConflictOptions(java.util.List)
	 */
	@Override
	protected void initConflictOptions(List<ConflictOption> options) {
		final ConflictOption my = new ConflictOption(getLabel(true) + " " //$NON-NLS-1$
			+ getMyOperation(MultiAttributeOperation.class).getReferencedValues().get(0), OptionType.MyOperation);
		my.addOperations(getMyOperations());

		final ConflictOption their = new ConflictOption(getLabel(false) + " " //$NON-NLS-1$
			+ getTheirOperation(MultiAttributeOperation.class).getReferencedValues().get(0), OptionType.TheirOperation);
		their.addOperations(getTheirOperations());

		options.add(my);
		options.add(their);
	}

	/**
	 * TODO adjust label
	 * 
	 * @param you
	 * @return
	 */
	private String getLabel(boolean you) {
		return (isLeftMy() && you || !isLeftMy() && !you ? Messages.MultiAttributeConflict_Add
			: Messages.MultiAttributeConflict_Remove) + " "; //$NON-NLS-1$
	}
}