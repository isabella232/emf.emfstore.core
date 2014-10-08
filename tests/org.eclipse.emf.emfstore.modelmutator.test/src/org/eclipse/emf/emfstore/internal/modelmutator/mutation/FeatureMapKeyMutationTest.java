/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Langer - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.modelmutator.mutation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.test.model.TestType;
import org.eclipse.emf.emfstore.test.model.TestmodelPackage;
import org.eclipse.emf.emfstore.test.model.TypeWithFeatureMapNonContainment;
import org.junit.Test;

/**
 * Unit tests for {@link FeatureMapKeyMutation}. TODO do some more testing to make sure delegation to other
 * mutations works fine
 * 
 * @author Philip Langer
 */
@SuppressWarnings("restriction")
public class FeatureMapKeyMutationTest extends AbstractMutationTest {

	private static final TestmodelPackage TEST_MODEL_PACKAGE = TestmodelPackage.eINSTANCE;

	@Test
	public void runUnconfiguredFeatureMapKeyMutation() {
		FeatureMapKeyMutation mutation = new FeatureMapKeyMutation(utilForTestTypeModel);
		assertTrue(mutation.apply());

		EStructuralFeature feature = mutation.getTargetFeature();
		assertTrue(feature == TEST_MODEL_PACKAGE.getTypeWithFeatureMapNonContainment_Map()
				|| feature == TEST_MODEL_PACKAGE.getTypeWithFeatureMapContainment_MapContainment());
	}

	@Test
	public void containmentFeatureMapKeyMutation() {
		FeatureMapKeyMutation mutation = new FeatureMapKeyMutation(utilForTestTypeModel);
		mutation.setTargetFeature(TEST_MODEL_PACKAGE.getTypeWithFeatureMapContainment_MapContainment());
		assertTrue(mutation.apply());

		final EList<TestType> firstKeyContainments = testTypeModel.getFirstKeyContainment();
		final EList<TestType> secondKeyContainments = testTypeModel.getSecondKeyContainment();

		// originally we had one in first key and one in second key containment
		// after the key change mutation, there should be two in either one of them
		assertTrue((firstKeyContainments.size() == 2 && secondKeyContainments.size() == 0)
				|| (firstKeyContainments.size() == 0 && secondKeyContainments.size() == 2));
	}

	@Test
	public void nonContainmentFeatureMapKeyMutation() {
		FeatureMapKeyMutation mutation = new FeatureMapKeyMutation(utilForTestTypeModel);
		mutation.setTargetFeature(TEST_MODEL_PACKAGE.getTypeWithFeatureMapNonContainment_Map());
		assertTrue(mutation.apply());

		final EList<TestType> firstKeyContainments = testTypeModel.getFirstKeyContainment();
		final TestType firstKeyContainmentsIdx0 = firstKeyContainments.get(0);
		final TypeWithFeatureMapNonContainment mutationTarget = (TypeWithFeatureMapNonContainment)firstKeyContainmentsIdx0;

		final EList<TestType> firstKeyValues = mutationTarget.getFirstKey();
		final EList<TestType> secondKeyValues = mutationTarget.getSecondKey();

		// originally we had one in first key and one in second key containment
		// after the key change mutation, there should be two in either one of them
		assertTrue((firstKeyValues.size() == 2 && secondKeyValues.size() == 0)
				|| (firstKeyValues.size() == 0 && secondKeyValues.size() == 2));
	}

	@Test
	public void getFeaturesOfFeatureMapGroupContainment() {
		FeatureMapKeyMutation mutation = new FeatureMapKeyMutation(utilForTestTypeModel);
		mutation.setTargetFeature(TEST_MODEL_PACKAGE.getTypeWithFeatureMapContainment_MapContainment());
		List<EStructuralFeature> featuresOfFeatureMapGroup = mutation.getFeaturesOfFeatureMapGroup();

		assertEquals(2, featuresOfFeatureMapGroup.size());
		assertTrue(featuresOfFeatureMapGroup.contains(TEST_MODEL_PACKAGE
				.getTypeWithFeatureMapContainment_FirstKeyContainment()));
		assertTrue(featuresOfFeatureMapGroup.contains(TEST_MODEL_PACKAGE
				.getTypeWithFeatureMapContainment_SecondKeyContainment()));
	}

	@Test
	public void getFeaturesOfFeatureMapGroupNonContainment() {
		FeatureMapKeyMutation mutation = new FeatureMapKeyMutation(utilForTestTypeModel);
		mutation.setTargetFeature(TEST_MODEL_PACKAGE.getTypeWithFeatureMapNonContainment_Map());
		List<EStructuralFeature> featuresOfFeatureMapGroup = mutation.getFeaturesOfFeatureMapGroup();

		assertEquals(2, featuresOfFeatureMapGroup.size());
		assertTrue(featuresOfFeatureMapGroup.contains(TEST_MODEL_PACKAGE
				.getTypeWithFeatureMapNonContainment_FirstKey()));
		assertTrue(featuresOfFeatureMapGroup.contains(TEST_MODEL_PACKAGE
				.getTypeWithFeatureMapNonContainment_SecondKey()));
	}

}
