/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.internal.diff;

import java.io.IOException;

import org.dom4j.DocumentException;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestConfig;
import org.eclipse.emf.emfstore.internal.fuzzy.emf.config.TestRun;
import org.junit.Test;

/**
 * Class used as JUnit plugin test to create diff reports from a
 * {@link org.eclipse.emf.emfstore.fuzzy.emf.diff.spi.TestRunProvider TestRunProvider}.
 *
 * @author Julian Sommerfeldt
 *
 */
public class CreateAllDiffs {

	/**
	 * Creates all test diffs from an {@link org.eclipse.emf.emfstore.fuzzy.emf.diff.spi.TestRunProvider
	 * TestRunProvider}.
	 */
	@Test
	public void createAllDiffs() {

		final DiffGenerator diffGenerator = new DiffGenerator();

		try {
			final HudsonTestRunProvider runProvider = new HudsonTestRunProvider();
			for (final TestConfig config : runProvider.getAllConfigs()) {
				runProvider.setConfig(config);
				final TestRun[] runs = runProvider.getTestRuns();
				diffGenerator.createDiff(runs[0], runs[1]);
			}
		} catch (final DocumentException e) {
			throw new RuntimeException(Messages.CouldNotCreateDiffs, e);
		} catch (final IOException e) {
			throw new RuntimeException(Messages.CouldNotCreateDiffs, e);
		}
	}
}
