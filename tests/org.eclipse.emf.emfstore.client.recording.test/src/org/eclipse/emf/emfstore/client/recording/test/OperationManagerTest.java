/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.recording.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.observers.OperationObserver;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.junit.Test;

/**
 * Tests the functionality of the {@link org.eclipse.emf.emfstore.internal.client.model.impl.OperationManager
 * OperationManager}.
 * 
 * @author emueller
 */
public class OperationManagerTest extends ESTest {

	@Test
	public void addObserver() {
		final boolean[] operationExecuted = new boolean[] { false };
		getProjectSpace().getOperationManager().addOperationObserver(new OperationObserver() {

			public void operationUndone(AbstractOperation operation) {

			}

			public void operationExecuted(AbstractOperation operation) {
				operationExecuted[0] = true;
			}
		});
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getLocalProject().getModelElements().add(Create.testElement());
			}
		});
		assertTrue(operationExecuted[0]);
	}

	@Test
	public void removeObserver() {
		final boolean[] operationExecuted = new boolean[] { false };
		final OperationObserver operationObserver = createOperationObserver(operationExecuted);
		getProjectSpace().getOperationManager().addOperationObserver(operationObserver);
		getProjectSpace().getOperationManager().removeOperationListener(operationObserver);
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getLocalProject().getModelElements().add(Create.testElement());
			}
		});
		assertFalse(operationExecuted[0]);
	}

	private static OperationObserver createOperationObserver(final boolean[] operationExecutedFlag) {
		return new OperationObserver() {

			public void operationUndone(AbstractOperation operation) {

			}

			public void operationExecuted(AbstractOperation operation) {
				operationExecutedFlag[0] = true;
			}
		};
	}
}
