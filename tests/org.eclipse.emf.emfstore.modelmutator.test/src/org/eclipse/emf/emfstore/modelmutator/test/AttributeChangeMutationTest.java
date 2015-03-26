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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESAttributeChangeMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESMutationFactory;
import org.eclipse.emf.emfstore.modelmutator.ESRandomChangeMode;
import org.junit.Test;

/**
 * Unit tests for {@link ESAttributeChangeMutation}.
 * 
 * @author Philip Langer
 */
public class AttributeChangeMutationTest extends AbstractMutationTest {

	@Test
	public void addValueForGivenAttributeAndContainer()
			throws ESMutationException {

		ESMutationFactory.attributeChange(utilForEPackageWithTwoClasses)
				.setNewValue("TEST")
				.setRandomChangeMode(ESRandomChangeMode.ADD)
				.setTargetObject(ePackageWithTwoClasses)
				.setTargetFeature(E_PACKAGE.getEPackage_NsURI()).apply();

		assertEquals("TEST", ePackageWithTwoClasses.getNsURI());
	}

	@Test
	public void unsetSingleValuedAttributeForGivenAttributeAndContainer()
			throws ESMutationException {
		ePackageWithTwoClasses.setNsURI("TEST");

		ESMutationFactory.attributeChange(utilForEPackageWithTwoClasses)
				.setRandomChangeMode(ESRandomChangeMode.DELETE)
				.setTargetObject(ePackageWithTwoClasses)
				.setTargetFeature(E_PACKAGE.getEPackage_NsURI()).apply();

		assertNull(ePackageWithTwoClasses.getNsURI());
		assertFalse(ePackageWithTwoClasses
				.eIsSet(E_PACKAGE.getEPackage_NsURI()));
	}

	@Test
	public void selectTargetContainerForGivenFeature()
			throws ESMutationException {

		ESAttributeChangeMutation mutation = ESMutationFactory
				.attributeChange(utilForEPackageWithTwoClasses)
				.setRandomChangeMode(ESRandomChangeMode.ADD)
				.setTargetFeature(E_PACKAGE.getEClass_Abstract());

		mutation.apply();

		assertEquals(E_PACKAGE.getEClass(), mutation.getTargetObject().eClass());
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(
				mutation.getTargetObject()));
	}

	@Test
	public void selectTargetFeatureForGivenTargetContainer()
			throws ESMutationException {

		ESAttributeChangeMutation mutation = ESMutationFactory
				.attributeChange(utilForEPackageWithTwoClasses)
				.setRandomChangeMode(ESRandomChangeMode.ADD)
				.setTargetObject(ePackageWithTwoClasses);

		mutation.apply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EClass targetContainerClass = ePackageWithTwoClasses.eClass();
		final EList<EAttribute> allAttributes = targetContainerClass
				.getEAllAttributes();

		assertTrue(allAttributes.contains(targetFeature));
	}

	@Test
	public void addObject() throws ESMutationException {

		ESAttributeChangeMutation mutation = ESMutationFactory.attributeChange(
				utilForEPackageWithTwoClasses).setRandomChangeMode(
				ESRandomChangeMode.ADD);

		mutation.apply();

		assertNotNull(mutation.getTargetObject());
		assertNotNull(mutation.getTargetFeature());
		assertThat(mutation.getTargetFeature(), instanceOf(EAttribute.class));
	}
}
