/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * JulianSommerfeldt
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.fuzzy.emf;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.AssertionFailedError;

import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.ConfigFactory;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestResult;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * {@link RunListener} used to create the report of a run of the {@link ESEMFDataProvider}.
 *
 * @author Julian Sommerfeldt
 *
 */
public class EMFRunListener extends RunListener {

	private final TestRun testRun;

	private final ESEMFDataProvider dataProvider;

	private TestResult testResult;

	private long testStartTime;

	private final String className;

	/**
	 * @param dataProvider
	 *            The {@link ESEMFDataProvider} containing the infos.
	 * @param testRun
	 *            The {@link TestRun} where to save the results.
	 */
	public EMFRunListener(ESEMFDataProvider dataProvider, TestRun testRun) {
		this.dataProvider = dataProvider;
		this.testRun = testRun;
		className = dataProvider.getConfig().getTestClass().getName();
	}

	@Override
	public void testRunFinished(Result result) {
		dataProvider.finish();
	}

	@Override
	public void testStarted(Description description) {
		if (filter(description)) {
			return;
		}
		testResult = ConfigFactory.eINSTANCE.createTestResult();
		testResult.setTestName(description.getMethodName().split(
			ESFuzzyRunner.NAME_SEPARATOR)[0]);
		testStartTime = System.currentTimeMillis();
	}

	@Override
	public void testFinished(Description description) {
		if (filter(description)) {
			return;
		}
		testResult.setExecutionTime(System.currentTimeMillis() - testStartTime);
		testResult.setSeedCount(dataProvider.getCurrentSeedCount());
		testRun.getResults().add(testResult);
	}

	@Override
	public void testFailure(Failure failure) {
		if (filter(failure.getDescription())) {
			return;
		}

		final Throwable throwable = failure.getException();
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		try {
			throwable.printStackTrace(new PrintWriter(sw));

			if (throwable instanceof AssertionFailedError) {
				testResult.setFailure(sw.toString());
			} else {
				testResult.setError(sw.toString());
			}
		} finally {
			try {
				sw.close();
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			pw.close();
		}
	}

	private boolean filter(Description description) {
		return !description.getClassName().equals(className);
	}
}
