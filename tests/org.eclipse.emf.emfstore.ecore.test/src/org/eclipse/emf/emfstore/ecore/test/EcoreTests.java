/*******************************************************************************
 * Copyright (c) 2012-2016 EclipseSource Muenchen GmbH and others.
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
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUser;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Delete;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.common.model.ESModelElementId;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.ChecksumErrorHandler;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <b>NOTE</b>: These tests are not part of the regular test suite for the time being.
 * In order to execute them, make sure you have running EMFStore server instance in the
 * background with the EMFStore Ecore-Plugin being part of the running configurartion.
 */
public class EcoreTests extends ESTestWithLoggedInUser {

	static final NullProgressMonitor NPM = new NullProgressMonitor();
	static final String PACKAGE_NAME = "myPackage"; //$NON-NLS-1$
	static final String CHECKOUT_NAME = "testCheckout"; //$NON-NLS-1$
	static final String ATTRIBUTE_NAME = "foo"; //$NON-NLS-1$

	@SuppressWarnings("restriction")
	@BeforeClass
	public static void beforeClass() {
		Configuration.getClientBehavior().setChecksumErrorHandler(ChecksumErrorHandler.LOG_DETAILED_AND_CANCEL);
	}

	@Test
	public void addEClass() throws SerializationException, ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = checkoutCopy();

		final ESPrimaryVersionSpec base = getLocalProject().getBaseVersion();
		final EPackage ePackage = ECreate.ePackage("mypackage"); //$NON-NLS-1$
		final EClass eClass = ECreate.eClass("A"); //$NON-NLS-1$
		final EAttribute eAttr = ECreate.eAttr(ATTRIBUTE_NAME, ECreate.eBooleanType());
		EAdd.to(eClass, eAttr);
		EAdd.to(ePackage, eClass);
		Add.toProject(getLocalProject(), ePackage);

		final ESPrimaryVersionSpec head = getLocalProject().commit(new NullProgressMonitor());
		ProjectUtil.update(copy);

		final ESModelElementId attrId = getLocalProject().getModelElementId(eAttr);
		final EAttribute copiedAttribute = (EAttribute) copy.getModelElement(attrId);

		assertNotSame(base, head);
		assertEquals(copiedAttribute.getEType(), EcorePackage.eINSTANCE.getEBooleanObject());
		assertNotNull(copiedAttribute);
	}

	@Test
	public void setESuperType() throws SerializationException, ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = getLocalProject().getRemoteProject().checkout(CHECKOUT_NAME,
			NPM);

		getLocalProject().getBaseVersion();
		final EPackage ePackage = ECreate.ePackage("mypackage"); //$NON-NLS-1$
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		EAdd.to(ePackage, clsA);

		Add.toProject(getLocalProject(), ePackage);
		getLocalProject().commit(NPM);

		final EClass clsB = ECreate.eClass("B"); //$NON-NLS-1$
		EAdd.to(ePackage, clsB);
		// set B as super type of A
		EUpdate.superType(clsA, clsB);

		final ESPrimaryVersionSpec head = getLocalProject().commit(NPM);
		copy.update(NPM);

		// commit must succeed
		assertEquals(2, head.getIdentifier());
		final ESModelElementId modelElementId = getLocalProject().getModelElementId(clsA);
		final EClass copiedClsA = (EClass) copy.getModelElement(modelElementId);
		assertEquals(copiedClsA.getESuperTypes().size(), 1);
	}

	@Test
	public void changeESuperType() throws SerializationException, ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = checkoutCopy();

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EClass clsB = ECreate.eClass("B"); //$NON-NLS-1$
		final EClass clsC = ECreate.eClass("C"); //$NON-NLS-1$
		EAdd.to(pkg, clsA, clsB, clsC);
		EUpdate.superType(clsA, clsB);
		Add.toProject(getLocalProject(), pkg);
		getLocalProject().commit(new NullProgressMonitor());

		// change super type from A to C
		EUpdate.superType(clsA, clsC);

		getLocalProject().commit(new NullProgressMonitor());
		copy.update(NPM);

		assertTrue(ModelUtil.areEqual(
			asProject(getLocalProject()),
			asProject(copy)));
	}

	@Test
	public void deleteESuperType() throws SerializationException, ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = checkoutCopy();

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EClass clsB = ECreate.eClass("B"); //$NON-NLS-1$
		EUpdate.superType(clsA, clsB);
		EAdd.to(pkg, clsA, clsB);
		Add.toProject(getLocalProject(), pkg);
		getLocalProject().commit(NPM);

		EDelete.superType(clsA);
		getLocalProject().commit(NPM);

		copy.update(NPM);

		assertTrue(clsA.getESuperTypes().isEmpty());
		assertTrue(ModelUtil.areEqual(
			asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void changeEAttribute() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = checkoutCopy();

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EAttribute eAttr = ECreate.eAttr(ATTRIBUTE_NAME, ECreate.eBooleanType());
		EAdd.to(clsA, eAttr);
		EAdd.to(pkg, clsA);
		Add.toProject(getLocalProject(), pkg);

		EUpdate.eAttr(eAttr, ECreate.eStringType());

		getLocalProject().commit(NPM);
		copy.update(NPM);

		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void changeEReference() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = getLocalProject()
			.getRemoteProject()
			.checkout(CHECKOUT_NAME, NPM);

		final EPackage ePackage = ECreate.ePackage("mypackage"); //$NON-NLS-1$
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EClass clsB = ECreate.eClass("B"); //$NON-NLS-1$
		final EClass clsC = ECreate.eClass("C"); //$NON-NLS-1$
		final EReference eRef = ECreate.eRef("a", clsA); //$NON-NLS-1$
		EAdd.to(ePackage, clsA, clsB, clsC);
		EAdd.to(clsC, eRef);

		Add.toProject(getLocalProject(), ePackage);
		// change type of ref from A to B
		EUpdate.eRef(eRef, clsB);
		getLocalProject().commit(NPM);

		copy.update(NPM);

		assertEquals(3, ePackage.getEClassifiers().size());
		assertEquals(clsB, eRef.getEType());
		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void deleteEReference() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = getLocalProject()
			.getRemoteProject()
			.checkout(CHECKOUT_NAME, NPM);

		final EPackage ePackage = ECreate.ePackage("mypackage"); //$NON-NLS-1$
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EClass clsB = ECreate.eClass("B"); //$NON-NLS-1$
		final EReference eRef = ECreate.eRef("a", clsA); //$NON-NLS-1$
		EAdd.to(ePackage, clsA, clsB);
		EAdd.to(clsB, eRef);

		Add.toProject(getLocalProject(), ePackage);
		Delete.fromProject(eRef);
		getLocalProject().commit(NPM);

		copy.update(NPM);

		assertEquals(2, ePackage.getEClassifiers().size());
		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void changeELiteralOfEAttribute() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = checkoutCopy();

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);

		final EEnum eEnum = ECreate.eEnum("AB"); //$NON-NLS-1$
		final EEnumLiteral a = ECreate.eLiteral("A", 0); //$NON-NLS-1$
		final EEnumLiteral b = ECreate.eLiteral("B", 1); //$NON-NLS-1$
		EAdd.to(eEnum, a, b);

		final EEnum anotherEEnum = ECreate.eEnum("CD"); //$NON-NLS-1$
		final EEnumLiteral c = ECreate.eLiteral("C", 0); //$NON-NLS-1$
		final EEnumLiteral d = ECreate.eLiteral("D", 1); //$NON-NLS-1$
		EAdd.to(anotherEEnum, c, d);

		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EAttribute eAttr = ECreate.eAttr("myAttr", eEnum); //$NON-NLS-1$
		EAdd.to(clsA, eAttr);
		EAdd.to(pkg, clsA, eEnum, anotherEEnum);

		Add.toProject(getLocalProject(), pkg);
		getLocalProject().commit(NPM);

		EUpdate.eAttr(eAttr, anotherEEnum);

		getLocalProject().commit(NPM);
		copy.update(NPM);

		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void deleteEEnumOfEReference() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = getLocalProject()
			.getRemoteProject()
			.checkout(CHECKOUT_NAME, NPM);

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EEnum eEnum = ECreate.eEnum("E"); //$NON-NLS-1$
		final EAttribute eAttr = ECreate.eAttr("myAttr", eEnum); //$NON-NLS-1$
		EAdd.to(eEnum, ECreate.eLiteral("L", 0)); //$NON-NLS-1$
		EAdd.to(clsA, eAttr);
		EAdd.to(pkg, clsA, eEnum);
		Add.toProject(getLocalProject(), pkg);

		getLocalProject().commit(NPM);

		Delete.fromProject(eEnum);

		getLocalProject().commit(NPM);
		copy.update(NPM);

		assertEquals(1, pkg.getEClassifiers().size());
		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void deleteEClass() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = getLocalProject()
			.getRemoteProject()
			.checkout(CHECKOUT_NAME, NPM);

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		EAdd.to(pkg, clsA);
		Add.toProject(getLocalProject(), pkg);

		getLocalProject().commit(NPM);

		Delete.fromProject(clsA);

		getLocalProject().commit(NPM);
		copy.update(NPM);

		assertEquals(0, pkg.getEClassifiers().size());
		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	@Test
	public void deleteEClassOfEReference() throws ESException {
		share(getUsersession(), getLocalProject());
		final ESLocalProject copy = getLocalProject()
			.getRemoteProject()
			.checkout(CHECKOUT_NAME, NPM);

		final EPackage pkg = ECreate.ePackage(PACKAGE_NAME);
		final EClass clsA = ECreate.eClass("A"); //$NON-NLS-1$
		final EClass clsB = ECreate.eClass("B"); //$NON-NLS-1$
		final EReference eRef = ECreate.eRef("aRef", clsA); //$NON-NLS-1$
		EAdd.to(clsB, eRef);
		EAdd.to(pkg, clsA, clsB);
		Add.toProject(getLocalProject(), pkg);
		getLocalProject().commit(NPM);

		Delete.fromProject(clsA);

		getLocalProject().commit(NPM);
		copy.update(NPM);

		assertEquals(1, pkg.getEClassifiers().size());
		assertTrue(
			ModelUtil.areEqual(asProject(getLocalProject()), asProject(copy)));
	}

	public void run(final Runnable runnable) {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				runnable.run();
			}
		}.run(false);
	}

	public ESLocalProject checkoutCopy() throws ESException {
		return getLocalProject().getRemoteProject().checkout(CHECKOUT_NAME, NPM);
	}

	@SuppressWarnings("restriction")
	private Project asProject(ESLocalProject project) {
		return ESLocalProjectImpl.class.cast(project).toInternalAPI().getProject();
	}
}
