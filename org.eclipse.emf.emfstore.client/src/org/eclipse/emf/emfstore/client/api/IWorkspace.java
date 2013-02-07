/*******************************************************************************
 * Copyright (c) 2012 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Otto von Wesendonk
 * Edgar Mueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.api;

import java.util.List;

/**
 * Container for all local projects and available servers.
 * 
 * @author emueller
 * @author wesendon
 */
public interface IWorkspace {

	/**
	 * Returns all local projects.
	 * 
	 * @return list of local projects
	 */
	List<? extends ILocalProject> getLocalProjects();

	/**
	 * Creates a new local project that is not shared with the server yet.
	 * 
	 * @param projectName
	 *            the project name
	 * @param projectDescription
	 *            the project description
	 * @return the project space that the new project resides in
	 */
	ILocalProject createLocalProject(String projectName, String projectDescription);

	/**
	 * Returns all available servers.
	 * 
	 * @return list of servers
	 */
	List<? extends IServer> getServers();

	/**
	 * Adds an server saves.
	 * 
	 * @param serverInfo
	 *            the server info to be added
	 */
	void addServer(IServer serverInfo);

	/**
	 * Removes an server saves.
	 * 
	 * @param serverInfo
	 *            the server info to be removed
	 */
	void removeServer(IServer serverInfo);
}