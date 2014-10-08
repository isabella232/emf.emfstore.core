/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESRemoteProjectImpl;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * UI controller for deleting a project on the server.
 * 
 * @author emueller
 */
public class UIDeleteRemoteProjectController extends AbstractEMFStoreUIController<Void> {

	private final ServerInfo serverInfo;
	private final ESUsersession session;
	private final ProjectInfo projectInfo;
	private final ESRemoteProject remoteProject;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * <p>
	 * The {@link ESUsersession} to delete the remote server instance will be injected. If this is not desired use one
	 * of the other constructors.
	 * </p>
	 * 
	 * @param shell
	 *            the parent {@link Shell} that should be used during the deletion of the project
	 * @param serverInfo
	 *            the server info containing the information about the server the project is hosted on
	 * @param projectInfo
	 *            the {@link ProjectInfo} that will be used to identify the remote project
	 */
	public UIDeleteRemoteProjectController(Shell shell, ServerInfo serverInfo, ProjectInfo projectInfo) {
		super(shell);
		this.serverInfo = serverInfo;
		this.projectInfo = projectInfo;
		session = null;
		remoteProject = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell} that should be used during the deletion of the project
	 * @param session
	 *            the {@link ESUsersession} that should be used to delete the remote server instance
	 * @param projectInfo
	 *            the {@link ProjectInfo} that will be used to identify the remote project
	 */
	public UIDeleteRemoteProjectController(Shell shell, ESUsersession session, ProjectInfo projectInfo) {
		super(shell);
		serverInfo = null;
		this.projectInfo = projectInfo;
		this.session = session;
		remoteProject = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell} that should be used during the deletion of the project
	 * @param session
	 *            the {@link ESUsersession} that should be used to delete the remote server instance
	 * @param remoteProject
	 *            the {@link ESRemoteProject} that should be deleted
	 */
	public UIDeleteRemoteProjectController(Shell shell, ESUsersession session, ESRemoteProject remoteProject) {
		super(shell);
		serverInfo = null;
		projectInfo = null;
		this.session = session;
		this.remoteProject = remoteProject;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor monitor) throws ESException {

		try {

			if (remoteProject != null) {
				deleteRemoteProject(remoteProject, session, monitor);
				return null;
			}
			deleteRemoteProject(serverInfo, projectInfo, session, monitor);
			return null;

		} catch (final ESException e) {
			String msg;
			if (remoteProject != null) {
				msg = MessageFormat.format(Messages.UIDeleteRemoteProjectController_DeleteProjectFailed_Message,
					remoteProject.getProjectName());
			} else {
				msg = MessageFormat.format(Messages.UIDeleteRemoteProjectController_DeleteProjectFailed_Message,
					projectInfo.getName());
			}
			MessageDialog.openError(getShell(), Messages.UIDeleteRemoteProjectController_DeleteProjectFailed_Title, msg
				+ e.getMessage());
		}

		return null;
	}

	private void deleteRemoteProject(ServerInfo server, ProjectInfo projectInfo, ESUsersession session,
		IProgressMonitor monitor) throws ESException {
		if (confirm(
			Messages.UIDeleteRemoteProjectController_Confirmation,
			MessageFormat.format(Messages.UIDeleteRemoteProjectController_DeleteRemoteProjectPrompt,
				projectInfo.getName()))) {
			if (session != null) {
				new ESRemoteProjectImpl(server, projectInfo).delete(session, monitor);
			} else {
				new ESRemoteProjectImpl(server, projectInfo).delete(monitor);
			}
		}
	}

	private void deleteRemoteProject(ESRemoteProject remoteProject, ESUsersession session, IProgressMonitor monitor)
		throws ESException {
		if (confirm(Messages.UIDeleteProjectController_Confirmation,
			MessageFormat.format(
				Messages.UIDeleteRemoteProjectController_DeleteRemoteProjectPrompt, remoteProject.getProjectName()))) {
			if (session != null) {
				remoteProject.delete(session, monitor);
			} else {
				remoteProject.delete(monitor);
			}
		}
	}
}
