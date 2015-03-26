/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * gurcankarakoc, deser - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.client.ui.util.EMFStorePreferenceHelper;
import org.eclipse.emf.emfstore.internal.client.ui.util.FileDialogHelper;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

/**
 * @author gurcankarakoc, deser
 */

public class CSVImportSource extends ImportSource {

	private static final String CSV_IMPORT_SOURCE_PATH = "org.eclipse.emf.emfstore.client.ui.CSVImportSourcePath"; //$NON-NLS-1$

	private final Map<String, ImportItemWrapper> groupMap = new LinkedHashMap<String, ImportItemWrapper>();

	private ArrayList<ImportItemWrapper> groups;
	private ArrayList<ImportItemWrapper> users;

	private String absFileName;

	/**
	 * Constructor.
	 */
	public CSVImportSource() {
	}

	/**
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource#getChildren(java.lang.Object)
	 * @param ob
	 *            the object to get the children from
	 * @return the children of the given object
	 */
	@Override
	public Object[] getChildren(Object ob) {
		final ImportItemWrapper importWrapper = (ImportItemWrapper) ob;
		if (importWrapper != null && importWrapper.getChildOrgUnits() != null) {
			return importWrapper.getChildOrgUnits().toArray();
		}
		return null;
	}

	/**
	 * @param ob
	 *            The object to get the root elements from
	 * @return The list of groups, which were read from the specified file.
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object ob) {
		return groups.toArray();
	}

	/**
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource#getLabel()
	 * @return String label.
	 */
	@Override
	public String getLabel() {
		return Messages.CSVImportSource_ImportFromCSV;
	}

	/**
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource#init(Shell)
	 * @param shell
	 *            the shell, which holds the dialog for file selection
	 * @return if a file was selected and successfully handled
	 */
	@Override
	public boolean init(Shell shell) {
		// clear old data
		groups = new ArrayList<ImportItemWrapper>();
		users = new ArrayList<ImportItemWrapper>();

		final String absolutePath = FileDialogHelper.openImportDialog(shell);

		// // TODO: RAP compat
		// final FileDialog dialog = createFileDialog();
		// if (dialog.open() == null) {
		// return false;
		// }
		//
		// final String fileName = dialog.getFileName();
		// final String filterPath = dialog.getFilterPath();
		// if (fileName == null) {
		// return false;
		// }

		if (absolutePath == null) {
			return false;
		}

		absFileName = absolutePath;
		final File file = new File(absFileName);
		EMFStorePreferenceHelper.setPreference(CSV_IMPORT_SOURCE_PATH, file.getParent());

		try {
			return readFile(file);
		} catch (final FileNotFoundException e) {
			// TODO: sensible error messages
			handleError(e);
			return false;
		} catch (final IOException e) {
			handleError(e);
			return false;
		} catch (final ArrayIndexOutOfBoundsException e) {
			handleError(e);
			return false;
		}
	}

	private boolean readFile(File file)
		throws IOException {

		final InputStreamReader isr = new InputStreamReader(new FileInputStream(file)); // "8859_1","ASCII"
		final BufferedReader bufferedReader = new BufferedReader(isr);

		String line;

		final int indexUserName = 0;
		final int indexForGroup = 1;

		try {
			while ((line = bufferedReader.readLine()) != null) {
				// Get the user information from the next line
				final String[] title = line.split(","); //$NON-NLS-1$

				final String userName = title[indexUserName];
				final String groupName = title[indexForGroup];

				ImportItemWrapper importWrapper = null;
				ArrayList<ImportItemWrapper> childOrgUnits;
				if (groupMap.get(groupName) == null) {
					final ACGroup group = AccesscontrolFactory.eINSTANCE.createACGroup();
					importWrapper = new ImportItemWrapper(null, group);

					group.setName(groupName);
					groups.add(importWrapper);
					groupMap.put(groupName, importWrapper);
					childOrgUnits = new ArrayList<ImportItemWrapper>();
				} else {
					importWrapper = groupMap.get(groupName);
					childOrgUnits = importWrapper.getChildOrgUnits();
				}

				final ACUser user = AccesscontrolFactory.eINSTANCE.createACUser();
				user.setName(userName);
				final ImportItemWrapper userImportWrapper = new ImportItemWrapper(null, user, importWrapper);
				users.add(userImportWrapper);

				childOrgUnits.add(userImportWrapper);
				importWrapper.setChildOrgUnits(childOrgUnits);
			}
		} finally {
			try {
				bufferedReader.close();
				isr.close();
			} catch (final IOException e) {
				WorkspaceUtil.logWarning(e.getMessage(), e);
				EMFStoreMessageDialog.showExceptionDialog(e);
				return false;
			}
		}

		return true;
	}

	private void handleError(final Exception e) {
		WorkspaceUtil.logWarning(e.getMessage(), e);
		EMFStoreMessageDialog.showExceptionDialog(e);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource#getMessage()
	 */
	@Override
	public String getMessage() {
		return Messages.CSVImportSource_Importing + absFileName;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Nothing to change
	}
}
