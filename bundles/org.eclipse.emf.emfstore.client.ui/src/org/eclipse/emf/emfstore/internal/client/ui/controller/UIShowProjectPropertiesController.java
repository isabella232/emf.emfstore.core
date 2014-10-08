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
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESRemoteProjectImpl;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * UI controller for showing the properties of a project via a dialog.
 * 
 * @author emueller
 * 
 */
public class UIShowProjectPropertiesController extends AbstractEMFStoreUIController<Void> {

	private final ProjectInfo projectInfo;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the shell that will be used to open up the properties dialog
	 * @param projectInfo
	 *            the {@link ProjectInfo} info that is needed to determine for which
	 *            project the properties should be shown
	 */
	public UIShowProjectPropertiesController(Shell shell, ProjectInfo projectInfo) {
		super(shell);
		this.projectInfo = projectInfo;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor progressMonitor) throws ESException {
		String revision = Messages.UIShowProjectPropertiesController_UnknownTag;
		ESPrimaryVersionSpec versionSpec;

		try {
			final ServerInfo serverInfo = (ServerInfo) projectInfo.eContainer();
			final ESRemoteProject remoteProjectImpl = new ESRemoteProjectImpl(serverInfo, projectInfo);
			// TODO: monitor
			versionSpec = remoteProjectImpl.resolveVersionSpec(ESVersionSpec.FACTORY.createHEAD(),
				new NullProgressMonitor());
			revision = Integer.toString(versionSpec.getIdentifier());
		} catch (final ESException e) {
			// do nothing
		}

		final String rev = revision;

		MessageDialog.openInformation(getShell(),
			Messages.UIShowProjectPropertiesController_ProjectInformation,
			MessageFormat.format(Messages.UIShowProjectPropertiesController_CurrentRevision,
				rev,
				projectInfo.getProjectId().getId()));
		return null;
	}
}
