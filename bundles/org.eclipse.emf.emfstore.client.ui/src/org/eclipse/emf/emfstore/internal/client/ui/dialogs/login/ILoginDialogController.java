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
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.login;

import java.util.List;

import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.server.exceptions.ESException;

/**
 * The login dialog controller manages a given {@link ESUsersession} and/or a {@link ESServer} instance
 * to determine when it is necessary to open a {@link LoginDialog} in order to authenticate the user.
 * If authentication already took place no such dialog should be opened.
 * 
 * @author ovonwesen
 * @author emueller
 */
public interface ILoginDialogController {

	/**
	 * Tries to login the given {@link ESUsersession}. If successful, the user session
	 * is attached to the workspace and saved.
	 * 
	 * @param usersession
	 *            the usersession to be validated
	 * @throws ESException
	 *             in case the log-in of the user session fails
	 */
	void validate(ESUsersession usersession) throws ESException;

	/**
	 * Returns the {@link ESUsersession} the login dialog controller was assigned to, if any.
	 * 
	 * @return the assigned user session or <code>null</code>, if none exists
	 */
	ESUsersession getUsersession();

	/**
	 * Returns the available {@link ESUsersession}s based on server info object, that is retrieved via
	 * {@link #getServer()}.
	 * 
	 * @return all available user sessions as an array.
	 */
	List<ESUsersession> getKnownUsersessions();

	/**
	 * Returns the {@link ESServer} the login dialog controller was assigned to, if any.
	 * If no server info was set, {@link #getUsersession() } will be used to try to determine the
	 * relevant server info.
	 * 
	 * @return the server info, if any
	 */
	ESServer getServer();
}