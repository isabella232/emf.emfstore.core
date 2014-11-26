/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
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

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.waitForShell;

import org.eclipse.emf.emfstore.bowling.Player;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIUpdateProjectToVersionController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests:
 * UpdateProjectController
 * CheckoutController
 * 
 * @author emueller
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class UIUpdateProjectToVersionControllerTest extends AbstractUIControllerTestWithCommit {

	@Override
	@Test
	public void testController() throws ESException {

		checkout();

		createPlayerAndCommit();
		updateToVersion();

		updateToVersionOnHead();

		final Player player = (Player) getCopy().getModelElements().get(0);
		assertEquals(PLAYER_NAME, player.getName());
	}

	protected void updateToVersionOnHead() {
		SWTBotPreferences.PLAYBACK_DELAY = 100;

		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIUpdateProjectToVersionController updateProjectController = new UIUpdateProjectToVersionController(
					getBot().getDisplay().getActiveShell(),
					getCopy());
				updateProjectController.execute();
			}
		});

		final Matcher<Shell> matcher = withText("No need to update");
		getBot().waitUntil(waitForShell(matcher));
		getBot().button("OK").click();
	}

}
