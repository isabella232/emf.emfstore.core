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
package org.eclipse.emf.emfstore.fuzzy.emf.test;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that verifies the configuration parameters read from the fuzzy config.
 * 
 * @author emueller
 * 
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class FuzzyProjectConfigTest extends FuzzyProjectTest {

	@Test
	public void createModel() {
		final Set<EObject> allModelElements = getProjectSpace().getProject().getAllModelElements();
		assertEquals(100, allModelElements.size());
	}

}
