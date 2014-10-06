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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

/**
 * Unit tests for {@link AttributeChangeMutation}.
 * 
 * @author Philip Langer
 */
public class AttributeChangeMutationTest extends AbstractMutationTest {

	@Test
	public void addValueForGivenAttributeAndContainer() {
		AttributeChangeMutation mutation = createAddingAttributeChangeValueMutation("TEST");
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_NsURI());
		mutation.apply();

		assertEquals("TEST", ePackageWithTwoClasses.getNsURI());
	}

	@Test
	public void unsetSingleValuedAttributeForGivenAttributeAndContainer() {
		ePackageWithTwoClasses.setNsURI("TEST");
		AttributeChangeMutation mutation = createDeletingAttributeChangeValueMutation();
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_NsURI());
		mutation.apply();
		
		assertNull(ePackageWithTwoClasses.getNsURI());
		assertFalse(ePackageWithTwoClasses.eIsSet(E_PACKAGE.getEPackage_NsURI()));
	}

	@Test
	public void selectTargetContainerForGivenFeature() throws MutationException {
		AttributeChangeMutation mutation = createAddingAttributeChangeValueMutation();
		mutation.setTargetFeature(E_PACKAGE.getEClass_Abstract());
		mutation.apply();

		assertEquals(E_PACKAGE.getEClass(), mutation.getTargetObject().eClass());
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(mutation.getTargetObject()));
	}

	@Test
	public void selectTargetFeatureForGivenTargetContainer() throws MutationException {
		AttributeChangeMutation mutation = createAddingAttributeChangeValueMutation();
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.apply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EClass targetContainerClass = ePackageWithTwoClasses.eClass();
		final EList<EAttribute> allAttributes = targetContainerClass.getEAllAttributes();
		assertTrue(allAttributes.contains(targetFeature));
	}

	@Test
	public void addObject() {
		AttributeChangeMutation mutation = createAddingAttributeChangeValueMutation();
		mutation.apply();

		assertTrue(mutation.getTargetObject() != null);
		assertTrue(mutation.getTargetFeature() != null);
		assertTrue(mutation.getTargetFeature() instanceof EAttribute);
	}

	private AttributeChangeMutation createAddingAttributeChangeValueMutation() {
		AttributeChangeMutation mutation = new AttributeChangeMutation(utilForEPackageWithTwoClasses) {
			@Override
			protected AttributeChangeMode getRandomChangeMode() {
				return AttributeChangeMode.ADD;
			}
		};
		return mutation;
	}

	private AttributeChangeMutation createAddingAttributeChangeValueMutation(final Object newValue) {
		AttributeChangeMutation mutation = new AttributeChangeMutation(utilForEPackageWithTwoClasses) {
			@Override
			protected Object createNewValue(EAttribute eAttribute) {
				return newValue;
			}

			@Override
			protected AttributeChangeMode getRandomChangeMode() {
				return AttributeChangeMode.ADD;
			}
		};
		return mutation;
	}

	private AttributeChangeMutation createDeletingAttributeChangeValueMutation() {
		AttributeChangeMutation mutation = new AttributeChangeMutation(utilForEPackageWithTwoClasses) {
			@Override
			protected AttributeChangeMode getRandomChangeMode() {
				return AttributeChangeMode.DELETE;
			}
		};
		return mutation;
	}
}
