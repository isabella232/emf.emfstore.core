/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * deser
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.wizard;

import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author deser
 */
public class AcUserImportAction extends Action {

	private final AdminBroker broker;

	/**
	 * @param broker
	 *            The admin broker which is needed for the wizard.
	 */
	public AcUserImportAction(AdminBroker broker) {
		super(Messages.AcUserImportAction_ImportUserOrGroup);
		this.broker = broker;
		setImageDescriptor(Activator.getImageDescriptor("icons/importuser.png")); //$NON-NLS-1$
		setToolTipText(Messages.AcUserImportAction_ImportUserOrGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		final AcUserImportWizard wizard = new AcUserImportWizard(broker);
		final WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.create();
		dialog.open();

		// We assume that every import was successful, because this is just used
		// to update views.
		notifyResult(true);
	}

}
