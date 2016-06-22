/*******************************************************************************
 * Copyright (c) 2011-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import org.eclipse.emf.emfstore.client.ESServer;
import org.eclipse.emf.emfstore.client.ESWorkspaceProvider;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIRemoveServerController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

/**
 * SWTBot test for {@link UIRemoveServerController}.
 *
 * @author emueller
 *
 */
public class UIRemoveServerControllerTest extends AbstractUIControllerTest {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.test.ui.controllers.AbstractUIControllerTest#testController()
	 */
	@Override
	public void testController() throws ESException {
		// share project in order to have one active project
		ProjectUtil.share(getUsersession(), getLocalProject());
		// removal should fail
		removeServer();
	}

	private void removeServer() {
		final int howManyServers = ESWorkspaceProvider.INSTANCE.getWorkspace().getServers().size();
		final ESServer serverToBeRemoved = ESWorkspaceProvider.INSTANCE.getWorkspace().getServers()
			.get(howManyServers - 1);
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIRemoveServerController removeServerController = new UIRemoveServerController(
					getBot().getDisplay().getActiveShell(),
					serverToBeRemoved);
				removeServerController.execute();
			}
		});
		getBot().button("Yes").click();
		// expect warning dialog that can be confirmed by clicking OK
		getBot().button("OK").click();
	}
}
