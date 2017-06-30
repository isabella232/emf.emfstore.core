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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.ESMutateUtil;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Util;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESDefaultModelMutator;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ESFuzzyTest to test the {@link ESDefaultModelMutator}.
 *
 * @author Julian Sommerfeldt
 *
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(ESEMFDataProvider.class)
public class MutatorTest {

	@Data
	private EObject obj;

	@Util
	private ESMutateUtil util;

	/**
	 * Tests if two generated models are equal.
	 *
	 * @throws SerializationException
	 */
	@Test
	public void compareTwoGeneratedProjects() throws SerializationException {

		final Project project1 = ModelFactory.eINSTANCE.createProject();
		final Project project2 = ModelFactory.eINSTANCE.createProject();
		ESDefaultModelMutator.generateModel(getConfig(project1));
		ESDefaultModelMutator.generateModel(getConfig(project2));

		ESDefaultModelMutator.changeModel(getConfig(project1));
		ESDefaultModelMutator.changeModel(getConfig(project2));

		EcoreUtil.equals(project1, project2);
	}

	private ESModelMutatorConfiguration getConfig(Project root) {
		final ESModelMutatorConfiguration mmc = new ESModelMutatorConfiguration(
			util.getEPackages(), root, util.getSeed());
		final Collection<EStructuralFeature> eStructuralFeaturesToIgnore = new HashSet<EStructuralFeature>();
		eStructuralFeaturesToIgnore
			.add(ModelPackage.Literals.PROJECT__CUT_ELEMENTS);
		mmc.seteStructuralFeaturesToIgnore(eStructuralFeaturesToIgnore);
		mmc.setMinObjectsCount(util.getMinObjectsCount());
		return mmc;
	}
}
