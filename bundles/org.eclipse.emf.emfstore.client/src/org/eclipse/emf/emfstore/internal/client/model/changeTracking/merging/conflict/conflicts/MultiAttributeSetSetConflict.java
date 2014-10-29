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

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictDescription;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption.OptionType;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;

public class MultiAttributeSetSetConflict extends VisualConflict {

	public MultiAttributeSetSetConflict(ConflictBucket conflictBucket, DecisionManager decisionManager) {
		super(conflictBucket, decisionManager);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.VisualConflict.dialogs.merge.conflict.Conflict#initConflictDescription()
	 */
	@Override
	protected ConflictDescription initConflictDescription(ConflictDescription description) {

		description.setDescription(DecisionUtil.getDescription("multiattributesetsetconflict", getDecisionManager() //$NON-NLS-1$
			.isBranchMerge()));

		return description;
	}

	@Override
	protected void initConflictOptions(List<ConflictOption> options) {
		final ConflictOption myOption = new ConflictOption(StringUtils.EMPTY, OptionType.MyOperation);
		myOption.addOperations(getMyOperations());
		final ConflictOption theirOption = new ConflictOption(StringUtils.EMPTY, OptionType.TheirOperation);
		theirOption.addOperations(getTheirOperations());

		myOption.setOptionLabel(Messages.MultiAttributeSetSetConflict_KeepMy);
		theirOption.setOptionLabel(Messages.MultiAttributeSetSetConflict_KeepTheir);

		options.add(myOption);
		options.add(theirOption);
	}

}