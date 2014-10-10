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
package org.eclipse.emf.emfstore.modelmutator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.AttributeChangeMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESMutationFactory;
import org.eclipse.emf.emfstore.modelmutator.ESRandomChangeMode;
import org.eclipse.emf.emfstore.modelmutator.ESReferenceChangeMutation;
import org.junit.Test;

/**
 * Unit tests for {@link AttributeChangeMutation}.
 * 
 * @author Philip Langer
 */
public class ReferenceChangeMutationTest extends AbstractMutationTest {

	@Test
	public void addValueForGivenReferenceAndContainer() throws ESMutationException {
		
		ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.ADD)
			.setNewReferenceValue(getFirstEClass())
			.setTargetObject(getSecondEClass())
			.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes())
			.apply();
			
		assertTrue(getSecondEClass().getESuperTypes().contains(getFirstEClass()));
	}

	@Test
	public void removeValueFromGivenReferenceAndContainer() throws ESMutationException {
		
		getFirstEClass().getESuperTypes().add(getSecondEClass());
		
		ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.DELETE)
			.setTargetObject(getFirstEClass())
			.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes())
			.apply();

		assertFalse(getFirstEClass().getESuperTypes().contains(getSecondEClass()));
	}

	@Test
	public void reorderValuesInGivenReferenceAndContainer() throws ESMutationException {
		
		ESReferenceChangeMutation mutation = ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.REORDER)
			.setTargetObject(ePackageWithTwoClasses)
			.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		final EObject firstEClass = getFirstEClass();
		final EObject secondEClass = getSecondEClass();
		mutation.apply();

		assertEquals(0, ePackageWithTwoClasses.getEClassifiers().indexOf(secondEClass));
		assertEquals(1, ePackageWithTwoClasses.getEClassifiers().indexOf(firstEClass));
	}

	@Test
	public void selectTargetContainerForGivenFeature() throws ESMutationException {
		
		ESReferenceChangeMutation mutation = ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.ADD)
			.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		
		mutation.apply();

		assertEquals(E_PACKAGE.getEClass(), mutation.getTargetObject().eClass());
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(mutation.getTargetObject()));
	}

	@Test
	public void selectTargetFeatureForGivenTargetContainer() throws ESMutationException {
		
		ESReferenceChangeMutation mutation = ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.ADD)
			.setTargetObject(getFirstEClass());
		
		mutation.apply();

		assertEquals(E_PACKAGE.getEClass_ESuperTypes(), mutation.getTargetFeature());
	}

	@Test
	public void unconfiguredAdd() throws ESMutationException {
		
		ESReferenceChangeMutation mutation = ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.ADD);
		
		mutation.apply();

		assertTrue(mutation.getTargetObject() == getFirstEClass()
				|| mutation.getTargetObject() == getSecondEClass());
		assertEquals(E_PACKAGE.getEClass_ESuperTypes(), mutation.getTargetFeature());
	}

	@Test(expected=ESMutationException.class)
	public void unconfiguredDelete() throws ESMutationException {
		
		ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.DELETE)
			.apply();
		
		fail("Should have thrown an exception since there is no object to delete from cross-reference");
	}
	
	@Test
	public void unconfiguredDelete_2() throws ESMutationException {
		getFirstEClass().getESuperTypes().add(getSecondEClass());
		
		ESReferenceChangeMutation mutation = ESMutationFactory.referenceChange(utilForEPackageWithTwoClasses)
			.setRandomChangeMode(ESRandomChangeMode.DELETE);
		
		mutation.apply();

		assertEquals(getFirstEClass(), mutation.getTargetObject());
		assertEquals(E_PACKAGE.getEClass_ESuperTypes(), mutation.getTargetFeature());
	}

	private EClass getFirstEClass() {
		return (EClass)ePackageWithTwoClasses.getEClassifiers().get(0);
	}

	private EClass getSecondEClass() {
		return (EClass)ePackageWithTwoClasses.getEClassifiers().get(1);
	}
}
