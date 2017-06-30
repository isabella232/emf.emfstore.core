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

import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.IS_CONTAINMENT_REFERENCE;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.hasMaxNumberOfContainments;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.isListWithSpecifiedSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.test.AbstractMutationTest;
import org.junit.Test;

/**
 * @author Philip Langer
 *
 */
public class MutationTargetSelectorTest extends AbstractMutationTest {

	@Test
	public void isSelectionValidForInvalidCombinationOfFeatureAndObject() {

		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEEnum_ELiterals());
		selector.setTargetObject(ePackageWithTwoClasses);
		assertFalse(selector.isValid());
	}

	@Test
	public void isSelectionValidForValidCombinationOfFeatureAndObject() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertTrue(selector.isValid());
	}

	@Test
	public void isSelectionValidForInvalidTargetFeaturePredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetFeaturePredicates().add(IS_CONTAINMENT_REFERENCE);
		selector.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertFalse(selector.isValid());
	}

	@Test
	public void isSelectionValidForInvalidTargetObjectPredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(1));
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		assertFalse(selector.isValid());
	}

	@Test
	public void isSelectionValidForValidTargetObjectButInvalidTargetFeaturePredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetFeaturePredicates().add(hasMaxNumberOfContainments(1));
		selector.getTargetFeaturePredicates().add(IS_CONTAINMENT_REFERENCE);
		selector.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertFalse(selector.isValid());
	}

	@Test
	public void isSelectionValidForInvalidTargetObjectButValidTargetFeaturePredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(0));
		selector.getTargetFeaturePredicates().add(IS_CONTAINMENT_REFERENCE);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		assertFalse(selector.isValid());
	}

	@Test
	public void isSelectionValidForValidTargetObjectPredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(0));
		selector.getTargetFeaturePredicates().add(IS_CONTAINMENT_REFERENCE);
		selector.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertTrue(selector.isValid());
	}

	@Test
	public void isSelectionValidForInvalidOriginalFeatureValuePredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		selector.getOriginalFeatureValuePredicates().add(isListWithSpecifiedSize(0));
		assertFalse(selector.isValid());
	}

	@Test
	public void isSelectionValidForValidOriginalFeatureValuePredicate() {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		selector.getOriginalFeatureValuePredicates().add(isListWithSpecifiedSize(2));
		assertTrue(selector.isValid());
	}

	@Test
	public void findingTargetObjectByFeature() throws ESMutationException {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());

		selector.doSelection();
		EObject targetObject = selector.getTargetObject();
		assertEquals(ePackageWithTwoClasses, targetObject);
		assertEquals(E_PACKAGE.getEPackage_EClassifiers(), selector.getTargetFeature());

		selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		selector.doSelection();
		targetObject = selector.getTargetObject();
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(targetObject));
		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), selector.getTargetFeature());
	}

	@Test
	public void findingTargetFeatureByObject() throws ESMutationException {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetObject(ePackageWithTwoClasses);

		selector.doSelection();
		final EStructuralFeature targetFeature = selector.getTargetFeature();
		assertTrue(ePackageWithTwoClasses.eClass().getEAllStructuralFeatures().contains(targetFeature));
		assertEquals(ePackageWithTwoClasses, selector.getTargetObject());
	}

	@Test
	public void findingTargetFeatureAndTargetObjectByPredicates() throws ESMutationException {
		final MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetFeaturePredicates().add(IS_CONTAINMENT_REFERENCE);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(0));

		selector.doSelection();
		final EObject targetObject = selector.getTargetObject();
		final EStructuralFeature targetReference = selector.getTargetFeature();
		assertTrue(IS_CONTAINMENT_REFERENCE.apply(targetReference));
		assertTrue(hasMaxNumberOfContainments(0).apply(targetObject));
	}

}
