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
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.CancelOperationException;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.BranchSelectionDialog;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.merge.MergeProjectHandler;
import org.eclipse.emf.emfstore.internal.server.model.versioning.BranchInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * UIController used to merge other branches into the current projectspace.
 *
 * @author wesendon
 */
public class UIMergeController extends AbstractEMFStoreUIController<Void> {

	private final ProjectSpace projectSpace;

	/**
	 * Default constructor.
	 *
	 * @param shell
	 *            active shell
	 * @param localProject
	 *            the project being merged
	 */
	public UIMergeController(Shell shell, ESLocalProject localProject) {
		super(shell);
		projectSpace = ((ESLocalProjectImpl) localProject)
			.toInternalAPI();
	}

	@Override
	public Void doRun(IProgressMonitor monitor) throws ESException {
		if (!projectSpace.changePackage().isEmpty()) {
			MessageDialog
			.openError(
				getShell(),
				Messages.UIMergeController_MergeNotApplicable_Title,
				Messages.UIMergeController_MergeNotApplicable_Message);
			return null;
		}

		final PrimaryVersionSpec selectedVersionSpec = branchSelection(projectSpace);

		if (selectedVersionSpec != null) {
			projectSpace.mergeBranch(selectedVersionSpec,
				new MergeProjectHandler(true), monitor);
		}
		return null;
	}

	private PrimaryVersionSpec branchSelection(final ProjectSpace projectSpace)
		throws ESException {

		// OTS: progress monitor
		final List<BranchInfo> branches = projectSpace.getBranches();
		final ListIterator<BranchInfo> iterator = branches.listIterator();
		while (iterator.hasNext()) {
			final BranchInfo current = iterator.next();
			if (current.getName().equals(
				projectSpace.getBaseVersion().getBranch())) {
				iterator.remove();
			}
		}

		final BranchInfo result = RunInUI.WithException
			.runWithResult(new Callable<BranchInfo>() {
				public BranchInfo call() throws Exception {

					final BranchSelectionDialog dialog = new BranchSelectionDialog(
						getShell(),
						branches);
					dialog.setBlockOnOpen(true);

					if (dialog.open() != Window.OK
						|| dialog.getResult() == null) {
						throw new CancelOperationException(
							Messages.UIMergeController_NoBranchGiven);
					}
					return dialog.getResult();

				}
			});
		return result.getHead();
	}
}
