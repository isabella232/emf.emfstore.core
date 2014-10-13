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
package org.eclipse.emf.emfstore.fuzzy.emf.test;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.fuzzy.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.Annotations.Util;
import org.eclipse.emf.emfstore.fuzzy.ESFuzzyRunner;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.ESMutateUtil;
import org.eclipse.emf.emfstore.modelmutator.ESDefaultModelMutator;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ESFuzzyTest to run bigger {@link ESModelMutatorConfiguration}s.
 * 
 * @author Julian Sommerfeldt
 * 
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class BigMutationTest {

	@Data
	private EObject root;

	@Util
	private ESMutateUtil util;

	/***/
	@Test
	public void createModel() {
		final ESModelMutatorConfiguration config =
			new ESModelMutatorConfiguration(util.getEPackages(), root, 1L);
		config.setMinObjectsCount(util.getMinObjectsCount());
		ESDefaultModelMutator.changeModel(config);
		// TODO: no assert here..
		System.out.println(ESModelMutatorUtil.getAllObjectsCount(root));
	}
}
