/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.util;

import java.io.File;

import org.eclipse.emf.emfstore.internal.client.ui.util.EMFStoreFileDialogHelper;
import org.eclipse.emf.emfstore.internal.client.ui.util.EMFStorePreferenceHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * {@link EMFStoreFileDialogHelper} using the SWT implementation of the {@link FileDialog}.
 * 
 * @author jfaltermeier
 * 
 */
public class EMFStoreFileDialogHelperImpl implements EMFStoreFileDialogHelper {

	private static final String IMPORT_MODEL_PATH = "org.eclipse.emf.emfstore.client.ui.importModelPath"; //$NON-NLS-1$
	private static final String EXPORT_MODEL_PATH = "org.eclipse.emf.emfstore.client.ui.exportModelPath"; //$NON-NLS-1$
	private static final String FILE_EXTENSION = "xmi"; //$NON-NLS-1$

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.util.EMFStoreFileDialogHelper#getPathForImport(org.eclipse.swt.widgets.Shell)
	 */
	public String getPathForImport(Shell shell) {
		final FileDialog dialog = new FileDialog(shell,
			SWT.OPEN);
		// dialog.setFilterNames(ECPImportHandlerHelper.FILTER_NAMES);
		// dialog.setFilterExtensions(ECPImportHandlerHelper.FILTER_EXTS);
		final String initialPath = EMFStorePreferenceHelper.getPreference(IMPORT_MODEL_PATH,
			System.getProperty("user.home")); //$NON-NLS-1$
		dialog.setFilterPath(initialPath);

		final String fileName = dialog.open();

		if (fileName == null) {
			return null;
		}

		final File file = new File(fileName);

		EMFStorePreferenceHelper.setPreference(IMPORT_MODEL_PATH, file.getParent());

		return file.getAbsolutePath();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.util.EMFStoreFileDialogHelper#getPathForExport(org.eclipse.swt.widgets.Shell,
	 *      java.lang.String)
	 */
	public String getPathForExport(Shell shell, String fileName) {
		final FileDialog dialog = new FileDialog(shell,
			SWT.SAVE);
		// dialog.setFilterNames(ECPExportHandlerHelper.FILTER_NAMES);
		// dialog.setFilterExtensions(ECPExportHandlerHelper.FILTER_EXTS);
		final String initialPath = EMFStorePreferenceHelper.getPreference(EXPORT_MODEL_PATH,
			System.getProperty("user.home")); //$NON-NLS-1$
		dialog.setFilterPath(initialPath);
		dialog.setOverwrite(true);

		try {
			final String initialFileName = "ModelElement_" + fileName + "." + FILE_EXTENSION; //$NON-NLS-1$ //$NON-NLS-2$
			dialog.setFileName(initialFileName);
		} catch (final NullPointerException e) {
			// do nothing
		}

		final String filePath = dialog.open();
		return filePath;
	}

}
