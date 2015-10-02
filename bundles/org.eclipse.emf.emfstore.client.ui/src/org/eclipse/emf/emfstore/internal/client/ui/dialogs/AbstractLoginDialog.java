/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs;

import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.login.ILoginDialogController;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author emueller
 *
 */
public abstract class AbstractLoginDialog extends TitleAreaDialog {

	private Usersession selectedSession;

	private String password;

	private boolean isSavePassword;

	private boolean isPasswordModified;

	private final ILoginDialogController controller;

	/**
	 * Constructor.
	 *
	 * @param parentShell
	 *            the parent shell
	 * @param controller
	 *            the login dialog controller responsible for opening up the
	 *            login dialog
	 */
	public AbstractLoginDialog(Shell parentShell, ILoginDialogController controller) {
		super(parentShell);
		this.controller = controller;
	}

	/**
	 * Returns the selected session.
	 *
	 * @return the selected session
	 */
	public Usersession getSelectedUsersession() {
		return selectedSession;
	}

	/**
	 * Sets the selected {@link Usersession}.
	 *
	 * @param selectedSession the selected {@link Usersession}
	 */
	public void setSelectedSession(Usersession selectedSession) {
		this.selectedSession = selectedSession;
	}

	/**
	 * Returns the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 *
	 * @param password the password to be set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Whether the password should be saved.
	 *
	 * @return {@code true}, if the password is saved, {@code false} otherwise
	 */
	public boolean isSavePassword() {
		return isSavePassword;
	}

	/**
	 * Whether the password should be saved.
	 *
	 * @param shouldSave {@code true}, if the password should be saved, {@code false} otherwise
	 */
	public void setPasswordSaved(boolean shouldSave) {
		isSavePassword = shouldSave;
	}

	/**
	 * Whether the password has been modified.
	 *
	 * @return {@code true}, if the password has been modified, {@code false} otherwise
	 */
	public boolean isPasswordModified() {
		return isPasswordModified;
	}

	/**
	 * Sets whether the password has been modified.
	 *
	 * @param passwordModified
	 *            {@code true} if the password has been modified, {@code false} otherwise
	 */
	public void setPasswordModified(boolean passwordModified) {
		isPasswordModified = passwordModified;
	}

	/**
	 * @return the controller
	 */
	public ILoginDialogController getController() {
		return controller;
	}

}
