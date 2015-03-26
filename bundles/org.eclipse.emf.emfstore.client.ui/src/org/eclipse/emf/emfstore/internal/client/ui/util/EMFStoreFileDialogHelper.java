/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
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

import org.eclipse.swt.widgets.Shell;

/**
 * Interface defining methods for retrieving Filepaths from a {@link org.eclipse.swt.widgets.FileDialog FileDialog}.
 *
 * @author jfaltermeier
 *
 */
public interface EMFStoreFileDialogHelper {

	/**
	 * Returns the absolute file path of the file selected by the user.
	 *
	 * @param shell The shell to open the {@link org.eclipse.swt.widgets.FileDialog FileDialog}
	 * @return the absolute filename or <code>null</code> if no file was selected
	 */
	String getPathForImport(Shell shell);

	/**
	 * Returns the file path to export a model element to.
	 *
	 * @param shell The shell to open the {@link org.eclipse.swt.widgets.FileDialog FileDialog}
	 * @param fileName a proposed filename for the export
	 * @return the file path
	 */
	String getPathForExport(Shell shell, String fileName);
}
