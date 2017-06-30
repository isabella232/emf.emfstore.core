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
package org.eclipse.emf.emfstore.internal.client.test.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for bug 428911. Saving should not cause an Exception.
 *
 * @author jfaltermeier
 *
 */
public class FeatureMapPersistenceTest extends ESTest {

	private Throwable exception;

	private final ILogListener logListener = new ILogListener() {
		public void logging(IStatus status, String plugin) {
			final Throwable ex = status.getException();
			if (ex != null) {
				exception = ex;
			}
		}
	};

	@Override
	@Before
	public void before() {
		super.before();
		Platform.addLogListener(logListener);
	}

	@Override
	@After
	public void after() {
		if (exception != null) {
			fail("Exception logged to console: " + exception);
		}
		Platform.removeLogListener(logListener);
		super.after();
	}

	@Ignore
	@Test
	public void testFeatureMapPersistence() {
		try {
			final TestElement testElement = RunESCommand.WithException.runWithResult(Exception.class,
				new Callable<TestElement>() {
					public TestElement call() throws Exception {
						final TestElement testElement = Create.testElement("parent");
						getLocalProject().getModelElements().add(testElement);
						return testElement;
					}
				});

			RunESCommand.WithException.run(Exception.class, new Callable<Void>() {
				public Void call() throws Exception {
					final TestElement child1 = Create.testElement("child1");
					testElement.getFeatureMapReferences1().add(child1);
					assertEquals(child1, testElement.getFeatureMapEntries().getValue(0));
					return null;
				}
			});

			RunESCommand.WithException.run(Exception.class, new Callable<Void>() {
				public Void call() throws Exception {
					getLocalProject().save();
					return null;
				}
			});
		}
		// BEGIN SUPRESS CATCH EXCEPTION
		catch (final Exception ex) {// END SUPRESS CATCH EXCEPTION
			fail(ex.getMessage());
		}

	}
}
