/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Eugen Neufeld - initial API and implementation
 * Edgar Mueller - API annotations
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIControllerFactoryImpl;
import org.eclipse.emf.emfstore.server.model.versionspec.ESBranchVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESVersionSpec;
import org.eclipse.swt.widgets.Shell;

/**
 * UI Controller factory.
 * 
 * @author eneufeld
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESUIControllerFactory {

	/**
	 * The instance.
	 */
	ESUIControllerFactory INSTANCE = UIControllerFactoryImpl.INSTANCE;

	/**
	 * Commits the changes of a project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project which changes are to be committed
	 * @return the version spec of the commit
	 */
	ESPrimaryVersionSpec commitProject(Shell shell, ESLocalProject project);

	/**
	 * Creates a new branch.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project for which the branch shall be created
	 * @return the version spec of the branch
	 */
	ESPrimaryVersionSpec createBranch(Shell shell, ESProject project);

	/**
	 * Creates a new branch.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project for which the branch shall be created
	 * @param branch the branch to be committed
	 * @return the version spec of the branch
	 */
	ESPrimaryVersionSpec createBranch(Shell shell, ESProject project, ESBranchVersionSpec branch);

	/**
	 * Create a new local project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @return the project
	 */
	ESLocalProject createLocalProject(Shell shell);

	/**
	 * Create a new local project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param name the name of the project
	 * @return the project
	 */
	ESLocalProject createLocalProject(Shell shell, String name);

	/**
	 * Create a new remote project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param usersession the session to be used to create the project
	 * @return the project
	 */
	ESRemoteProject createRemoteProject(Shell shell, ESUsersession usersession);

	/**
	 * Create a new remote project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param usersession the session to be used to create the project
	 * @param projectName the name of the project
	 * @return the project
	 */
	ESRemoteProject createRemoteProject(Shell shell, ESUsersession usersession, String projectName);

	/**
	 * Deletes a local project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project to be deleted
	 */
	void deleteLocalProject(Shell shell, ESLocalProject project);

	/**
	 * Deletes a remote project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param remoteProject the project to be deleted
	 * @param usersession the session to be used to create the project
	 */
	void deleteRemoteProject(Shell shell, ESRemoteProject remoteProject, ESUsersession usersession);

	/**
	 * Login to the server.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param server the server to login to
	 */
	void login(Shell shell, ESServer server);

	/**
	 * Log out a usersession.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param usersession the usersession to end
	 */
	void logout(Shell shell, ESUsersession usersession);

	/**
	 * Merge a branch into the project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project to merge
	 */
	void mergeBranch(Shell shell, ESLocalProject project);

	/**
	 * Register a new {@link EPackage} at the server.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param server the server at which the package should be registered
	 */
	void registerEPackage(Shell shell, ESServer server);

	/**
	 * Remove a server from the workspace.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param server the server to remove
	 */
	void removeServer(Shell shell, ESServer server);

	/**
	 * Share a project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project to share
	 */
	void shareProject(Shell shell, ESLocalProject project);

	/**
	 * Display the history view.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project for which the history is to be displayed
	 */
	void showHistoryView(Shell shell, ESLocalProject project);

	/**
	 * Display the history view.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param eObject the eObject for which the history is to be displayed
	 */
	void showHistoryView(Shell shell, EObject eObject);

	/**
	 * Update a project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project to update
	 * @return the version spec of the updated project
	 */
	ESPrimaryVersionSpec updateProject(Shell shell, ESLocalProject project);

	/**
	 * Update a project.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project to update
	 * @param version the version to update to
	 * @return the version spec of the updated project
	 */
	ESPrimaryVersionSpec updateProject(Shell shell, ESLocalProject project, ESVersionSpec version);

	/**
	 * Update a project to a specific version.
	 * 
	 * @param shell the parent {@link Shell} that should be used
	 * @param project the project to update
	 * @return the version spec of the updated project
	 */
	ESPrimaryVersionSpec updateProjectToVersion(Shell shell, ESLocalProject project);
}
