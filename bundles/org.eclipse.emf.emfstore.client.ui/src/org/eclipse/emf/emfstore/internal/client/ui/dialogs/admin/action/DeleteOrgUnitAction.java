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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.PropertiesForm;
import org.eclipse.emf.emfstore.internal.server.ServerConfiguration;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.Role;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.roles.ServerAdmin;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Action for deleting a {@link ACOrgUnit}.
 *
 * @author emueller
 *
 */
public abstract class DeleteOrgUnitAction extends Action {

	private final TableViewer tableViewer;
	private final AdminBroker adminBroker;
	private final PropertiesForm form;

	/**
	 * Constructor.
	 *
	 * @param title
	 *            the action title
	 * @param adminBroker
	 *            the {@link AdminBroker} responsible for deleting the {@link ACOrgUnit}
	 * @param tableViewer
	 *            the {@link TableViewer} listing all available {@link ACOrgUnit}s
	 * @param form
	 *            the {@link PropertiesForm} that will display the details of the created {@link ACOrgUnit}
	 */
	protected DeleteOrgUnitAction(String title, AdminBroker adminBroker, TableViewer tableViewer, PropertiesForm form) {
		super(title);
		this.tableViewer = tableViewer;
		this.adminBroker = adminBroker;
		this.form = form;
	}

	private <T extends ACOrgUnit> List<T> toList(Iterator<T> iterator) {
		final List<T> ts = new ArrayList<T>();
		while (iterator.hasNext()) {

			final T t = iterator.next();

			if (t == null) {
				continue;
			}

			ts.add(t);
		}

		return ts;
	}

	private boolean askConfirmationMessage(Shell shell, List<ACOrgUnit> orgUnits) {

		final List<String> names = new ArrayList<String>();

		for (final ACOrgUnit orgUnit : orgUnits) {
			names.add(orgUnit.getName());
		}

		return MessageDialog.openQuestion(
			shell,
			confirmationMessageTitle(),
			prepareQuestion(names));
	}

	private String prepareQuestion(List<String> orgUnits) {
		if (orgUnits.size() == 1) {
			return MessageFormat.format(
				Messages.DeleteOrgUnitAction_ConfirmationMessage_Single, orgUnits.get(0), orgUnitName());
		}

		final StringBuilder builder = new StringBuilder(
			MessageFormat.format(
				Messages.DeleteOrgUnitAction_ConfirmationMessage_Many, orgUnitName()));
		builder.append(StringUtils.join(orgUnits, "\n")); //$NON-NLS-1$
		return builder.toString();
	}

	@Override
	public void run() {
		final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		final List<ACOrgUnit> orgUnits = toList(selection.iterator());

		if (!askConfirmationMessage(
			Display.getCurrent().getActiveShell(),
			orgUnits)) {

			return;
		}

		for (final ACOrgUnit acOrgUnit : orgUnits) {
			final String superUser = ServerConfiguration.getProperties().getProperty(
				ServerConfiguration.SUPER_USER,
				ServerConfiguration.SUPER_USER_DEFAULT);
			boolean isAdmin = false;
			for (final Iterator<Role> it = acOrgUnit.getRoles().iterator(); it.hasNext();) {
				final Role userRole = it.next();
				if (acOrgUnit.getName().compareTo(superUser) == 0 && userRole instanceof ServerAdmin) {
					isAdmin = true;
					break;
				}
			}
			final Display display = Display.getCurrent();
			if (isAdmin) {
				MessageDialog.openInformation(display.getActiveShell(),
					Messages.DeleteOrgUnitAction_IllegalDeleteionAttempt,
					Messages.DeleteOrgUnitAction_SuperCanNotBeDeleted);
			} else {
				try {
					deleteAction(acOrgUnit);
				} catch (final AccessControlException ex) {
					MessageDialog.openWarning(display.getActiveShell(),
						Messages.DeleteOrgUnitAction_InsufficientAccessRights,
						Messages.DeleteOrgUnitAction_OrgUnitCanNotBeDeleted + orgUnitName());
				} catch (final ESException ex) {
					EMFStoreMessageDialog.showExceptionDialog(ex);
				}
			}

			if (form.getCurrentInput() instanceof ACOrgUnit && form.getCurrentInput().equals(acOrgUnit)) {
				form.setInput(null);

			}
		}
		tableViewer.refresh();
	}

	/**
	 * @return the adminBroker
	 */
	public AdminBroker getAdminBroker() {
		return adminBroker;
	}

	/**
	 * Returns the title of the confirmation dialog.
	 *
	 * @return the title of the confirmation dialog.
	 */
	protected String confirmationMessageTitle() {
		return MessageFormat.format(Messages.DeleteOrgUnitAction_ConfirmationMessageTitle, orgUnitName());
	}

	/**
	 * Actually deletes the given {@link ACOrgUnit}.
	 *
	 * @param orgUnit
	 *            the {@link ACOrgUnit} to be deleted
	 * @throws AccessControlException
	 *             in case the deletion is not allowed
	 * @throws ESException
	 *             in case the deletion failed
	 */
	protected abstract void deleteAction(ACOrgUnit orgUnit) throws AccessControlException, ESException;

	/**
	 * Returns the name of the {@link ACOrgUnit} to be deleted.
	 *
	 * @return the name of the {@link ACOrgUnit} to be deleted
	 */
	protected abstract String orgUnitName();
}
