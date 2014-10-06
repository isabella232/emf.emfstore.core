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
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Test;

/**
 * Unit tests for {@link DeleteObjectMutation}.
 * 
 * @author Philip Langer
 */
public class DeleteObjectMutationTest extends AbstractMutationTest {
	
	@Test
	public void deleteObjectForGivenFeatureAndGivenContainer() {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.apply();

		assertEquals(1, ePackageWithTwoClasses.getEClassifiers().size());
	}
	
	@Test
	public void deleteObjectForGivenFeature() {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.apply();

		// we only have one possible target container with the given feature
		// so apply() should have added one new EClassifier to it
		assertEquals(1, ePackageWithTwoClasses.getEClassifiers().size());
	}
	
	@Test
	public void selectTargetContainerForGivenFeature() throws MutationException {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.doApply();

		// we only have one possible target container with the given feature
		assertEquals(ePackageWithTwoClasses, mutation.getTargetObject());
	}

	@Test
	public void selectTargetFeatureForGivenObject() throws MutationException {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.doApply();

		final EStructuralFeature targetFeature = mutation.getTargetFeature();
		final EClass targetContainerClass = ePackageWithTwoClasses.eClass();
		final EList<EReference> allContainmentFeatures = targetContainerClass.getEAllContainments();
		assertTrue(allContainmentFeatures.contains(targetFeature));
	}

	@Test
	public void deleteObject() {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setMaxNumberOfContainments(1);
		mutation.apply();

		assertEquals(1, getAllObjectsCount(ePackageWithTwoClasses));
	}

	@Test
	public void deleteInGivenTargetContainerForGivenFeature() {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.setTargetObject(ePackageWithTwoClasses);
		mutation.setMaxNumberOfContainments(1);
		mutation.apply();

		assertEquals(1, ePackageWithTwoClasses.getEClassifiers().size());
	}

	@Test
	public void throwsExceptionIfNoValidTargetContainerIsAvailable() {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
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
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
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
	
	@Test
	public void throwsExceptionIfSelectionOfTargetContainerViolatesMaxNumberOfObjects() {
		DeleteObjectMutation mutation = new DeleteObjectMutation(utilForEPackageWithTwoClasses);
		mutation.setMaxNumberOfContainments(-1);
		mutation.setTargetFeature(E_PACKAGE.getEPackage_EClassifiers());
		mutation.setTargetObject(ePackageWithTwoClasses);

		try {
			mutation.doApply();
			fail("Should have thrown a Mutation Exception, because there this mutation "
					+ "would delete more than the specified maximum number of objects.");
		} catch (MutationException e) {
			assertTrue(true);
		}
	}

}
