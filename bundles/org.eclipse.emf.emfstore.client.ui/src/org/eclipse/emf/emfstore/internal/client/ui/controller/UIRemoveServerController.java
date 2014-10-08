/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESWorkspaceImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * UI controller for removing a server from the workspace.
 * 
 * @author emueller
 */
public class UIRemoveServerController extends
	AbstractEMFStoreUIController<Void> {

	private final ServerInfo serverInfo;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent shell that should be used during the delet
	 * @param server
	 *            the server info that contains the information about which
	 *            server should be removed from the workspace
	 */
	public UIRemoveServerController(Shell shell, ESServer server) {
		super(shell);
		serverInfo = ((ESServerImpl) server).toInternalAPI();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor monitor) throws ESException {

		final boolean shouldDelete = MessageDialog.openQuestion(getShell(),
			Messages.UIRemoveServerController_Confirmation, String.format(
				Messages.UIRemoveServerController_DeleteProject_Prompt,
				serverInfo.getName()));

		if (!shouldDelete) {
			return null;
		}

		final ESWorkspaceImpl workspace = ESWorkspaceProviderImpl.getInstance().getWorkspace();
		final EList<ProjectSpace> projectSpaces = workspace.toInternalAPI().getProjectSpaces();
		final ArrayList<ProjectSpace> usedSpaces = new ArrayList<ProjectSpace>();

		for (final ProjectSpace projectSpace : projectSpaces) {
			if (projectSpace.getUsersession() != null
				&& projectSpace.getUsersession().getServerInfo()
					.equals(serverInfo)) {
				usedSpaces.add(projectSpace);
			}
		}

		RunInUI.run(new Callable<Void>() {
			public Void call() throws Exception {
				ESWorkspaceProviderImpl.getInstance().getWorkspace().getServers()
					.remove(serverInfo);
				return null;
			}
		});

		if (usedSpaces.size() == 0) {
			// TODO: add code to add & remove server
			new EMFStoreCommand() {
				@Override
				protected void doRun() {
					EcoreUtil.delete(serverInfo);
					// TODO OTS auto save;
					// ESWorkspaceProviderImpl.getInstance().getWorkspace().save();
				}
			}.run(false);

			return null;
		}

		final StringBuilder message = new StringBuilder();

		for (final ProjectSpace pSpace : usedSpaces) {
			message.append(Messages.UIRemoveServerController_Newline + pSpace.getProjectName());
		}

		MessageDialog.openError(
			getShell(),
			Messages.UIRemoveServerController_ErrorDelete_Title,
			String.format(
				Messages.UIRemoveServerController_ErrorDelete_Message
					+ message.toString(),
				serverInfo.getName()));

		return null;
	}

}