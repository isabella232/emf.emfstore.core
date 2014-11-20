/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Eugen Neufeld - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;

/**
 * A helper class that can be used to export projects.
 * 
 * @author Eugen Neufeld, David Soto Setzke
 * 
 */
public final class FileDialogHelper {

	private static final String EMFSTORE_CLIENT_UI_PLUGIN_ID = "org.eclipse.emf.emfstore.client.ui"; //$NON-NLS-1$

	private static final String FILE_DIALOG_HELPER_CLASS = "org.eclipse.emf.emfstore.internal.client.ui.util.EMFStoreFileDialogHelperImpl"; //$NON-NLS-1$

	private FileDialogHelper() {
	}

	/**
	 * Opens an export file dialog.
	 * 
	 * @param shell
	 *            the parent shell
	 * @param fileName
	 *            the initial file name
	 * @return the absolute file path to the exported file, or {@code null} if export has been cancelled
	 */
	public static String openExportDialog(Shell shell, String fileName) {
		return getFilePathByFileDialog(shell, true, fileName);
	}

	/**
	 * Opens an import file dialog.
	 * 
	 * @param shell
	 *            the parent shell
	 * @return the absolute file path to the imported file, or {@code null} if import has been cancelled
	 */
	public static String openImportDialog(Shell shell) {
		return getFilePathByFileDialog(shell, false, null);
	}

	private static String getFilePathByFileDialog(Shell shell, boolean isExport, String fileName) {
		try {
			final Class<EMFStoreFileDialogHelper> clazz = loadClass(EMFSTORE_CLIENT_UI_PLUGIN_ID,
				FILE_DIALOG_HELPER_CLASS);
			final EMFStoreFileDialogHelper fileDialogHelper = clazz.getConstructor().newInstance();

			if (isExport) {
				return fileDialogHelper.getPathForExport(shell, fileName);
			}

			return fileDialogHelper.getPathForImport(shell);
		} catch (final ClassNotFoundException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final InstantiationException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final IllegalAccessException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final IllegalArgumentException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final InvocationTargetException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final NoSuchMethodException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		} catch (final SecurityException ex) {
			WorkspaceUtil.logException(ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> loadClass(String bundleName, String clazz) throws ClassNotFoundException {
		final Bundle bundle = Platform.getBundle(bundleName);
		if (bundle == null) {
			throw new ClassNotFoundException(clazz + " cannot be loaded because bundle " + bundleName //$NON-NLS-1$
				+ " cannot be resolved"); //$NON-NLS-1$
		}
		return (Class<T>) bundle.loadClass(clazz);
	}

}
