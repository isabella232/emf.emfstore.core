/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.ui.controllers;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIUndoLastOperationController;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests:
 * UndoLastOperationController
 * 
 * @author jfaltermeier
 * 
 */
public class UIUndoLastOperationControllerTest extends AbstractUIControllerTest {

	private ILogListener logListener;
	private Throwable exception;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.client.test.ui.controllers.AbstractUIControllerTest#testController()
	 */
	@Override
	@Test
	public void testController() throws ESException {
		assertEquals(0, getLocalProject().getModelElements().size());
		Add.toProject(getLocalProject(), Create.player());
		assertEquals(1, getLocalProject().getModelElements().size());
		undoLastOperation();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		logListener = new ILogListener() {
			public void logging(IStatus status, String plugin) {
				final Throwable ex = status.getException();
				if (ex != null) {
					exception = ex;
				}
			}
		};
		Platform.addLogListener(logListener);
	}

	private void undoLastOperation() {
		UIThreadRunnable.asyncExec(new VoidResult() {
			public void run() {
				final UIUndoLastOperationController undoLastOperationController = new UIUndoLastOperationController(
					getBot()
						.getDisplay().getActiveShell(),
					getLocalProject());
				undoLastOperationController.execute();
			}
		});

		getBot().waitUntil(new DefaultCondition() {
			// BEGIN SUPRESS CATCH EXCEPTION
			public boolean test() throws Exception {
				return getLocalProject().getModelElements().size() == 0;
			}

			// END SURPRESS CATCH EXCEPTION
			public String getFailureMessage() {
				return "Last command was not undone. ";
			}
		});

	}

	@After
	public void after() {
		if (exception != null) {
			fail(exception.getMessage());
		}
		Platform.removeLogListener(logListener);
	}
}
