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
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.wizard.AcUserImportAction;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateGroupAction;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.DeleteGroupAction;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * @author gurcankarakoc, deser
 */
public class GroupTabContent extends TabContent implements IPropertyChangeListener {

	private static final String GROUP_ICON = "icons/Group.gif"; //$NON-NLS-1$
	private static final String DELETE_ICON = "icons/delete.gif"; //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param string
	 *            name of tab
	 * @param adminBroker
	 *            the {@link AdminBroker} that is needed to communicate with the server
	 * @param form
	 *            the form that is used to set input to properties form and update its table viewer
	 *            upon deletion of OrgUnits
	 */
	public GroupTabContent(String string, AdminBroker adminBroker, PropertiesForm form) {
		super(string, adminBroker, form);
		setTab(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.TabContent#initActions()
	 */
	@Override
	protected List<Action> initActions() {

		final Action createNewGroup = new CreateGroupAction(getAdminBroker(), getTableViewer(), getForm());
		createNewGroup.setImageDescriptor(Activator.getImageDescriptor(GROUP_ICON));
		createNewGroup.setToolTipText(Messages.GroupTabContent_Create_New_Group);

		final Action deleteGroup = new DeleteGroupAction(getAdminBroker(), getTableViewer(), getForm());
		deleteGroup.setImageDescriptor(Activator.getImageDescriptor(DELETE_ICON));
		deleteGroup.setToolTipText(Messages.GroupTabContent_Delete_Group);

		final Action importOrgUnit = new AcUserImportAction(getAdminBroker());
		importOrgUnit.addPropertyChangeListener(this);

		return Arrays.asList(createNewGroup, deleteGroup, importOrgUnit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITableLabelProvider getLabelProvider() {
		return new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return Activator.getImageDescriptor(GROUP_ICON).createImage();
			}

			public String getColumnText(Object element, int columnIndex) {
				return ((ACGroup) element).getName();
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStructuredContentProvider getContentProvider() {
		return new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				// return a list of Groups in project space
				final List<ACGroup> groups = new ArrayList<ACGroup>();
				try {
					groups.addAll(getAdminBroker().getGroups());
				} catch (final AccessControlException ex) {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.GroupTabContent_Insufficient_Access_Rights,
						Messages.GroupTabContent_Not_Allowed_To_List_Groups);
				} catch (final ESException ex) {
					EMFStoreMessageDialog.showExceptionDialog(ex);
				}
				return groups.toArray(new ACGroup[groups.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		};
	}

	/**
	 * Refresh the tableViewer after a property change. (Used e.g. after importing users via e.g. CSV.)
	 *
	 * @param event The event to deal with.
	 */
	public void propertyChange(PropertyChangeEvent event) {
		getTableViewer().refresh();
	}

}
