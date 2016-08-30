/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.ecore.test;

import static org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil.share;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EcoreTests extends ESTestWithLoggedInUser {

	private static final String CHECKOUT_NAME = "testCheckout"; //$NON-NLS-1$
	private static final String ATTRIBUTE_NAME = "foo"; //$NON-NLS-1$

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Test
	public void roundTripWithEcore() throws SerializationException, ESException {
		final NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
		share(getUsersession(), getLocalProject());

		final ESLocalProject checkedoutCopy = getLocalProject().getRemoteProject().checkout(CHECKOUT_NAME,
			nullProgressMonitor);

		try {
			final ESPrimaryVersionSpec base = getLocalProject().getBaseVersion();
			Add.toProject(getLocalProject(), createEPackageWithSimpleClass());

			final ESPrimaryVersionSpec head = getLocalProject().commit(new NullProgressMonitor());
			ProjectUtil.update(checkedoutCopy);
			final EAttribute copiedAttribute = findAttribute(checkedoutCopy);

			assertNotSame(base, head);
			assertEquals(copiedAttribute.getEType(), EcorePackage.eINSTANCE.getEBooleanObject());
			assertNotNull(copiedAttribute);
		} catch (final ESException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Creates an {@link EPackage} containing a single class with a simple boolean
	 * data type.
	 *
	 * @return
	 */
	private EPackage createEPackageWithSimpleClass() {
		final EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("mypackage"); //$NON-NLS-1$

		final EClass cls = EcoreFactory.eINSTANCE.createEClass();
		cls.setName("MyClass"); //$NON-NLS-1$

		final EStructuralFeature attr = EcoreFactory.eINSTANCE.createEAttribute();
		attr.setName(ATTRIBUTE_NAME);
		attr.setEType(EcorePackage.eINSTANCE.getEBooleanObject());

		cls.getEStructuralFeatures().add(attr);
		ePackage.getEClassifiers().add(cls);
		return ePackage;
	}

	private EAttribute findAttribute(ESLocalProject copy) {
		final EPackage copiedPackage = (EPackage) copy.getModelElements().get(0);
		final EClass copiedClass = (EClass) copiedPackage.getEClassifiers().get(0);
		final EList<EStructuralFeature> eStructuralFeatures = copiedClass.getEStructuralFeatures();
		EAttribute copiedAttribute = null;

		for (final EStructuralFeature eStructuralFeature : eStructuralFeatures) {
			if (eStructuralFeature.getName().equals(ATTRIBUTE_NAME)) {
				copiedAttribute = (EAttribute) eStructuralFeature;
			}
		}

		return copiedAttribute;
	}
}
