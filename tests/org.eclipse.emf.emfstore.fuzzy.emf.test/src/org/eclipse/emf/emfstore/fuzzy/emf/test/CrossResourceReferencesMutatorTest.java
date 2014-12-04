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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.fuzzy.emf.ESEMFDataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.ESMutateUtil;
import org.eclipse.emf.emfstore.fuzzy.emf.ESPredicates;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Data;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.DataProvider;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Mutator;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.Annotations.Util;
import org.eclipse.emf.emfstore.fuzzy.emf.junit.ESFuzzyRunner;
import org.eclipse.emf.emfstore.internal.common.model.ModelPackage;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.modelmutator.ESCrossResourceReferencesModelMutator;
import org.eclipse.emf.emfstore.modelmutator.ESModelMutatorConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * ESFuzzyTest to test the {@link ESCrossResourceReferencesModelMutator}.
 *
 * @author emueller
 *
 */
@RunWith(ESFuzzyRunner.class)
@DataProvider(value = ESEMFDataProvider.class)
public class CrossResourceReferencesMutatorTest {

	@Data
	private Project project;

	@Util
	private ESMutateUtil util;

	@Mutator
	private ESCrossResourceReferencesModelMutator mutator;

	@Test
	public void createsCrossResourceReferences() {

		mutator.mutateUntil(ESPredicates.hasExternalReference());
		final List<Resource> resources = mutator.getResourceSet().getResources();

		final int allObjectsCount = computeSize(project.eAllContents());

		final Resource resource1 = resources.get(0);
		final Resource resource2 = resources.get(1);

		final int firstResourceContents = computeSize(resource1.getAllContents());
		final int secondResourceContents = computeSize(resource2.getAllContents());

		System.out.println(getConfig(project).getMinObjectsCount() + " vs. " + allObjectsCount);

		assertTrue(secondResourceContents > 0);
		assertEquals(2, resources.size());
		assertEquals(allObjectsCount + 1, firstResourceContents);
		assertTrue(ESPredicates.hasExternalReference().apply(project));
	}

	@Test
	public void createsCrossResourceReferencesWithTwoResourceSets() {

		final Project clonedProject = EcoreUtil.copy(project);

		final ESCrossResourceReferencesModelMutator mutator =
			new ESCrossResourceReferencesModelMutator(getConfig(project));
		final ESCrossResourceReferencesModelMutator secondMutator =
			new ESCrossResourceReferencesModelMutator(getConfig(clonedProject));

		mutator.mutateUntil(ESPredicates.hasExternalReference());
		secondMutator.mutateUntil(ESPredicates.hasExternalReference());

		EcoreUtil.equals(project, clonedProject);
	}

	private static int computeSize(final TreeIterator<EObject> allContents) {
		int allObjectsCount = 0;
		while (allContents.hasNext()) {
			allContents.next();
			allObjectsCount++;
		}
		return allObjectsCount;
	}

	private ESModelMutatorConfiguration getConfig(Project root) {
		final ESModelMutatorConfiguration mmc = new ESModelMutatorConfiguration(
			util.getEPackages(), root, util.getSeed());
		final Collection<EStructuralFeature> eStructuralFeaturesToIgnore = new HashSet<EStructuralFeature>();
		eStructuralFeaturesToIgnore
			.add(ModelPackage.Literals.PROJECT__CUT_ELEMENTS);
		mmc.seteStructuralFeaturesToIgnore(eStructuralFeaturesToIgnore);
		mmc.setMinObjectsCount(util.getMinObjectsCount());
		mmc.setDoNotGenerateRoot(true);
		return mmc;
	}
}
