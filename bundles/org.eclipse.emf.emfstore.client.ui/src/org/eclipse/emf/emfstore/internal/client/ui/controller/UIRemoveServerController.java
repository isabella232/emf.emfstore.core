/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.Workspace;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
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

		final Workspace workspace = ESWorkspaceProviderImpl.getInstance().getInternalWorkspace();
		workspace.getServerInfos().remove(serverInfo);
		final Set<Usersession> invalidSessions = new LinkedHashSet<Usersession>();
		// remove all now invalid sessions
		for (final Usersession session : ESWorkspaceProviderImpl.getInstance().getInternalWorkspace().getUsersessions()) {
			if (session.getServerInfo() == serverInfo) {
				invalidSessions.add(session);
			}
		}
		workspace.getUsersessions().removeAll(invalidSessions);
		workspace.save();

		return null;
	}

}