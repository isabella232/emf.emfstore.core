/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Max Hohenegger (bug 371196)
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.login;

import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.sessionprovider.ESAbstractSessionProvider;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.exceptions.LoginCanceledException;
import org.eclipse.emf.emfstore.internal.client.ui.common.RunInUI;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * An implementation of a session providers that uses a server selection and a login dialog
 * to authenticate users.
 * 
 * @author wesendon
 * @author emueller
 */
public class BasicUISessionProvider extends ESAbstractSessionProvider {

	private ESServer selectedServerInfo;

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.sessionprovider.ESAbstractSessionProvider#provideUsersession(org.eclipse.emf.emfstore.client.ESServer)
	 */
	@Override
	public ESUsersession provideUsersession(ESServer server) throws ESException {
		if (server == null) {
			final Integer userInput = RunInUI.runWithResult(new Callable<Integer>() {
				public Integer call() throws Exception {
					// try to retrieve a server info by showing a server info selection dialog
					final ServerInfoSelectionDialog dialog = new ServerInfoSelectionDialog(
						Display.getCurrent().getActiveShell(),
						ESWorkspaceProviderImpl.getInstance().getInternalWorkspace().getServerInfos());
					final int input = dialog.open();
					selectedServerInfo = dialog.getResult();
					return input;
				}
			});

			if (userInput == Window.OK) {
				server = selectedServerInfo;
			} else if (userInput == Window.CANCEL) {
				throw new LoginCanceledException(Messages.BasicUISessionProvider_UserCancelledOperation);
			}
		}
		if (server == null) {
			throw new AccessControlException(Messages.BasicUISessionProvider_ServerCouldNotBeDetermined);
		}

		return loginServerInfo(server);
	}

	/**
	 * Extracted from {@link #provideUsersession(ESServer)} in order to allow overwriting.
	 * This method logs in a given server.
	 * 
	 * @param server
	 *            the server from which a {@link ESUsersession} should be extracted
	 * @return a logged in usersession. If none could be obtained the login dialog will open
	 * @throws ESException in case of an exception
	 */
	protected ESUsersession loginServerInfo(ESServer server) throws ESException {
		// TODO Short cut for logged in sessions to avoid login screen.
		// We have to discuss whether this is really wanted.
		if (server.getLastUsersession() != null && server.getLastUsersession().isLoggedIn()) {
			return server.getLastUsersession();
		}
		return new LoginDialogController().login(server);
	}

	@Override
	public ESUsersession login(ESUsersession usersession) throws ESException {
		if (usersession != null) {
			return new LoginDialogController().login(usersession);
		}
		return null;
	}
}