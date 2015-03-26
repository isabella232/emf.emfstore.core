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
package org.eclipse.emf.emfstore.fuzzy.emf;

/**
 * Holds test configuration parameters.
 *
 * @author emueller
 * @since 2.0
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ESTestConfig {

	/**
	 * Returns the unique ID of this test configuration.
	 *
	 * @return the unique ID of this test configuration
	 */
	String getId();

	/**
	 * Returns the initial seed for the random generator.
	 *
	 * @return the initial seed for the random generator
	 */
	long getSeed();

	/**
	 * Specifies how many times the test should be executed.
	 *
	 * @return the number of test executions
	 */
	int getCount();

	/**
	 * Returns the configuration of the model mutator.
	 *
	 * @return the configuration of the model mutator
	 */
	ESMutatorConfig getMutatorConfig();

	/**
	 * Returns the class containing the test.
	 *
	 * @return the class containing the test
	 */
	Class<?> getTestClass();

}
