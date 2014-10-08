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

import java.text.MessageFormat;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.views.CreateProjectDialog;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * UI controller for creating a remote project.
 * 
 * @author emueller
 * 
 */
public class UICreateRemoteProjectController extends AbstractEMFStoreUIController<ESRemoteProject> {

	private final Usersession session;
	private final String projectName;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell} to be used during the creation of the remote project
	 */
	private UICreateRemoteProjectController(Shell shell) {
		super(shell, true, false);
		session = null;
		projectName = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell} to be used during the creation of the remote project
	 * @param session
	 *            the session to be used to create the project
	 */
	public UICreateRemoteProjectController(Shell shell, ESUsersession session) {
		super(shell);
		this.session = ((ESUsersessionImpl) session).toInternalAPI();
		projectName = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell} to be used during the creation of the remote project
	 * @param session
	 *            the session to be used to create the project
	 * @param projectName
	 *            the name of the project to be created
	 */
	public UICreateRemoteProjectController(Shell shell, ESUsersession session, String projectName) {
		super(shell);
		this.session = ((ESUsersessionImpl) session).toInternalAPI();
		this.projectName = projectName;
	}

	private ESRemoteProject createRemoteProject(IProgressMonitor monitor) throws ESException {
		final String projectName = RunInUI.runWithResult(new Callable<String>() {

			public String call() throws Exception {
				final CreateProjectDialog dialog = new CreateProjectDialog(getShell());
				if (dialog.open() == Window.OK) {
					return dialog.getName();
				}
				return null;
			}
		});

		return createRemoteProject(session, projectName, StringUtils.EMPTY, monitor);
	}

	private ESRemoteProject createRemoteProject(final Usersession usersession, final String name,
		final String description, IProgressMonitor monitor) throws ESException {
		return usersession.toAPI().getServer().createRemoteProject(name, monitor);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public ESRemoteProject doRun(IProgressMonitor monitor) throws ESException {
		try {
			if (session == null) {
				throw new IllegalArgumentException(Messages.UICreateRemoteProjectController_SessionMustNotBeNull);
			}

			if (projectName == null) {
				return createRemoteProject(monitor);
			}

			return createRemoteProject(session, projectName, StringUtils.EMPTY, monitor);

		} catch (final ESException e) {
			RunInUI.run(new Callable<Void>() {
				public Void call() throws Exception {
					MessageDialog.openError(
						getShell(),
						Messages.UICreateRemoteProjectController_CreateProjectFailed_Title,
						MessageFormat.format(Messages.UICreateRemoteProjectController_CreateProjectFailed_Message,
							e.getMessage()));
					return null;
				}
			});
		}

		return null;
	}

}
