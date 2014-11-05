/*******************************************************************************
 * Copyright (c) 2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.common.dsl;

import java.util.concurrent.Callable;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.server.model.ESLogMessage;
import org.eclipse.emf.emfstore.server.model.versionspec.ESBranchVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versionspec.ESTagVersionSpec;

public final class CreateAPI {

	private CreateAPI() {

	}

	public static ESPrimaryVersionSpec primaryVersionSpec(int identifier) {
		return Create.primaryVersionSpec(identifier).toAPI();
	}

	public static ESTagVersionSpec tagVersionSpec(String branchName, String tag) {
		return Create.tagVersionSpec(branchName, tag).toAPI();
	}

	public static ESLogMessage logMessage() {
		return Create.logMessage().toAPI();
	}

	public static ESBranchVersionSpec branchVersionSpec(String branch) {
		return Create.branchVersionSpec(branch).toAPI();
	}

	public static ESLocalProject project(final String name) {
		return RunESCommand.runWithResult(new Callable<ESLocalProject>() {
			public ESLocalProject call() throws Exception {
				return ESWorkspaceProvider.INSTANCE.getWorkspace().createLocalProject(name);
			}
		});
	}
}
