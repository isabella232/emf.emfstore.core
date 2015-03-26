/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.internal.client.model.controller.RevertCommitController;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.impl.api.versionspec.ESPrimaryVersionSpecImpl;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * UI controller for reverting a commit.
 *
 * @author emueller
 * @author wesendon
 *
 */
public class UIRevertCommitController extends AbstractEMFStoreUIController<Void> {

	private final ESPrimaryVersionSpecImpl versionSpec;
	private final ESLocalProjectImpl localProject;

	/**
	 * Constructor.
	 *
	 * @param shell
	 *            the shell that is used during the revert
	 * @param versionSpec
	 *            {@link org.eclipse.emf.emfstore.internal.server.model.versioning.VersionSpec VersionSpec} of the
	 *            commit to be reverted
	 * @param localProject
	 *            project that contains the version to be reverted
	 */
	public UIRevertCommitController(Shell shell, ESPrimaryVersionSpec versionSpec, ESLocalProject localProject) {
		super(shell, false, false);
		this.localProject = (ESLocalProjectImpl) localProject;
		this.versionSpec = (ESPrimaryVersionSpecImpl) versionSpec;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor monitor) throws ESException {

		final InputDialog dialog = new InputDialog(null,
			Messages.UIRevertCommitController_Confirmation,
			MessageFormat.format(
				Messages.UIRevertCommitController_RevertCommitPrompt_1
					+ Messages.UIRevertCommitController_RevertCommitPrompt_2,
				localProject.getProjectName()),
			createDefaultProjectName(localProject),
			new IInputValidator() {
				public String isValid(String newText) {
					if (StringUtils.isNotBlank(newText)) {
						return null;
					}
					return Messages.UIRevertCommitController_InvalidProjectName;
				}
			});

		if (dialog.open() == Window.OK) {

			final PrimaryVersionSpec primaryVersionSpec = ModelUtil.clone(versionSpec.toInternalAPI());
			final String checkoutName = dialog.getValue();

			try {
				new RevertCommitController(localProject.toInternalAPI(),
					primaryVersionSpec,
					true,
					checkoutName).execute();
			} catch (final ESException e) {
				MessageDialog.openWarning(getShell(),
					Messages.UIRevertCommitController_RevertFailed_Title,
					MessageFormat.format(Messages.UIRevertCommitController_RevertFailed_Message, e.getMessage()));
			}
		}
		return null;
	}

	private String createDefaultProjectName(ESLocalProject project) {
		final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		return project.getProjectName() + "@" + format.format(new Date()); //$NON-NLS-1$
	}

}
