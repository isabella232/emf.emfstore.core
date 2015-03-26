/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swt.widgets.Shell;

/**
 * UI controller for deleting a local project.
 *
 * @author emueller
 */
public class UIDeleteProjectController extends AbstractEMFStoreUIController<Void> {

	private final ESLocalProject localProject;

	/**
	 * Constructor.
	 *
	 * @param shell
	 *            the shell that will be used during the deletion of the project
	 * @param localProject
	 *            the {@link ESLocalProject} containing the project that should be deleted
	 */
	public UIDeleteProjectController(Shell shell, ESLocalProject localProject) {
		super(shell);
		this.localProject = localProject;
	}

	private void deleteProject(final ESLocalProject localProject) {
		try {
			// TODO: pass monitor in & handle exceptions
			localProject.delete(new NullProgressMonitor());
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ESException e) {
			e.printStackTrace();
		}
	}

	private boolean confirmation(final ESLocalProject localProject) {
		String message = MessageFormat.format(
			Messages.UIDeleteProjectController_DeleteQuestion,
			localProject.getProjectName());

		if (localProject.isShared() && localProject.getBaseVersion() != null) {
			message += Messages.UIDeleteProjectController_InVersion + localProject.getBaseVersion().getIdentifier();
		}

		message += Messages.UIDeleteProjectController_QuestionMark;

		return confirm(Messages.UIDeleteProjectController_Confirmation, message);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor monitor) throws ESException {

		if (!confirmation(localProject)) {
			return null;
		}

		deleteProject(localProject);
		return null;
	}

}
