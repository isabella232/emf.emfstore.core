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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.ReferenceChangeMutation.ReferenceChangeMode;
import org.junit.Test;

/**
 * Unit tests for {@link AttributeChangeMutation}.
 * 
 * @author Philip Langer
 */
public class ReferenceChangeMutationTest extends AbstractMutationTest {

	@Test
	public void addValueForGivenReferenceAndContainer() {
		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.ADD,
				getFirstEClass());
		mutation.setTargetObject(getSecondEClass());
		mutation.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		mutation.apply();

		assertTrue(getSecondEClass().getESuperTypes().contains(getFirstEClass()));
	}

	@Test
	public void removeValueFromGivenReferenceAndContainer() {
		getFirstEClass().getESuperTypes().add(getSecondEClass());

		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.DELETE);
		mutation.setTargetObject(getFirstEClass());
		mutation.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		mutation.apply();

		assertFalse(getFirstEClass().getESuperTypes().contains(getSecondEClass()));
	}

	@Test
	public void reorderValuesInGivenReferenceAndContainer() {
		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.REORDER);
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		final EObject firstEClass = getFirstEClass();
		final EObject secondEClass = getSecondEClass();
		mutation.apply();

		assertEquals(0, ePackageWithTwoClasses.getEClassifiers().indexOf(secondEClass));
		assertEquals(1, ePackageWithTwoClasses.getEClassifiers().indexOf(firstEClass));
	}

	@Test
	public void selectTargetContainerForGivenFeature() throws MutationException {
		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.ADD);
		mutation.setTargetFeature(E_PACKAGE.getEClass_ESuperTypes());
		mutation.apply();

		assertEquals(E_PACKAGE.getEClass(), mutation.getTargetObject().eClass());
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(mutation.getTargetObject()));
	}

	@Test
	public void selectTargetFeatureForGivenTargetContainer() throws MutationException {
		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.ADD);
		mutation.setTargetObject(getFirstEClass());
		mutation.apply();

		assertEquals(E_PACKAGE.getEClass_ESuperTypes(), mutation.getTargetFeature());
	}

	@Test
	public void unconfiguredAdd() {
		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.ADD);
		mutation.apply();

		assertTrue(mutation.getTargetObject() == getFirstEClass()
				|| mutation.getTargetObject() == getSecondEClass());
		assertEquals(E_PACKAGE.getEClass_ESuperTypes(), mutation.getTargetFeature());
	}

	@Test
	public void unconfiguredDelete() {
		ReferenceChangeMutation mutation = createReferenceChangeValueMutation(ReferenceChangeMode.DELETE);
		try {
			mutation.doApply();
			fail("Should have thrown an exception since there is no object to delete from cross-reference");
		} catch (MutationException e) {
		}

		getFirstEClass().getESuperTypes().add(getSecondEClass());
		mutation = createReferenceChangeValueMutation(ReferenceChangeMode.DELETE);
		try {
			mutation.doApply();
		} catch (MutationException e) {
			fail("Now we should have been able to find a cross-reference to remove from.");
		}

		assertEquals(getFirstEClass(), mutation.getTargetObject());
		assertEquals(E_PACKAGE.getEClass_ESuperTypes(), mutation.getTargetFeature());
	}

	private ReferenceChangeMutation createReferenceChangeValueMutation(final ReferenceChangeMode mode) {
		return createReferenceChangeValueMutation(mode, null);
	}

	private ReferenceChangeMutation createReferenceChangeValueMutation(final ReferenceChangeMode mode,
			final Object newValue) {
		ReferenceChangeMutation mutation = new ReferenceChangeMutation(utilForEPackageWithTwoClasses) {
			@Override
			protected EObject selectNewReferenceValue() throws MutationException {
				if (newValue != null) {
					return (EObject)newValue;
				} else {
					return super.selectNewReferenceValue();
				}
			}

			@Override
			protected ReferenceChangeMode getRandomChangeMode() {
				return mode;
			}
		};
		return mutation;
	}

	private EClass getFirstEClass() {
		return (EClass)ePackageWithTwoClasses.getEClassifiers().get(0);
	}

	private EClass getSecondEClass() {
		return (EClass)ePackageWithTwoClasses.getEClassifiers().get(1);
	}
}
