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
import static org.junit.Assert.fail;

import java.util.ConcurrentModificationException;
import java.util.List;

import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.common.ESObserver;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.client.observers.OperationObserver;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall;
import org.eclipse.emf.emfstore.internal.common.observer.ObserverCall.Result;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.test.model.TestElement;
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
		final OperationObserver observer = createOperationObserver(new Runnable() {
			public void run() {
				operationExecuted[0] = true;
			}
		});
		ESWorkspaceProviderImpl.getObserverBus().register(observer);
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getLocalProject().getModelElements().add(Create.testElement());
			}
		});
		ESWorkspaceProviderImpl.getObserverBus().unregister(observer);
		assertTrue(operationExecuted[0]);
	}

	@Test
	public void removeObserver() {
		final boolean[] operationExecuted = new boolean[] { false };
		final OperationObserver observer = createOperationObserver(new Runnable() {
			public void run() {
				operationExecuted[0] = true;
			}
		});
		ESWorkspaceProviderImpl.getObserverBus().register(observer);
		ESWorkspaceProviderImpl.getObserverBus().unregister(observer);
		Add.toProject(getLocalProject(), Create.testElement());
		assertFalse(operationExecuted[0]);
	}

	// @Ignore
	@Test
	public void bug467578() {
		final OperationObserver observer = createOperationObserver(new Runnable() {
			public void run() {
				try {
					final A proxy = ESWorkspaceProviderImpl.getObserverBus().notify(A.class);
					proxy.foo(getLocalProject());
					final List<Result> results = ((ObserverCall) proxy).getObserverCallResults();
					if (results.get(0).exceptionOccurred()) {
						fail(results.get(0).getException().getMessage());
					}
				} catch (final ConcurrentModificationException ex) {
					fail(ex.getMessage());
				}
			}
		});
		final AImpl a = new AImpl();
		ESWorkspaceProviderImpl.getObserverBus().register(observer);
		ESWorkspaceProviderImpl.getObserverBus().register(a);
		Add.toProject(getLocalProject(), Create.testElement());
		ESWorkspaceProviderImpl.getObserverBus().unregister(observer);
		ESWorkspaceProviderImpl.getObserverBus().unregister(a);
	}

	public interface A extends ESObserver {
		void foo(ESLocalProject localProject);
	}

	public class AImpl implements A {
		public void foo(ESLocalProject localProject) {
			final TestElement testElement = (TestElement) localProject.getModelElements().get(0);
			new EMFStoreCommand() {
				@Override
				protected void doRun() {
					testElement.setName("foo"); //$NON-NLS-1$
				}
			}.run(false);
		}
	}

	private static OperationObserver createOperationObserver(final Runnable runnable) {
		return new OperationObserver() {

			public void operationExecuted(ProjectSpace projectSpace, AbstractOperation operation) {
				runnable.run();
			}

			public void operationUndone(ProjectSpace projectSpace, AbstractOperation operation) {

			}
		};
	}

}
