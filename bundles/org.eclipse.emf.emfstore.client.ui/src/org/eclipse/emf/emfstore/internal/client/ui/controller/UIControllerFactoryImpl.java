/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory;
import org.eclipse.emf.emfstore.server.model.versionspec.ESBranchVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.swt.widgets.Shell;

/**
 * Implementation of the UI controller factory.
 */
public final class UIControllerFactoryImpl implements ESUIControllerFactory {

	/**
	 * The instance.
	 */
	public static final UIControllerFactoryImpl INSTANCE = new UIControllerFactoryImpl();

	private UIControllerFactoryImpl() {
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#commitProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public ESPrimaryVersionSpec commitProject(Shell shell,
		ESLocalProject project) {
		return new UICommitProjectController(shell, project).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#createBranch(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESProject)
	 */
	public ESPrimaryVersionSpec createBranch(Shell shell, ESProject project) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#createBranch(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESProject,
	 *      org.eclipse.emf.emfstore.server.model.versionspec.ESBranchVersionSpec)
	 */
	public ESPrimaryVersionSpec createBranch(Shell shell, ESProject project,
		ESBranchVersionSpec branch) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#createLocalProject(org.eclipse.swt.widgets.Shell)
	 */
	public ESLocalProject createLocalProject(Shell shell) {
		return new UICreateLocalProjectController(shell).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#createLocalProject(org.eclipse.swt.widgets.Shell,
	 *      java.lang.String)
	 */
	public ESLocalProject createLocalProject(Shell shell, String name) {
		return new UICreateLocalProjectController(shell, name).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#createRemoteProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESUsersession)
	 */
	public ESRemoteProject createRemoteProject(Shell shell,
		ESUsersession usersession) {
		return new UICreateRemoteProjectController(shell, usersession)
			.execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#createRemoteProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESUsersession, java.lang.String)
	 */
	public ESRemoteProject createRemoteProject(Shell shell,
		ESUsersession usersession, String projectName) {
		return new UICreateRemoteProjectController(shell, usersession,
			projectName).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#deleteLocalProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public void deleteLocalProject(Shell shell, ESLocalProject project) {
		new UIDeleteProjectController(shell, project).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#deleteRemoteProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESRemoteProject, org.eclipse.emf.emfstore.client.ESUsersession)
	 */
	public void deleteRemoteProject(Shell shell, ESRemoteProject remoteProject,
		ESUsersession usersession) {
		new UIDeleteRemoteProjectController(shell, usersession, remoteProject)
			.execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#login(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESServer)
	 */
	public void login(Shell shell, ESServer server) {
		new UILoginSessionController(shell, server).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#logout(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESUsersession)
	 */
	public void logout(Shell shell, ESUsersession usersession) {
		new UILogoutSessionController(shell, usersession).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#mergeBranch(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public void mergeBranch(Shell shell, ESLocalProject project) {
		new UIMergeController(shell, project).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#registerEPackage(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESServer)
	 */
	public void registerEPackage(Shell shell, ESServer server) {
		new UIRegisterEPackageController(shell, server).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#removeServer(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESServer)
	 */
	public void removeServer(Shell shell, ESServer server) {
		new UIRemoveServerController(shell, server).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#shareProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public void shareProject(Shell shell, ESLocalProject project) {
		new UIShareProjectController(shell, project).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#showHistoryView(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public void showHistoryView(Shell shell, ESLocalProject project) {
		new UIShowHistoryController(shell, project).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#showHistoryView(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void showHistoryView(Shell shell, EObject eObject) {
		new UIShowHistoryController(shell, eObject).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#updateProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public ESPrimaryVersionSpec updateProject(Shell shell,
		ESLocalProject project) {
		return new UIUpdateProjectController(shell, project).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#updateProject(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec)
	 */
	public ESPrimaryVersionSpec updateProject(Shell shell,
		ESLocalProject project, ESVersionSpec version) {
		return new UIUpdateProjectController(shell, project, version).execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.ui.ESUIControllerFactory#updateProjectToVersion(org.eclipse.swt.widgets.Shell,
	 *      org.eclipse.emf.emfstore.client.ESLocalProject)
	 */
	public ESPrimaryVersionSpec updateProjectToVersion(Shell shell,
		ESLocalProject project) {
		return new UIUpdateProjectToVersionController(shell, project).execute();
	}

}
