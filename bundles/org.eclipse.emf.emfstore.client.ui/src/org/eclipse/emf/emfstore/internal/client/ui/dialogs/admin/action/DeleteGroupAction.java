/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action;

import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.Messages;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.PropertiesForm;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.viewers.TableViewer;

/**
 * Action for deleting a group.
 *
 * @author emueller
 *
 */
public class DeleteGroupAction extends DeleteOrgUnitAction {

	/**
	 * Creates the delete-user-action.
	 *
	 * @param adminBroker
	 *            the {@link AdminBroker} that actually deletes the user
	 * @param tableViewer
	 *            the {@link TableViewer} containing all the groups
	 * @param form
	 *            the {@link PropertiesForm} containing details about the current group
	 */
	public DeleteGroupAction(AdminBroker adminBroker, TableViewer tableViewer, PropertiesForm form) {
		super(Messages.GroupTabContent_Delete_Group, adminBroker, tableViewer, form);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.DeleteOrgUnitAction#deleteAction(org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit)
	 */
	@Override
	protected void deleteAction(ACOrgUnit orgUnit) throws AccessControlException, ESException {
		getAdminBroker().deleteGroup(orgUnit.getId());

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.DeleteOrgUnitAction#orgUnitName()
	 */
	@Override
	protected String orgUnitName() {
		return Messages.DeleteGroupAction_OrgUnitName;
	}

}
