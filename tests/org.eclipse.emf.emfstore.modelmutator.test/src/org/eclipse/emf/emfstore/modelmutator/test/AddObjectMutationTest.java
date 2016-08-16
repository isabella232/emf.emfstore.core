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

import static org.eclipse.emf.emfstore.modelmutator.ESModelMutatorUtil.getAllObjectsCount;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.modelmutator.ESAddObjectMutation;
import org.eclipse.emf.emfstore.modelmutator.ESMutationException;
import org.eclipse.emf.emfstore.modelmutator.ESMutationFactory;
import org.junit.Test;

/**
 * Unit tests for {@link ESAddObjectMutation}.
 * 
 * @author Philip Langer
 */
public class AddObjectMutationTest extends AbstractMutationTest {

	@Test
	public void addObjectForGivenFeatureAndContainer() throws ESMutationException {

		ESMutationFactory.add(utilForEPackageWithTwoClasses).setTargetObject(ePackageWithTwoClasses)
			.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers()).apply();

		assertEquals(3, ePackageWithTwoClasses.getEClassifiers().size());
	}

	@Test
	public void addObjectForGivenFeature() throws ESMutationException {

		ESMutationFactory.add(utilForEPackageWithTwoClasses).setTargetFeature(E_PACKAGE.getEPackage_EClassifiers())
			.apply();

		// we only have one possible target container with the given feature
		// so apply() should have added one new EClassifier to it
		assertEquals(3, ePackageWithTwoClasses.getEClassifiers().size());
	}

	@Test
	public void selectTargetContainerForGivenFeature() throws ESMutationException {

		ESAddObjectMutation mutation = ESMutationFactory.add(utilForEPackageWithTwoClasses)
			.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());

		mutation.apply();

		// we only have one possible target container with the given feature
		assertEquals(ePackageWithTwoClasses, mutation.getTargetObject());
	}

	@Test
	public void selectTargetFeatureForGivenTargetContainer() throws ESMutationException {

		ESAddObjectMutation mutation = ESMutationFactory.add(utilForEPackageWithTwoClasses)
			.setTargetObject(ePackageWithTwoClasses);

		mutation.apply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EClass targetContainerClass = ePackageWithTwoClasses.eClass();
		final EList<EReference> allContainmentFeatures = targetContainerClass.getEAllContainments();

		assertTrue(allContainmentFeatures.contains(targetFeature));
	}

	@Test
	public void selectTargetFeatureAndContainerForGivenEObjectToAdd() throws ESMutationException {

		ESAddObjectMutation mutation = ESMutationFactory.add(utilForEPackageWithTwoClasses)
			.setEObjectToAdd(E_FACTORY.createEAttribute());

		mutation.apply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EObject targetContainer = mutation.getTargetObject();
		final EObject realContainer = mutation.getEObjectToAdd().eContainer();

		assertEquals(E_PACKAGE.getEClass_EStructuralFeatures(), targetFeature);
		assertEquals(E_PACKAGE.getEClass(), targetContainer.eClass());
		assertTrue(ePackageWithTwoClasses.getEClassifiers().contains(realContainer));
	}

	@Test
	public void addObject() throws ESMutationException {

		ESMutationFactory.add(utilForEPackageWithTwoClasses).apply();

		assertEquals(3, getAllObjectsCount(ePackageWithTwoClasses));
	}

	@Test(expected = ESMutationException.class)
	public void throwsExceptionIfNoValidTargetContainerIsAvailable() throws ESMutationException {

		ESMutationFactory.add(utilForEPackageWithTwoClasses).setTargetFeature(E_PACKAGE.getEEnum_ELiterals()).apply();

		fail("Should have thrown a Mutation Exception, because there is no valid target container.");
	}

	@Test(expected = ESMutationException.class)
	public void throwsExceptionIfSelectionOfTargetContainerIsImpossible() throws ESMutationException {

		ESMutationFactory.add(utilForEPackageWithTwoClasses).setTargetFeature(E_PACKAGE.getEClass_EStructuralFeatures())
			.setTargetObject(ePackageWithTwoClasses).apply();

		fail("Should have thrown a Mutation Exception, because there is no valid target container.");
	}
}