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

import static org.junit.Assert.*;
import static org.eclipse.emf.emfstore.internal.modelmutator.mutation.MutationPredicates.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

/**
 * @author Philip Langer
 *
 */
public class MutationTargetSelectorTest extends AbstractMutationTest {

	@Test
	public void isSelectionValidForInvalidCombinationOfFeatureAndObject() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEEnum_ELiterals());
		selector.setTargetObject(ePackageWithTwoClasses);
		assertFalse(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForValidCombinationOfFeatureAndObject() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertTrue(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForInvalidTargetFeaturePredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetFeaturePredicates().add(isContainmentReference);
		selector.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertFalse(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForInvalidTargetObjectPredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(1));
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		assertFalse(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForValidTargetObjectButInvalidTargetFeaturePredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetFeaturePredicates().add(hasMaxNumberOfContainments(1));
		selector.getTargetFeaturePredicates().add(isContainmentReference);
		selector.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertFalse(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForInvalidTargetObjectButValidTargetFeaturePredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(0));
		selector.getTargetFeaturePredicates().add(isContainmentReference);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		assertFalse(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForValidTargetObjectPredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(0));
		selector.getTargetFeaturePredicates().add(isContainmentReference);
		selector.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		selector.setTargetObject(ePackageWithTwoClasses.getEClassifiers().get(0));
		assertTrue(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForInvalidOriginalFeatureValuePredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		selector.getOriginalFeatureValuePredicates().add(isListWithSpecifiedSize(0));
		assertFalse(selector.isValid());
	}
	
	@Test
	public void isSelectionValidForValidOriginalFeatureValuePredicate() {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		selector.setTargetObject(ePackageWithTwoClasses);
		selector.getOriginalFeatureValuePredicates().add(isListWithSpecifiedSize(2));
		assertTrue(selector.isValid());
	}
	
	@Test
	public void findingTargetObjectByFeature() throws MutationException {
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
	public void findingTargetFeatureByObject() throws MutationException {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.setTargetObject(ePackageWithTwoClasses);
		
		selector.doSelection();
		EStructuralFeature targetFeature = selector.getTargetFeature();
		assertTrue(ePackageWithTwoClasses.eClass().getEAllStructuralFeatures().contains(targetFeature));
		assertEquals(ePackageWithTwoClasses, selector.getTargetObject());
	}
	
	@Test
	public void findingTargetFeatureAndTargetObjectByPredicates() throws MutationException {
		MutationTargetSelector selector = new MutationTargetSelector(utilForEPackageWithTwoClasses);
		selector.getTargetFeaturePredicates().add(isContainmentReference);
		selector.getTargetObjectPredicates().add(hasMaxNumberOfContainments(0));
		
		selector.doSelection();
		EObject targetObject = selector.getTargetObject();
		EStructuralFeature targetReference = selector.getTargetFeature();
		assertTrue(isContainmentReference.apply(targetReference));
		assertTrue(hasMaxNumberOfContainments(0).apply(targetObject));
	}

}
