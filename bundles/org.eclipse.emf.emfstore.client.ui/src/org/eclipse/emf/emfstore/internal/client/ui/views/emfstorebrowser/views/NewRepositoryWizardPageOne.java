/*******************************************************************************
 * Copyright (c) 2008-2016 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Shterev, Carl Pfeiffer - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.views;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.exceptions.ESCertificateException;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * The main page of the wizard.
 *
 * @author shterev
 */
public class NewRepositoryWizardPageOne extends WizardPage {

	/**
	 * Serial version UID for RAP.
	 */
	public static final long serialVersionUID = 1L;
	private static final int DEFAULT_PORT = 8080;
	private Text name;
	private Text url;
	private Spinner port;
	private Combo certificateCombo;

	/**
	 * Default constructor.
	 */
	public NewRepositoryWizardPageOne() {
		super(Messages.NewRepositoryWizardPageOne_Main);
		setTitle(Messages.NewRepositoryWizardPageOne_ServerDetails);
		setDescription(Messages.NewRepositoryWizardPageOne_SelectDetails);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(Composite parent) {
		final NewRepositoryWizard wizard = (NewRepositoryWizard) getWizard();
		final ESServer server = wizard.getServer();

		GridData gd;
		final Composite composite = new Composite(parent, SWT.NONE);

		final GridLayout gl = new GridLayout();
		final int ncol = 3;
		gl.numColumns = ncol;
		composite.setLayout(gl);

		// Server Name
		new Label(composite, SWT.NONE).setText(Messages.NewRepositoryWizardPageOne_Name);
		name = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol - 1;
		name.setLayoutData(gd);

		// Server URL
		new Label(composite, SWT.NONE).setText(Messages.NewRepositoryWizardPageOne_URL);
		url = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol - 1;
		url.setLayoutData(gd);

		// Server Port
		new Label(composite, SWT.NONE).setText(Messages.NewRepositoryWizardPageOne_Port);
		port = new Spinner(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol - 1;
		port.setLayoutData(gd);
		port.setValues(DEFAULT_PORT, 1, 999999, 0, 1, 10);
		setControl(composite);

		// Certificate
		new Label(composite, SWT.NONE).setText(Messages.NewRepositoryWizardPageOne_Certificate);
		certificateCombo = new Combo(composite, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ncol - 2;
		certificateCombo.setLayoutData(gd);
		certificateCombo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		initCombo();

		// Choose Certificate, Opens Dialogue
		final Button button = new Button(composite, SWT.NONE);
		button.setText(Messages.NewRepositoryWizardPageOne_Edit);
		button.addSelectionListener(new SelectionDialogListener());

		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 1;
		button.setLayoutData(gd);

		if (server.getURL() != null) {
			name.setText(server.getName());
			url.setText(server.getURL());
			port.setSelection(server.getPort());
			if (server.getCertificateAlias() == null) {
				return;
			}

			setCertificateSelection(server);
		}
	}

	private void setCertificateSelection(final ESServer server) {
		try {
			if (KeyStoreManager.getInstance().contains(server.getCertificateAlias())) {
				for (int i = 0; i < certificateCombo.getItemCount(); i++) {
					if (certificateCombo.getItem(i).equals(server.getCertificateAlias())) {
						certificateCombo.select(i);
						break;
					}
				}
			} else {
				certificateCombo.setText(StringUtils.EMPTY);
			}
		} catch (final ESCertificateException e1) {
			certificateCombo.setText(StringUtils.EMPTY);
		}
	}

	private void initCombo() {
		try {
			final ArrayList<String> certificates = KeyStoreManager.getInstance().getCertificates();
			certificateCombo.setItems(certificates.toArray(new String[certificates.size()]));

			final NewRepositoryWizard wizard = (NewRepositoryWizard) getWizard();
			final ESServer server = wizard.getServer();
			final String selectedCertificate = server.getCertificateAlias();
			certificateCombo.select(certificates.indexOf(selectedCertificate));
		} catch (final ESCertificateException e) {
			WorkspaceUtil.logException(e.getMessage(), e);
		}
	}

	/**
	 * @return if the input on the current page is valid.
	 */
	@Override
	public boolean canFlipToNextPage() {
		if (getErrorMessage() != null) {
			return false;
		}
		if (isTextNonEmpty(name.getText()) && isTextNonEmpty(url.getText()) && isComboNotEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Saves the uses choices from this page to the model. Called on exit of the
	 * page
	 *
	 * @return the server
	 */
	public ESServer getServer() {
		return ESServer.FACTORY.createServer(name.getText(),
			url.getText(),
			port.getSelection(),
			certificateCombo.getText());
	}

	/**
	 * Returns true if Text is not empty, i.e. not null/only space characters.
	 *
	 * @param t
	 * @return boolean
	 */
	private static boolean isTextNonEmpty(String s) {
		if (s != null && s.trim().length() > 0) {
			return true;
		}
		return false;
	}

	private boolean isComboNotEmpty() {
		final String s = certificateCombo.getItem(certificateCombo.getSelectionIndex());
		return isTextNonEmpty(s);
	}

	/**
	 * Listener for the selection dialog.
	 *
	 * @author pfeifferc
	 */
	class SelectionDialogListener implements SelectionListener {

		/**
		 * Serial version UID for RAP.
		 */
		public static final long serialVersionUID = 1L;

		/**
		 * @param e
		 *            selection event
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// nothing to do
		}

		/**
		 * @param e
		 *            selection event
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			final CertificateSelectionDialog csd = new CertificateSelectionDialog(
				Display.getCurrent().getActiveShell(),
				new CertificateSelectionDialogLabelProvider());
			ArrayList<String> certificates;
			try {
				certificates = KeyStoreManager.getInstance().getCertificates();
				csd.setElements(certificates.toArray());
			} catch (final ESCertificateException e1) {
				csd.setErrorMessage(e1.getMessage());
			}
			csd.setBlockOnOpen(true);
			csd.setTitle(Messages.NewRepositoryWizardPageOne_CertificateSelectionDialog);
			csd.open();
			if (csd.getReturnCode() == Window.OK) {
				initCombo();
				for (int i = 0; i < certificateCombo.getItemCount(); i++) {
					final String item = certificateCombo.getItem(i);
					if (item.equals(csd.getCertificateAlias())) {
						certificateCombo.select(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * {@link LabelProvider} for the certificate selection dialog.
	 *
	 */
	final class CertificateSelectionDialogLabelProvider extends LabelProvider {

		/**
		 * Serial version UID for RAP.
		 */
		public static final long serialVersionUID = 1L;

		@Override
		public String getText(Object element) {
			if (element instanceof String) {
				return element.toString();
			}
			return StringUtils.EMPTY;
		}
	}
}
