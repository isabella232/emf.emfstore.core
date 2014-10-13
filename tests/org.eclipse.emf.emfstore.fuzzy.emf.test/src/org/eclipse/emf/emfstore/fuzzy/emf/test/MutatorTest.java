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
import java.util.Iterator;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.fuzzy.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.Annotations.Util;
import org.eclipse.emf.emfstore.fuzzy.ESFuzzyRunner;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.ESMutateUtil;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.modelmutator.ESDefaultModelMutator;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.Assert;
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
	 */
	@Test
	public void compareTwoGeneratedProjects() {

		final Project project1 = ModelFactory.eINSTANCE.createProject();
		final Project project2 = ModelFactory.eINSTANCE.createProject();
		ESDefaultModelMutator.generateModel(getConfig(project1));
		ESDefaultModelMutator.generateModel(getConfig(project2));

		ESDefaultModelMutator.changeModel(getConfig(project1));
		ESDefaultModelMutator.changeModel(getConfig(project2));

		Iterator<EObject> project1Iterator = project1.getAllModelElements()
			.iterator();
		Iterator<EObject> project2Iterator = project2.getAllModelElements()
			.iterator();

		while (project1Iterator.hasNext()) {
			final EObject modelElement = project1Iterator.next();
			final ModelElementId modelElementId = project1
				.getModelElementId(modelElement);
			if (!project2.contains(modelElementId)) {
				failed(project1, project2);
			}
		}

		final TreeIterator<EObject> allContentsProject1 = project1.eAllContents();
		final TreeIterator<EObject> allContentsProject2 = project2.eAllContents();

		while (allContentsProject1.hasNext()) {
			if (!allContentsProject2.hasNext()) {
				failed(project1, project2);
			}
			final EObject modelElement = allContentsProject1.next();
			final ModelElementId modelElementId = project1
				.getModelElementId(modelElement);
			final EObject modelElement2 = allContentsProject2.next();
			final ModelElementId modelElementId2 = project2
				.getModelElementId(modelElement2);
			if (!modelElementId.equals(modelElementId2)) {
				failed(project1, project2);
			}
		}

		project1Iterator = project1.getAllModelElements().iterator();
		project2Iterator = project2.getAllModelElements().iterator();

		while (project1Iterator.hasNext()) {
			final EObject modelElement = project1Iterator.next();
			final ModelElementId modelElementId = project1
				.getModelElementId(modelElement);
			final ModelElementId modelElementId2 = project2
				.getModelElementId(project2Iterator.next());
			if (!modelElementId.equals(modelElementId2)) {
				failed(project1, project2);
			}
		}
	}

	private void failed(Project project1, Project project2) {
		util.saveEObject(project1, "original_project", true);
		util.saveEObject(project2, "own_project", true);
		Assert.assertTrue(false);
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
