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
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.ESRemoteProject;
import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESUsersession;
import org.eclipse.emf.emfstore.client.ESWorkspace;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.exceptions.ESServerNotFoundException;
import org.eclipse.emf.emfstore.client.test.ui.AllUITestsWithMock;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESServerImpl;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESUsersessionImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommandWithException;
import org.eclipse.emf.emfstore.internal.server.exceptions.FatalESException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public abstract class AbstractUIControllerTest extends SWTBotTestCase {

	private final SWTWorkbenchBot bot = new SWTWorkbenchBot();

	private final ESWorkspace workspace = ESWorkspaceProvider.INSTANCE.getWorkspace();
	private ESLocalProject localProject;
	private ESLocalProject checkedoutCopy;
	private ESServer server;
	private ESUsersession usersession;

	@Override
	@Before
	public void setUp() throws Exception {
		server = ESServer.FACTORY.createServer("localhost", 8080, KeyStoreManager.DEFAULT_CERTIFICATE);
		server = ESWorkspaceProvider.INSTANCE.getWorkspace().addServer(server);
		try {
			usersession = server.login("super", "super");
		} catch (final ESException e) {
			fail(e.getMessage());
		}
		assertEquals(usersession, server.getLastUsersession());
		deleteLocalProjects();
		deleteRemoteProjects(usersession);

		localProject = workspace.createLocalProject("TestProject");
		localProject.shareProject(usersession, new NullProgressMonitor());
	}

	@Override
	@After
	public void tearDown() {

		final EMFStoreCommandWithException<ESException> cmd = new EMFStoreCommandWithException<ESException>() {
			@Override
			protected void doRun() {
				((ESServerImpl) server).toInternalAPI().setLastUsersession(null);
				((ESUsersessionImpl) usersession).setServer(null);
				// setUp might have failed
				if (usersession != null && usersession.isLoggedIn()) {
					try {
						usersession.logout();

						final Iterator<Usersession> iter = ESWorkspaceProviderImpl.getInstance().getWorkspace()
							.toInternalAPI()
							.getUsersessions().iterator();
						while (iter.hasNext()) {
							if (iter.next().getServerInfo() == ((ESServerImpl) server).toInternalAPI()) {
								iter.remove();
							}
						}
						ESWorkspaceProvider.INSTANCE.getWorkspace().removeServer(server);
					} catch (final ESException e) {
						setException(e);
					} catch (final ESServerNotFoundException e) {
						fail(e.getMessage());
					}
				}
			}
		};

		cmd.run(false);

		if (cmd.hasException()) {
			fail(cmd.getException().getMessage());
		}

		try {
			super.tearDown();
			// BEGIN SUPRESS CATCH EXCEPTION
		} catch (final Exception ex) {
			fail(ex.getMessage());
			// END SURPRESS CATCH EXCEPTION
		}
	}

	protected static void deleteLocalProjects() throws IOException, FatalESException, ESException {
		for (final ESLocalProject project : ESWorkspaceProvider.INSTANCE.getWorkspace().getLocalProjects()) {
			project.delete(new NullProgressMonitor());
		}
	}

	protected static void deleteRemoteProjects(ESUsersession usersession) throws IOException, FatalESException,
	ESException {
		for (final ESRemoteProject project : ESWorkspaceProvider.INSTANCE.getWorkspace().getServers().get(0)
			.getRemoteProjects(usersession)) {
			project.delete(usersession, new NullProgressMonitor());
		}
	}

	protected int timeout() {
		return AllUITestsWithMock.TIMEOUT;
	}

	@Test
	public abstract void testController() throws ESException;

	public SWTWorkbenchBot getBot() {
		return bot;
	}

	public ESLocalProject getLocalProject() {
		return localProject;
	}

	public ESLocalProject getCheckedoutCopy() {
		return checkedoutCopy;
	}

	public ESUsersession getUsersession() {
		return usersession;
	}

	public ESServer getServer() {
		return server;
	}

	public void setCheckedoutCopy(ESLocalProject localProject) {
		checkedoutCopy = localProject;
	}

	public void setUsersession(ESUsersession usersession) {
		this.usersession = usersession;
	}
}
