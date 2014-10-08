/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Zardosht Hodaie - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;

/**
 * This shows attributes of a ACGroup (name, description) and show a list of its
 * member OrgUnits. You can use drag and drop to drop a group or a user on
 * members list, and it will be added to members.
 * 
 * @author Hodaie
 */
public class GroupComposite extends PropertiesComposite {

	private ACGroup group;
	private final OrgUnitManagementGUI orgUnitMgmtGUI;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            parent
	 * @param style
	 *            style
	 * @param adminBroker
	 *            used to communicate with the server
	 * @param orgUnitMgmtGUI
	 *            used to find out what which tab is active, so that if needed
	 *            update its list viewer
	 */
	public GroupComposite(Composite parent, int style, AdminBroker adminBroker, OrgUnitManagementGUI orgUnitMgmtGUI) {
		super(parent, style, adminBroker);
		this.orgUnitMgmtGUI = orgUnitMgmtGUI;
		createControls();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeOrgUnit(ACOrgUnit orgUnit) {
		try {
			getAdminBroker().removeMember(group.getId(), orgUnit.getId());
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.GroupComposite_Insufficient_Access_Rights,
				Messages.GroupComposite_Not_Allowed_To_Remove_Member_From_Selected_Group);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
		getTableViewer().refresh();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addExistingOrgUnit(ACOrgUnit orgUnit) {
		if (orgUnit != null && !orgUnit.equals(group)) {
			try {
				getAdminBroker().addMember(group.getId(), orgUnit.getId());
			} catch (final AccessControlException e) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.GroupComposite_Insufficient_Access_Rights,
					Messages.GroupComposite_Not_Allowed_To_Add_Member_To_Selected_Group);
			} catch (final ESException ex) {
				EMFStoreMessageDialog.showExceptionDialog(ex);
			}
		}

		getTableViewer().refresh();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addNewOrgUnit() {
		for (final ACOrgUnit newMember : getNewMembers()) {
			try {
				getAdminBroker().addMember(group.getId(), newMember.getId());
			} catch (final AccessControlException e) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.GroupComposite_Insufficient_Access_Rights,
					Messages.GroupComposite_Not_Allowed_To_Add_Member_To_Selected_Group);
			} catch (final ESException ex) {
				EMFStoreMessageDialog.showExceptionDialog(ex);
			}
		}

		getTableViewer().refresh();
	}

	/**
	 * This will be used when adding a new member using add button. It shows a
	 * list of ACOrgUnits on the server.
	 * 
	 * @return selected elements
	 */
	private EList<ACOrgUnit> getNewMembers() {
		// 1. show a list of all AcOrgUnits that are not member of this group
		// (get list of all AcOrgUnits, remove those who take part in this
		// 2. return the selected ACOrgunits

		final Collection<ACOrgUnit> allOrgUnits = new BasicEList<ACOrgUnit>();
		final EList<ACOrgUnit> members = new BasicEList<ACOrgUnit>();
		try {
			allOrgUnits.addAll(getAdminBroker().getOrgUnits());
			allOrgUnits.removeAll(getAdminBroker().getMembers(group.getId()));
			if (allOrgUnits.contains(group)) {
				allOrgUnits.remove(group);
			}

			final Object[] result = showDialog(allOrgUnits, Messages.GroupComposite_Select_Member);

			for (int i = 0; i < result.length; i++) {
				if (result[i] instanceof ACOrgUnit) {
					members.add((ACOrgUnit) result[i]);
				}
			}
		} catch (final ESException e) {
			// ZH Auto-generated catch block
			e.printStackTrace();
		}
		return members;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.PropertiesComposite#getTabTitle()
	 */
	@Override
	protected String getTabTitle() {
		return Messages.GroupComposite_Members;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateControls(EObject input) {
		if (input instanceof ACGroup) {
			group = (ACGroup) input;
			getTxtName().setText(
				group.getName());
			getTxtDescription().setText(
				group.getDescription() == null ? StringUtils.EMPTY : group.getDescription());
			getTableViewer().setInput(group);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveOrgUnitAttributes() {
		if (getTxtName() == null || getTxtDescription() == null) {
			return;
		}
		if (group == null) {
			return;
		}
		if (!(group.getName().equals(getTxtName().getText()) && group.getDescription().equals(
			getTxtDescription().getText()))) {
			try {
				getAdminBroker().changeOrgUnit(group.getId(), getTxtName().getText(), getTxtDescription().getText());
				((Form) getParent().getParent()).setText(Messages.GroupComposite_Group + getTxtName().getText());
				orgUnitMgmtGUI.getActiveTabContent().getTableViewer().refresh();
			} catch (final ESException e) {
				EMFStoreMessageDialog.showExceptionDialog(e);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addDragNDropSupport() {

		// add drag support
		super.addDragNDropSupport();

		// add drop support
		final int ops = DND.DROP_COPY;
		final Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getTransfer() };
		final DropTargetListener dropListener = new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				if (PropertiesForm.getDragSource().equals(Messages.GroupComposite_Projects)) {
					event.detail = DND.DROP_NONE;
				} else {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (PropertiesForm.getDragNDropObject() != null) {
					if (PropertiesForm.getDragNDropObject() instanceof ACOrgUnit) {
						final ACOrgUnit orgUnit = (ACOrgUnit) PropertiesForm.getDragNDropObject();
						addExistingOrgUnit(orgUnit);
						PropertiesForm.setDragNDropObject(null);
						getTableViewer().refresh();
					}
				}
			}
		};
		getTableViewer().addDropSupport(ops, transfers, dropListener);

	}

} // GroupComposite
