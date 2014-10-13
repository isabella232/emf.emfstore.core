/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Edgar - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.test;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.fuzzy.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.ESFuzzyRunner;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Edgar
 * 
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class FuzzyProjectConfigTest extends FuzzyProjectTest {

	@Data
	private Project project;

	@Test
	public void createModel() {
		final Set<EObject> allModelElements = project.getAllModelElements();
		assertEquals(100, allModelElements.size());
	}

}
