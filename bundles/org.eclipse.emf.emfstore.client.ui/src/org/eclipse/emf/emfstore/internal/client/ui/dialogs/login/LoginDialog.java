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
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.common.APIUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

/**
 * The login dialog.
 * 
 * @author ovonwesen
 * @author emueller
 * 
 * @see LoginDialogController
 */
public class LoginDialog extends TitleAreaDialog {

	private static final String CLIENT_UI_BUNDLE = "org.eclipse.emf.emfstore.client.ui"; //$NON-NLS-1$
	private static final String LOGIN_ICON = "icons/login_icon.png"; //$NON-NLS-1$
	private Text passwordField;
	private Button savePassword;
	private ComboViewer usernameCombo;

	private final ILoginDialogController controller;
	private Usersession selectedUsersession;
	private List<Usersession> knownUsersessions;
	private String password;
	private boolean passwordModified;
	private boolean isSavePassword;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 *            the parent shell to be used by the dialog
	 * @param controller
	 *            the login dialog controller repsonsible for opening up the
	 *            login dialog
	 * 
	 */
	public LoginDialog(Shell parentShell, ILoginDialogController controller) {
		super(parentShell);
		this.controller = controller;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(ResourceManager.getPluginImage(CLIENT_UI_BUNDLE, LOGIN_ICON));
		setTitle(Messages.LoginDialog_Login_To + controller.getServer().getName());
		setMessage(Messages.LoginDialog_Enter_Name_And_Password);
		getShell().setText(Messages.LoginDialog_Auth_Required);
		final Composite area = (Composite) super.createDialogArea(parent);
		final Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Composite loginContainer = new Composite(container, SWT.NONE);
		loginContainer.setLayout(new GridLayout(3, false));
		loginContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
			true, 1, 1));
		loginContainer.setBounds(0, 0, 64, 64);

		createUsernameLabel(loginContainer);
		createUsernameCombo(loginContainer);
		createPasswordLabel(loginContainer);
		createPasswordField(loginContainer);
		createSavePasswordLabel(loginContainer);
		createSavePassword(loginContainer);

		initData();
		if (controller.getUsersession() == null) {
			final ESUsersession lastUsersession = controller.getServer().getLastUsersession();
			if (lastUsersession != null) {
				loadUsersession(((ESUsersessionImpl) lastUsersession).toInternalAPI());
			} else {
				loadUsersession(null);
			}
		} else {
			final ESUsersession usersession = controller.getUsersession();
			loadUsersession(((ESUsersessionImpl) usersession).toInternalAPI());
		}
		return area;
	}

	private void createSavePassword(Composite parent) {
		savePassword = new Button(parent, SWT.CHECK);
		new Label(parent, SWT.NONE);
	}

	private void createSavePasswordLabel(Composite parent) {
		final Label savePasswordLabel = new Label(parent, SWT.NONE);
		savePasswordLabel.setText(Messages.LoginDialog_Save_Password);
	}

	private void createPasswordField(Composite parent) {
		passwordField = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 250;
		passwordField.setLayoutData(gridData);
		passwordField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				passwordModified = true;
				flushErrorMessage();
			}
		});
		new Label(parent, SWT.NONE);
	}

	private void createPasswordLabel(Composite parent) {
		final Label passwordLabel = new Label(parent, SWT.NONE);
		final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 80;
		passwordLabel.setLayoutData(gridData);
		passwordLabel.setText(Messages.LoginDialog_Password);
	}

	private void createUsernameLabel(Composite parent) {

		final Label usernameLabel = new Label(parent, SWT.NONE);
		final GridData gridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false,
			1, 1);
		gridData.widthHint = 95;
		usernameLabel.setLayoutData(gridData);
		usernameLabel.setText(Messages.LoginDialog_Username);
	}

	private void createUsernameCombo(Composite parent) {
		usernameCombo = new ComboViewer(parent, SWT.NONE);
		final ComboListener comboListener = new ComboListener();
		usernameCombo.addPostSelectionChangedListener(comboListener);
		final Combo combo = usernameCombo.getCombo();
		combo.addModifyListener(comboListener);
		final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gridData.widthHint = 235;
		combo.setLayoutData(gridData);
		new Label(parent, SWT.NONE);
	}

	private void initData() {
		usernameCombo.setContentProvider(ArrayContentProvider.getInstance());
		usernameCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Usersession
					&& ((Usersession) element).getUsername() != null) {
					return ((Usersession) element).getUsername();
				}
				return super.getText(element);
			}
		});

		knownUsersessions = APIUtil.mapToInternalAPI(Usersession.class, controller.getKnownUsersessions());
		usernameCombo.setInput(knownUsersessions);
	}

	/**
	 * Fills the login dialog data according to the given {@link Usersession}.
	 * 
	 * @param usersession
	 *            the user session to be loaded
	 */
	private void loadUsersession(Usersession usersession) {
		if (usersession != null && getSelectedUsersession() == usersession) {
			return;
		}

		selectedUsersession = usersession;

		// reset fields
		passwordField.setMessage(StringUtils.EMPTY);
		savePassword.setSelection(false);

		if (getSelectedUsersession() != null) {

			// check whether text is set correctly
			if (!usernameCombo.getCombo().getText()
				.equals(getSelectedUsersession().getUsername())) {
				usernameCombo.getCombo().setText(
					getSelectedUsersession().getUsername());
			}

			if (getSelectedUsersession().isSavePassword()
				&& getSelectedUsersession().getPassword() != null) {
				passwordField
					.setMessage(Messages.LoginDialog_Password_Saved_Reenter_To_Change);
				passwordField.setText(StringUtils.EMPTY);
				savePassword.setSelection(true);
			}
			// reset password modified. modified password is only relevant when
			// dealing with saved passwords.
			passwordModified = false;
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		final String username = usernameCombo.getCombo().getText();

		Usersession candidateSession = getSelectedUsersession();
		final ESServerImpl server = (ESServerImpl) controller.getServer();

		// try to find usersession with same username in order to avoid
		// duplicates
		if (candidateSession == null) {
			candidateSession = getUsersessionIfKnown(username);
		}

		if (candidateSession == null
			|| !candidateSession.getServerInfo().equals(server.toInternalAPI())) {
			final ESServerImpl serverImpl = (ESServerImpl) controller.getServer();
			candidateSession = ModelFactory.eINSTANCE.createUsersession();
			final Usersession session = candidateSession;
			selectedUsersession = candidateSession;

			RunESCommand.run(new Callable<Void>() {
				public Void call() throws Exception {
					session.setServerInfo(serverImpl.toInternalAPI());
					session.setUsername(username);
					return null;
				}
			});
		}

		password = passwordField.getText();
		isSavePassword = savePassword.getSelection();

		super.okPressed();
	}

	private Usersession getUsersessionIfKnown(String username) {

		if (getSelectedUsersession() != null && getSelectedUsersession().getUsername().equals(username)) {
			return getSelectedUsersession();
		}

		for (final Usersession session : knownUsersessions) {
			if (session.getUsername().equals(username)) {
				return session;
			}
		}
		return null;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent,
			IDialogConstants.OK_ID,
			Messages.LoginDialog_Ok,
			true);
		createButton(parent,
			IDialogConstants.CANCEL_ID,
			Messages.LoginDialog_Cancel,
			false);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(400, 250);
	}

	/**
	 * Clears the error message.
	 */
	private void flushErrorMessage() {
		setErrorMessage(null);
	}

	/**
	 * Returns the selected {@link Usersession}.
	 * 
	 * @return the selected {@link Usersession}
	 */
	public Usersession getSelectedUsersession() {
		return selectedUsersession;
	}

	/**
	 * Simple listener for loading the selected usersession if the user changes
	 * the selected entry within the combo box that contains all known
	 * usersessions.
	 * 
	 * @author ovonwesen
	 * 
	 */
	private final class ComboListener implements ISelectionChangedListener,
		ModifyListener {
		private String lastText = StringUtils.EMPTY;

		public void selectionChanged(SelectionChangedEvent event) {
			final ISelection selection = event.getSelection();
			if (selection instanceof StructuredSelection) {
				final Object firstElement = ((StructuredSelection) selection)
					.getFirstElement();
				if (firstElement instanceof Usersession) {
					loadUsersession((Usersession) firstElement);
				}
			}
		}

		public void modifyText(ModifyEvent e) {
			final String text = usernameCombo.getCombo().getText();
			if (StringUtils.isNotBlank(text) && !text.equals(lastText)) {
				loadUsersession(getUsersessionIfKnown(text));
				lastText = text;
			}
			flushErrorMessage();
		}
	}

	/**
	 * Returns the password as entered by the user.
	 * 
	 * @return the entered password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Whether the password should be saved.
	 * 
	 * @return {@code true}, if the password should be saved, {@code false} otherwise
	 */
	public boolean isSavePassword() {
		return isSavePassword;
	}

	/**
	 * Whether the password has been modified.
	 * 
	 * @return {@code true}, if the password has been modified, {@code false} otherwise
	 */
	public boolean isPasswordModified() {
		return passwordModified;
	}
}