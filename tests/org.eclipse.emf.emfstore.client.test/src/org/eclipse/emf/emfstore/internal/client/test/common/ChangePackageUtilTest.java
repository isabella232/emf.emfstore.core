/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.test.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTestWithLoggedInUserMock;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.test.common.util.ProjectUtil;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackageEnvelope;
import org.eclipse.emf.emfstore.internal.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.ChangePackageUtil;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

/**
 * @author emueller
 *
 */
public class ChangePackageUtilTest extends ESTestWithLoggedInUserMock {

	private Optional<Integer> fragmentationSize;

	@BeforeClass
	public static void beforeClass() {
		startEMFStore();
	}

	@AfterClass
	public static void afterClass() {
		stopEMFStore();
	}

	@Override
	public void before() {
		super.before();
		fragmentationSize = Configuration.getClientBehavior().getChangePackageFragmentSize();
		Configuration.getClientBehavior().setChangePackageFragmentSize(Optional.of(1));
	}

	@Override
	@After
	public void after() {
		Configuration.getClientBehavior().setChangePackageFragmentSize(fragmentationSize);
	}

	@Test
	public void shouldEmitAtLeastOneElement() throws ESException {
		final ChangePackage changePackage = VersioningFactory.eINSTANCE.createChangePackage();
		final Iterator<ChangePackageEnvelope> it = ChangePackageUtil.splitChangePackage(changePackage, 1);

		assertTrue(it.hasNext());
		final ChangePackageEnvelope next = it.next();

		assertFalse(it.hasNext());
		assertEquals(0, next.getFragmentIndex());
		assertEquals(1, next.getFragmentCount());
	}

	@Test
	public void shouldAssertThatCallingHasNextOnTheReturnedIteratorHasNoEffect() throws ESException {
		ProjectUtil.share(getUsersession(), getLocalProject());
		final TestElement foo = Create.testElement("foo");
		final TestElement bar = Create.testElement("bar");
		final TestElement baz = Create.testElement("baz");

		Add.toProject(getLocalProject(), foo);
		Add.toProject(getLocalProject(), bar);
		Add.toProject(getLocalProject(), baz);

		final AbstractChangePackage localChangePackage = getProjectSpace().getLocalChangePackage();
		final Iterator<ChangePackageEnvelope> it = ChangePackageUtil.splitChangePackage(localChangePackage, 2);

		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
		assertTrue(it.hasNext());
	}

	@Test
	public void shouldSplitOperationsIntoChunks() throws ESException {
		ProjectUtil.share(getUsersession(), getLocalProject());
		final TestElement foo = Create.testElement("foo");
		final TestElement bar = Create.testElement("bar");
		final TestElement baz = Create.testElement("baz");

		Add.toProject(getLocalProject(), foo);
		Add.toProject(getLocalProject(), bar);
		Add.toProject(getLocalProject(), baz);

		final AbstractChangePackage localChangePackage = getProjectSpace().getLocalChangePackage();
		final Iterator<ChangePackageEnvelope> it = ChangePackageUtil.splitChangePackage(localChangePackage, 2);

		assertTrue(it.hasNext());
		assertEquals(2, it.next().getFragment().size());
		assertTrue(it.hasNext());
		assertEquals(1, it.next().getFragment().size());
		assertFalse(it.hasNext());
	}

	@Test
	public void shouldSetHasNextFlagCorreclty() throws ESException {
		ProjectUtil.share(getUsersession(), getLocalProject());
		final TestElement foo = Create.testElement("foo");
		final TestElement bar = Create.testElement("bar");
		final TestElement baz = Create.testElement("baz");

		Add.toProject(getLocalProject(), foo);
		Add.toProject(getLocalProject(), bar);
		Add.toProject(getLocalProject(), baz);

		final AbstractChangePackage localChangePackage = getProjectSpace().getLocalChangePackage();
		final Iterator<ChangePackageEnvelope> it = ChangePackageUtil.splitChangePackage(localChangePackage, 2);

		final ChangePackageEnvelope firstFragment = it.next();
		final ChangePackageEnvelope secondFragment = it.next();

		assertFalse(firstFragment.isLast());
		assertTrue(secondFragment.isLast());
	}

}
