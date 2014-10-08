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
import static org.junit.Assert.fail;
import static org.eclipse.emf.emfstore.internal.modelmutator.api.ModelMutatorUtil.getAllObjectsCount;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

/**
 * Unit tests for {@link AddObjectMutation}.
 * 
 * @author Philip Langer
 */
public class AddObjectMutationTest extends AbstractMutationTest {

	@Test
	public void addObjectForGivenFeatureAndContainer() {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.apply();

		assertEquals(3, ePackageWithTwoClasses.getEClassifiers().size());
	}
	
	@Test
	public void addObjectForGivenFeature() {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.apply();

		// we only have one possible target container with the given feature
		// so apply() should have added one new EClassifier to it
		assertEquals(3, ePackageWithTwoClasses.getEClassifiers().size());
	}
	
	@Test
	public void selectTargetContainerForGivenFeature() throws MutationException {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.doApply();

		// we only have one possible target container with the given feature
		assertEquals(ePackageWithTwoClasses, mutation.getTargetObject());
	}

	@Test
	public void selectTargetFeatureForGivenTargetContainer() throws MutationException {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.doApply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EClass targetContainerClass = ePackageWithTwoClasses.eClass();
		final EList<EReference> allContainmentFeatures = targetContainerClass.getEAllContainments();
		assertTrue(allContainmentFeatures.contains(targetFeature));
	}
	
	@Test
	public void selectTargetFeatureAndContainerForGivenEObjectToAdd() throws MutationException {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setEObjectToAdd(E_FACTORY.createEAttribute());
		mutation.doApply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EObject targetContainer = mutation.getTargetObject();
		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), targetFeature);
		assertEquals(E_PACKAGE.getEClass(), targetContainer.eClass());
		final EObject realContainer = mutation.getEObjectToAdd().eContainer();
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(realContainer));
	}

	@Test
	public void addObject() {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.apply();

		assertEquals(3, getAllObjectsCount(ePackageWithTwoClasses));
	}

	@Test
	public void addGivenObjectForGivenFeature() {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.apply();

		assertEquals(3, ePackageWithTwoClasses.getEClassifiers().size());
	}

	@Test
	public void throwsExceptionIfNoValidTargetContainerIsAvailable() {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEEnum_ELiterals());

		try {
			mutation.doApply();
			fail("Should have thrown a Mutation Exception, because there is "
					+ "no valid target container.");
		} catch (MutationException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void throwsExceptionIfSelectionOfTargetContainerIsImpossible() {
		AddObjectMutation mutation = new AddObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures());
		mutation.setTargetObject(ePackageWithTwoClasses);

		try {
			mutation.doApply();
			fail("Should have thrown a Mutation Exception, because there is "
					+ "no valid target container.");
		} catch (MutationException e) {
			assertTrue(true);
		}
	}

}
