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

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.PropertiesForm;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.viewers.TableViewer;

/**
 * Action for creating a group.
 *
 * @author emueller
 *
 */
public class CreateGroupAction extends CreateOrgUnitAction {

	private static final String FIELD_NAME = Messages.CreateGroupAction_GroupName_Field;

	/**
	 * Creates the create group action.
	 *
	 * @param adminBroker
	 *            the {@link AdminBroker} that actually creates the group
	 * @param tableViewer
	 *            the {@link TableViewer} containing all the groups
	 * @param form
	 *            the {@link PropertiesForm} containing group details
	 */
	public CreateGroupAction(AdminBroker adminBroker, TableViewer tableViewer, PropertiesForm form) {
		super(Messages.CreateGroupAction_ActionTitle, adminBroker, tableViewer, form);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#getInputFieldNames()
	 */
	@Override
	protected Set<String> getInputFieldNames() {
		final Set<String> fieldNames = new LinkedHashSet<String>();
		fieldNames.add(FIELD_NAME);
		return fieldNames;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#getPrimaryFieldName()
	 */
	@Override
	protected String getPrimaryFieldName() {
		return FIELD_NAME;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#orgUnitName()
	 */
	@Override
	protected String orgUnitName() {
		return Messages.CreateGroupAction_OrgUnitName;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws ESException
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.action.CreateOrgUnitAction#createOrgUnit(java.lang.String)
	 */
	@Override
	protected ACOrgUnitId createOrgUnit(Map<String, String> fieldValues) throws ESException {
		final String groupName = fieldValues.get(FIELD_NAME);

		if (StringUtils.isBlank(groupName)) {
			throw new ESException(Messages.CreateGroupAction_GroupName_Empty);
		}

		return getAdminBroker().createGroup(groupName);
	}
}
