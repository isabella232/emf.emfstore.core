/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictDescription;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;

public class MultiAttributeSetConflict extends VisualConflict {

	public MultiAttributeSetConflict(ConflictBucket conflictBucket, DecisionManager decisionManager,
		boolean isMyRemove) {
		super(conflictBucket, decisionManager, isMyRemove, true);
	}

	/**
	 * LEFT: Remove, RIGHT: set
	 */

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.VisualConflict.dialogs.merge.conflict.Conflict#initConflictDescription()
	 */
	@Override
	protected ConflictDescription initConflictDescription(ConflictDescription description) {

		if (isLeftMy()) {
			description.setDescription(
				DecisionUtil.getDescription("multiattributesetconflict.my", //$NON-NLS-1$
					getDecisionManager().isBranchMerge()));

		} else {
			description.setDescription(
				DecisionUtil.getDescription("multiattributesetconflict.their", //$NON-NLS-1$
					getDecisionManager().isBranchMerge()));
		}

		return description;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.VisualConflict.dialogs.merge.conflict.Conflict#initConflictOptions(java.util.List)
	 */
	@Override
	protected void initConflictOptions(List<ConflictOption> options) {
		final ConflictOption myOption = new ConflictOption(StringUtils.EMPTY, OptionType.MyOperation);
		myOption.addOperations(getMyOperations());
		final ConflictOption theirOption = new ConflictOption(StringUtils.EMPTY, OptionType.TheirOperation);
		theirOption.addOperations(getTheirOperations());

		if (isLeftMy()) {
			myOption.setOptionLabel(Messages.MultiAttributeSetConflict_RemoveElement);
			theirOption.setOptionLabel(Messages.MultiAttributeSetConflict_ChangeElement);
		} else {
			myOption.setOptionLabel(Messages.MultiAttributeSetConflict_ChangeElement);
			theirOption.setOptionLabel(Messages.MultiAttributeSetConflict_RemoveElement);
		}

		options.add(myOption);
		options.add(theirOption);
	}
}