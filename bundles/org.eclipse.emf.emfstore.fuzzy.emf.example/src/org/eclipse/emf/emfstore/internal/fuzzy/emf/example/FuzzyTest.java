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
package org.eclipse.emf.emfstore.internal.fuzzy.emf.example;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.fuzzy.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.ESFuzzyRunner;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Example test for the {@link ESFuzzyRunner}.
 * 
 * @author Julian Sommerfeldt
 * 
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class FuzzyTest {

	@Data
	private EObject root;

	/**
	 * ESFuzzyTest to check if the {@link ESFuzzyRunner} is working.
	 */
	@Test
	public void test() {
		Assert.assertNotNull(root);
	}
}
