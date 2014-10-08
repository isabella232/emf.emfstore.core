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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link MoveObjectMutation}.
 * 
 * @author Philip Langer
 */
public class MoveObjectMutationTest extends AbstractMutationTest {

	@Before
	public void addEAttributeToFirstEClassInEPackageWithTwoClasses() {
		EAttribute eAttributeToAdd = E_FACTORY.createEAttribute();
		EClass firstEClass = getFirstEClass();
		firstEClass.getEStructuralFeatures().add(eAttributeToAdd);
	}

	private EClass getFirstEClass() {
		return (EClass)ePackageWithTwoClasses.getEClassifiers().get(0);
	}

	private EAttribute getEAttributeInFirstClass() {
		return getFirstEAttribute(getFirstEClass());
	}

	private EClass getSecondEClass() {
		return (EClass)ePackageWithTwoClasses.getEClassifiers().get(1);
	}

	private EAttribute getEAttributeInSecondClass() {
		return getFirstEAttribute(getSecondEClass());
	}

	private EAttribute getFirstEAttribute(EClass eClass) {
		if (eClass.getEAttributes().isEmpty()) {
			return null;
		}
		return eClass.getEAttributes().get(0);
	}

	@Test
	public void moveObjectForGivenSourceFeatureAndSourceContainerAndTargetFeatureAndTargetContainer() {
		EAttribute attributeToMove = getEAttributeInFirstClass();

		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setSourceObject(getFirstEClass());
		mutation.setSourceFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		mutation.setTargetObject(getSecondEClass());
		mutation.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		mutation.setEObjectToMove(attributeToMove);
		mutation.apply();

		assertEquals(getSecondEClass(), attributeToMove.eContainer());
	}

	@Test
	public void moveObject() {
		int tries = 0;
		boolean success = false;
		while (!success) {
			try {
				applyUnconfigeredMove();
				success = true;
			} catch (Exception e) {
				if (tries++ > 3)
					fail();
			}
		}
	}

	private void applyUnconfigeredMove() throws MutationException {
		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.doApply();
		assertEAttributeInFirstClassHasBeenMoved();
	}

	@Test
	public void moveObjectForGivenFeature() {
		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		mutation.apply();

		assertEAttributeInFirstClassHasBeenMoved();
	}

	private void assertEAttributeInFirstClassHasBeenMoved() {
		final EObject eAttributeInFirstClass = getEAttributeInFirstClass();
		final EObject eAttributeInSecondClass = getEAttributeInSecondClass();
		assertNull(eAttributeInFirstClass);
		assertTrue("Attribute has not been moved", eAttributeInSecondClass != null);
	}

	@Test
	public void setupForSourceGivenFeature() throws MutationException {
		EAttribute eAttribute = getEAttributeInFirstClass();

		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setSourceFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		mutation.doApply();

		assertEquals(getFirstEClass(), mutation.getSourceObject());
		assertEquals(eAttribute, mutation.getEObjectToMove());
		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), mutation.getTargetFeature());
		assertTrue(mutation.getTargetObject() == getSecondEClass());
	}

	@Test
	public void setupForGivenTargetContainer() throws MutationException {
		EAttribute eAttribute = getEAttributeInFirstClass();

		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetObject(getSecondEClass());
		mutation.doApply();

		assertEquals(getFirstEClass(), mutation.getSourceObject());
		assertEquals(eAttribute, mutation.getEObjectToMove());
		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), mutation.getTargetFeature());
		assertEquals(getSecondEClass(), eAttribute.eContainer());
	}

	@Test
	public void throwsExceptionIfNoValidObjectToMoveIsAvailable() {
		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEEnum_ELiterals());

		try {
			mutation.doApply();
			fail("Should have thrown a Mutation Exception, because there is no valid setup.");
		} catch (MutationException e) {
			assertTrue(true);
		}
	}

	@Test
	public void throwsExceptionIfNoValidTargetContainerIsAvailable() {
		MoveObjectMutation mutation = new MoveObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());

		try {
			mutation.doApply();
			fail("Should have thrown a Mutation Exception, because there is no valid setup.");
		} catch (MutationException e) {
			assertTrue(true);
		}
	}

}
