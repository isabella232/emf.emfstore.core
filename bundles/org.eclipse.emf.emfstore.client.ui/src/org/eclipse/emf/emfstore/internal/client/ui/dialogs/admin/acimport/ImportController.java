/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * deser,karakoc
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * @author deser, karakoc
 */
public class ImportController {

	private final AdminBroker adminBroker;
	private ImportSource importSource;
	private final Map<ACOrgUnitId, ImportItemWrapper> importedUnits;

	/**
	 * @param adminBroker
	 *            the admin broker.
	 */
	public ImportController(AdminBroker adminBroker) {
		this.adminBroker = adminBroker;
		importedUnits = new HashMap<ACOrgUnitId, ImportItemWrapper>();
	}

	/**
	 * @param wrappedOrgUnits
	 *            a list of wrapped OrgUnits, which should be imported.
	 */
	public void importOrgUnits(ArrayList<ImportItemWrapper> wrappedOrgUnits) {

		// first go through the list and add all units of type group
		importGroups(wrappedOrgUnits);

		// then add all units of type user
		importUsers(wrappedOrgUnits);

		// finally set the associations on the imported units
		setAssociations();
	}

	private void importUsers(ArrayList<ImportItemWrapper> wrappedOrgUnits) {
		for (int i = 0; i < wrappedOrgUnits.size(); i++) {
			final ImportItemWrapper wrappedOrgUnit = wrappedOrgUnits.get(i);
			if (wrappedOrgUnit.getOrgUnit() instanceof ACUser) {
				// add this user to the system
				try {
					final String username = wrappedOrgUnit.getOrgUnit().getName();
					if (null == existUser(username)) {
						importedUnits.put(adminBroker.createUser(username), wrappedOrgUnit);
					}
				} catch (final AccessControlException ex) {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.ImportController_Insufficient_Access_Rights,
						Messages.ImportController_Not_Allowed_To_Create_Users);
				} catch (final ESException ex) {
					EMFStoreMessageDialog.showExceptionDialog(ex);
				}
			}
		}
	}

	private void importGroups(ArrayList<ImportItemWrapper> wrappedOrgUnits) {
		for (int i = 0; i < wrappedOrgUnits.size(); i++) {
			final ImportItemWrapper wrappedOrgUnit = wrappedOrgUnits.get(i);
			if (wrappedOrgUnit.getOrgUnit() instanceof ACGroup) {
				// add this group to the system if it doesn't exist
				try {
					final String groupname = wrappedOrgUnit.getOrgUnit().getName();
					if (null == existGroup(groupname)) {
						importedUnits.put(adminBroker.createGroup(groupname), wrappedOrgUnit);
					}
				} catch (final AccessControlException ex) {
					MessageDialog.openWarning(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.ImportController_Insufficient_Access_Rights,
						Messages.ImportController_Not_Allowed_To_Create_Groups);
				} catch (final ESException ex) {
					EMFStoreMessageDialog.showExceptionDialog(ex);
				}
			}
		}
	}

	private void setAssociations() {
		for (final ACOrgUnitId unitId : importedUnits.keySet()) {
			if (importedUnits.get(unitId).getParentOrgUnit() != null) {

				final ACOrgUnitId existGroup = existGroup(importedUnits.get(unitId).getParentOrgUnit().getOrgUnit()
					.getName());

				// we do not want self-containment
				if (existGroup != null && !existGroup.equals(unitId)) {
					try {
						adminBroker.addMember(existGroup, unitId);
					} catch (final ESException e) {
						WorkspaceUtil.logWarning(e.getMessage(), e);
						EMFStoreMessageDialog.showExceptionDialog(e);
					}
				}
			}
		}
	}

	/**
	 * Checks whether a group with the given name exists.
	 *
	 * @param groupName
	 *            the name of a group
	 * @return the {@link ACOrgUnitId} of the group with the matching name if the group exists, {@code null} otherwise
	 */
	private ACOrgUnitId existGroup(final String groupName) {
		ACOrgUnitId exist = null;
		try {
			for (final ACGroup acGroup : getAdminBroker().getGroups()) {
				if (acGroup.getName().equalsIgnoreCase(groupName)) {
					exist = acGroup.getId();
					break;
				}
			}
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.ImportController_Insufficient_Access_Rights,
				Messages.ImportController_Not_Allowed_To_List_Groups);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		return exist;
	}

	/**
	 * Checks whether an user with the given name exists.
	 *
	 * @param userName
	 *            the name of an user
	 * @return the {@link ACOrgUnitId} of the user with the matching name if the user exists, {@code null} otherwise
	 */
	private ACOrgUnitId existUser(final String userName) {
		ACOrgUnitId exist = null;
		try {
			for (final ACUser acUser : getAdminBroker().getUsers()) {
				if (acUser.getName().equalsIgnoreCase(userName)) {
					exist = acUser.getId();
					break;
				}
			}
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.ImportController_Insufficient_Access_Rights,
				Messages.ImportController_Not_Allowed_To_List_Users);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		return exist;
	}

	/**
	 * @return the admin broker.
	 */
	public AdminBroker getAdminBroker() {
		return adminBroker;
	}

	/**
	 * @param importSource
	 *            the import source, that should be used for the import.
	 */
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
	}

	/**
	 * @return the current import source.
	 */
	public ImportSource getImportSource() {
		return importSource;
	}

	/**
	 * @return a small title, that can be displayed e.g. in a GUI.
	 */
	public String getTitle() {
		// if importSource isn't initialized yet, return an empty string instead
		return null == importSource ? StringUtils.EMPTY : importSource.getLabel();
	}

	/**
	 * @return Returns a small message to describe where data currently gets
	 *         imported from.
	 */
	public String getMessage() {
		// if importSource isn't initialized yet, return an empty string instead
		return null == importSource ? StringUtils.EMPTY : importSource.getMessage();
	}
}
