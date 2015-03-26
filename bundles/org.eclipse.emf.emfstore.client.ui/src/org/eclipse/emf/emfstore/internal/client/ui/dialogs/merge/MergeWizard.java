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
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge;

import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.util.UIDecisionUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

/**
 * Container of the Merge Dialog.
 *
 * @author wesendon
 */
public class MergeWizard extends Wizard {

	private final DecisionManager decisionManager;

	/**
	 * Default constructor.
	 *
	 * @param decisionManager
	 *            decisionManager
	 */
	public MergeWizard(DecisionManager decisionManager) {
		super();
		setWindowTitle(Messages.MergeWizard_Title);
		setDefaultPageImageDescriptor(UIDecisionUtil.getImageDescriptor("merge_wizard2.gif")); //$NON-NLS-1$

		this.decisionManager = decisionManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(new MergeWizardPage(decisionManager));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		if (decisionManager.isResolved()) {
			return true;
		}

		MessageDialog.openInformation(getShell(),
			Messages.MergeWizard_ResolveConflicts_Title,
			Messages.MergeWizard_ResolveConflicts_Message_1
				+ Messages.MergeWizard_ResolveConflicts_Message_2);
		return false;
	}
}
