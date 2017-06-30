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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.MoveObjectMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMoveObjectMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESMutationFactory;
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
		final EAttribute eAttributeToAdd = E_FACTORY.createEAttribute();
		final EClass firstEClass = getFirstEClass();
		firstEClass.getEStructuralFeatures().add(eAttributeToAdd);
	}

	private EClass getFirstEClass() {
		return (EClass) ePackageWithTwoClasses.getEClassifiers().get(0);
	}

	private EAttribute getEAttributeInFirstClass() {
		return getFirstEAttribute(getFirstEClass());
	}

	private EClass getSecondEClass() {
		return (EClass) ePackageWithTwoClasses.getEClassifiers().get(1);
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
	public void moveObjectForGivenSourceFeatureAndSourceContainerAndTargetFeatureAndTargetContainer()
		throws ESMutationException {

		final EAttribute attributeToMove = getEAttributeInFirstClass();

		ESMutationFactory.move(utilForEPackageWithTwoClasses).setSourceObject(getFirstEClass())
			.setSourceFeature(E_PACKAGE.getEClass_EStructuralFeatures()).setTargetObject(getSecondEClass())
			.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures()).setEObjectToMove(attributeToMove).apply();

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
			} catch (final Exception e) {
				if (tries++ > 3) {
					fail();
				}
			}
		}
	}

	private void applyUnconfigeredMove() throws ESMutationException {

		ESMutationFactory.move(utilForEPackageWithTwoClasses).apply();

		assertEAttributeInFirstClassHasBeenMoved();
	}

	@Test
	public void moveObjectForGivenFeature() throws ESMutationException {

		ESMutationFactory.move(utilForEPackageWithTwoClasses)
			.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures()).apply();

		assertEAttributeInFirstClassHasBeenMoved();
	}

	private void assertEAttributeInFirstClassHasBeenMoved() {
		final EObject eAttributeInFirstClass = getEAttributeInFirstClass();
		final EObject eAttributeInSecondClass = getEAttributeInSecondClass();
		assertNull(eAttributeInFirstClass);
		assertTrue("Attribute has not been moved", eAttributeInSecondClass != null);
	}

	@Test
	public void setupForSourceGivenFeature() throws ESMutationException {
		final EAttribute eAttribute = getEAttributeInFirstClass();

		final ESMoveObjectMutation mutation = ESMutationFactory.move(utilForEPackageWithTwoClasses)
			.setSourceFeature(E_PACKAGE.getEClass_EStructuralFeatures());

		mutation.apply();

		assertEquals(getFirstEClass(), mutation.getSourceObject());
		assertEquals(eAttribute, mutation.getEObjectToMove());
		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), mutation.getTargetFeature());
		assertTrue(mutation.getTargetObject() == getSecondEClass());
	}

	@Test
	public void setupForGivenTargetContainer() throws ESMutationException {
		final EAttribute eAttribute = getEAttributeInFirstClass();

		final ESMoveObjectMutation mutation = ESMutationFactory.move(utilForEPackageWithTwoClasses)
			.setTargetObject(getSecondEClass());

		mutation.apply();

		assertEquals(getFirstEClass(), mutation.getSourceObject());
		assertEquals(eAttribute, mutation.getEObjectToMove());
		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), mutation.getTargetFeature());
		assertEquals(getSecondEClass(), eAttribute.eContainer());
	}

	@Test(expected = ESMutationException.class)
	public void throwsExceptionIfNoValidObjectToMoveIsAvailable() throws ESMutationException {

		ESMutationFactory.move(utilForEPackageWithTwoClasses).setTargetFeature(E_PACKAGE.getEEnum_ELiterals()).apply();

		fail("Should have thrown a Mutation Exception, because there is no valid setup.");
	}

	@Test(expected = ESMutationException.class)
	public void throwsExceptionIfNoValidTargetContainerIsAvailable() throws ESMutationException {

		ESMutationFactory.move(utilForEPackageWithTwoClasses).setTargetFeature(E_PACKAGE.getEPackage_EClassifiers())
			.apply();

		fail("Should have thrown a Mutation Exception, because there is no valid setup.");
	}
}
