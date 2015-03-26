/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESRemoteProjectImpl;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UICheckoutController;
import org.eclipse.emf.emfstore.internal.client.ui.views.historybrowserview.HistoryBrowserView;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.HistoryInfo;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Handler for checking out a specific revision of a project.<br/>
 * It is assumed that the user previously has selected a {@link HistoryInfo} instance.
 *
 * @author emueller
 *
 */
public class CheckoutRevisionHandler extends AbstractEMFStoreHandler {

	@Override
	public void handle() {

		final HistoryInfo historyInfo = requireSelection(HistoryInfo.class);
		final PrimaryVersionSpec versionSpec = ModelUtil.clone(historyInfo.getPrimarySpec());

		// TODO: remove HistoryBrowserView
		final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null) {
			return;
		}

		if (!(activePage.getActivePart() instanceof HistoryBrowserView)) {
			return;
		}

		final HistoryBrowserView view = (HistoryBrowserView) activePage.getActivePart();

		ESRemoteProjectImpl remoteProject = null;
		try {
			remoteProject = view.getProjectSpace().toAPI().getRemoteProject();
		} catch (final ESException e) {
			// TODO: OTS
		}

		new UICheckoutController(getShell(), versionSpec.toAPI(), remoteProject).execute();
	}

}
