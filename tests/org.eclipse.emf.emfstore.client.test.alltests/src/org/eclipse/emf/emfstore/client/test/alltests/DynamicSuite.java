/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.alltests;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class DynamicSuite extends Suite {

	/**
	 * @param klass
	 * @throws InitializationError
	 */
	public DynamicSuite(Class<?> klass) throws InitializationError {
		super(klass, getTests());
	}

	private static Class<?>[] getTests() throws InitializationError {

		final Class<?>[] tests = new Class[1];
		try {
			tests[0] = Class.forName("org.eclipse.emf.emfstore.client.changetracking.test.AllChangeTrackingTests");
		} catch (final ClassNotFoundException ex) {
			throw new InitializationError(ex);
		}

		return tests;
	}

}