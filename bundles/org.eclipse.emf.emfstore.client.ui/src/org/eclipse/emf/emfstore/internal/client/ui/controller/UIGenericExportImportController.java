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

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.internal.client.importexport.ExportImportControllerExecutor;
import org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.client.ui.util.EMFStorePreferenceHelper;
import org.eclipse.emf.emfstore.internal.client.ui.util.FileDialogHelper;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Generic UI-specific controller class that is capable of executing
 * both, import and export controller classes.
 * 
 * @author emueller
 * 
 */
public class UIGenericExportImportController extends AbstractEMFStoreUIController<Void> {

	private final IExportImportController controller;
	private File file;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell}
	 * @param controller
	 *            the {@link IExportImportController} to be executed
	 */
	public UIGenericExportImportController(Shell shell, IExportImportController controller) {
		super(shell);
		this.controller = controller;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#preRun()
	 */
	@Override
	public boolean preRun() {
		file = selectFile();
		return file != null;
	}

	private File selectFile() {

		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		String absoluteFilePath;
		if (controller.isExport()) {
			absoluteFilePath = FileDialogHelper.openExportDialog(shell, controller.getFilename());
		} else {
			absoluteFilePath = FileDialogHelper.openImportDialog(shell);
		}

		// if (controller.getParentFolderPropertyKey() != null) {
		// final String initialPath = EMFStorePreferenceHelper.getPreference(controller.getParentFolderPropertyKey(),
		//				System.getProperty("user.home")); //$NON-NLS-1$
		// dialog.setFilterPath(initialPath);
		// }

		if (absoluteFilePath == null) {
			return null;
		}

		final File file = new File(absoluteFilePath);
		return file;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor progressMonitor) throws ESException {
		EMFStorePreferenceHelper.setPreference(controller.getParentFolderPropertyKey(), file.getParent());

		try {
			new ExportImportControllerExecutor(file, progressMonitor).execute(controller);
			MessageDialog.openInformation(getShell(), controller.isExport() ?
				Messages.UIGenericExportImportController_ExportImport_Title_0 :
				Messages.UIGenericExportImportController_ExportImport_Title_1 +
					Messages.UIGenericExportImportController_ExportImport_Title_2,
				MessageFormat.format(Messages.UIGenericExportImportController_ExportImport_Message_0,
					controller.getLabel(),
					controller.isExport() ?
						Messages.UIGenericExportImportController_ExportImport_Message_1 :
						Messages.UIGenericExportImportController_ExportImport_Message_2));
		} catch (final IOException e) {
			EMFStoreMessageDialog.showExceptionDialog(getShell(), e);
		}

		return null;
	}
}
