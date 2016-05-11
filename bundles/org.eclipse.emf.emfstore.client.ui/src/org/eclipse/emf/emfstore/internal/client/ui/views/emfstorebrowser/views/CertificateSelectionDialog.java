/*******************************************************************************
 * Copyright (c) 2008-2016 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Carl Pfeiffer, Maximilian Koegel - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.views;

import java.security.cert.X509Certificate;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.client.exceptions.ESCertificateException;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.ui.util.FileDialogHelper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * The modified ElementListSelectionDialog. Includes further functionality to
 * import certificates from files instead of choosing from the list.
 *
 * @author pfeifferc
 */
public class CertificateSelectionDialog extends ElementListSelectionDialog {

	/**
	 * Serial version UID for RAP.
	 */
	public static final long serialVersionUID = 1L;

	private TableItem selectedTableItem;
	private String alias = StringUtils.EMPTY;

	/**
	 * The constructor.
	 *
	 * @param parent
	 *            Parent
	 * @param renderer
	 *            Renderer
	 */
	public CertificateSelectionDialog(Shell parent, ILabelProvider renderer) {
		super(parent, renderer);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
	}

	/**
	 * Overridden method to allow adding further elements onto the dialog
	 * composite.
	 *
	 * @see org.eclipse.ui.dialogs.ElementListSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 * @return Control
	 * @param parent
	 *            Parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		// standard layout used by dialogue area
		final GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);

		// two column layout composite
		final Composite grid = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(grid);

		// left column composite
		final Composite left = new Composite(grid, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(left);

		// right column composite
		final Composite right = new Composite(grid, SWT.NONE);
		GridLayoutFactory.createFrom(layout).margins(layout.marginWidth, 35).applyTo(right);
		GridDataFactory.fillDefaults().grab(true, true).hint(300, 200).applyTo(right);
		applyDialogFont(right);

		// right column: certificate details
		new Label(right, SWT.NONE).setText(Messages.CertificateSelectionDialog_Alias);
		final Text certAlias = new Text(right, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(certAlias);
		certAlias.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		certAlias.setEditable(false);
		new Label(right, SWT.NONE).setText(Messages.CertificateSelectionDialog_Details);
		final Text certDetails = new Text(right, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, false).hint(300, 230).applyTo(certDetails);
		certDetails.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		certDetails.setEditable(false);

		// left column: dialogue area composite (displays certificates and
		// filter)
		final Composite dialogArea = new Composite(left, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(dialogArea);
		final Control control = super.createDialogArea(dialogArea);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(control);

		// left column: import button, composite used to ensure correct
		// alignment
		final Composite certButtonsComposite = new Composite(grid, SWT.NONE);
		GridLayoutFactory.createFrom(layout).numColumns(3).equalWidth(true).margins(layout.marginWidth, 0)
			.applyTo(certButtonsComposite);
		applyDialogFont(certButtonsComposite);
		final Button browse = new Button(certButtonsComposite, SWT.NONE);
		browse.setText(Messages.CertificateSelectionDialog_Import);
		browse.addSelectionListener(new CertificateSelectionListener());

		// Delete certificate
		final Button delete = new Button(certButtonsComposite, SWT.NONE);
		delete.setText(Messages.CertificateSelectionDialog_Delete);
		delete.addSelectionListener(new DeleteButtonSelectionListener());
		getFilteredList().addSelectionListener(new SelectionListenerImplementation(certDetails, certAlias));
		return control;
	}

	/**
	 * Returns the alias of the certificate.
	 *
	 * @return the certificate alias
	 */
	public String getCertificateAlias() {
		return alias;
	}

	/**
	 * Opens up an information {@link MessageDialog} with the given <code>errorMessage</code>.
	 *
	 * @param errorMessage
	 *            the error message
	 */
	// TODO: rename method
	public void setErrorMessage(String errorMessage) {
		MessageDialog.openInformation(Display.getDefault().getActiveShell(),
			Messages.CertificateSelectionDialog_Attention_Title, errorMessage);
	}

	/**
	 * Certificate Selection Listener.
	 *
	 * @author koegel
	 *
	 */
	final class SelectionListenerImplementation implements SelectionListener {
		/**
		 * Serial version UID for RAP.
		 */
		public static final long serialVersionUID = 1L;
		private final Text certDetails;
		private final Text certAlias;

		private SelectionListenerImplementation(Text certDetails, Text certAlias) {
			this.certDetails = certDetails;
			this.certAlias = certAlias;
		}

		/**
		 *
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// nothing to do
		}

		/**
		 *
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			if (((Table) e.getSource()).getItems().length > 0) {
				selectedTableItem = ((Table) e.getSource()).getItems()[((Table) e.getSource()).getSelectionIndex()];
				alias = selectedTableItem.getText();
				try {
					final X509Certificate selectedCertificate = (X509Certificate) KeyStoreManager.getInstance()
						.getCertificate(alias);
					final String[] details = selectedCertificate.toString().split("\n"); //$NON-NLS-1$
					String tmp = StringUtils.EMPTY;
					for (int i = 2; i < 14; i++) {
						tmp += i == 7 || i == 8 ? StringUtils.EMPTY : details[i].trim() + "\n"; //$NON-NLS-1$
					}
					certAlias.setText(alias);
					certDetails.setText(tmp);
				} catch (final ESCertificateException e1) {
					setErrorMessage(e1.getMessage());
				}
			}
		}
	}

	/**
	 * Choose certificate from file system and name it.
	 *
	 * @author pfeifferc
	 */
	class CertificateSelectionListener implements SelectionListener {
		/**
		 * Serial version UID for RAP.
		 */
		public static final long serialVersionUID = 1L;

		/**
		 * Add a certificate specified by the user.
		 *
		 * @param event
		 *            selection event
		 */
		public void widgetSelected(SelectionEvent event) {

			final String absoluteFilePath = FileDialogHelper.openImportDialog(getShell());

			if (StringUtils.isNotBlank(absoluteFilePath)) {
				final String location = absoluteFilePath;

				final InputDialog inputDialog = new InputDialog(Display.getCurrent().getActiveShell(),
					Messages.CertificateSelectionDialog_CertificateDesignation_Title,
					Messages.CertificateSelectionDialog_CertificateDesignation_Message,
					StringUtils.EMPTY, null);

				inputDialog.setBlockOnOpen(true);
				if (inputDialog.open() != Window.OK) {
					return;
				}

				String alias = inputDialog.getValue();
				if (StringUtils.isBlank(alias)) {
					alias = Messages.CertificateSelectionDialog_Unnamed + EcoreUtil.generateUUID();
				}

				try {
					KeyStoreManager.getInstance().addCertificate(alias, location);
				} catch (final ESCertificateException e) {
					setErrorMessage(e.getMessage());
				}

				try {
					setListElements(KeyStoreManager.getInstance().getCertificates().toArray());
				} catch (final ESCertificateException e1) {
					setErrorMessage(e1.getMessage());
				}
			}
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 * @param e
		 *            selection event
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// nothing to do
		}
	}

	/**
	 * Selection listener for the delete button.
	 *
	 */
	final class DeleteButtonSelectionListener implements SelectionListener {

		/**
		 * Serial version UID for RAP.
		 */
		public static final long serialVersionUID = 1L;

		/**
		 *
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// nothing to do
		}

		/**
		 *
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			if (selectedTableItem != null && StringUtils.isNotBlank(selectedTableItem.getText())) {
				final String alias = selectedTableItem.getText();
				try {
					KeyStoreManager.getInstance().deleteCertificate(alias);
					setListElements(KeyStoreManager.getInstance().getCertificates().toArray());
				} catch (final ESCertificateException e1) {
					setErrorMessage(e1.getMessage());
				}
			}
		}
	}
}
